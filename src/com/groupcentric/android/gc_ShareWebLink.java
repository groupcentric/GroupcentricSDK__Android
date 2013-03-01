package com.groupcentric.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * @author support@groupcentric.com <br>
 *         Purpose: Handles dynamic profile webview content <br>
 *         Notes: <br>
 *         Revisions:
 * 
 * 
 */
public class gc_ShareWebLink extends Activity implements OnClickListener {
	private gc_Variables var;
    WebView webview_share;
    TextView edtURL;
    EditText edtMessage;
    EditText edt_search_link_url;
    ImageButton imgBtnGo;
    ImageButton imgStop;
    ImageButton send_link;
    ProgressBar progURL;
    int iGroupID = 0;
    String strWebUrl = "";
    private static final int RC_UPLOAD_WS_IMAGE  = 150;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gc_activity_share_weblink);
		var = new gc_Variables(); //finish instantiating the GroupcentricVariables object which will give access to the Groupcentric UserID and APIKey
		webview_share = (WebView) findViewById(R.id.webView_share_link); 
		webview_share.setWebViewClient(new webClient());
		progURL = (ProgressBar) findViewById(R.id.prog_loadurl);
		edtURL = (TextView) findViewById(R.id.edt_link_url); 
		edtURL.setOnClickListener(this);
		edt_search_link_url = (EditText) findViewById(R.id.edt_search_link_url);
		edtMessage = (EditText) findViewById(R.id.edt_link_message);
		imgBtnGo = (ImageButton) findViewById(R.id.btn_go);
		imgStop = (ImageButton) findViewById(R.id.btn_stop);
		send_link = (ImageButton) findViewById(R.id.btn_send_link);
		send_link.setOnClickListener(this);
		imgBtnGo.setOnClickListener(this);
		imgStop.setOnClickListener(this);
        progURL.getIndeterminateDrawable().setColorFilter(Color.parseColor(getResources().getString(R.color.gc_font_white)), android.graphics.PorterDuff.Mode.MULTIPLY); 

        imgStop.setVisibility(View.GONE);
        if (savedInstanceState != null) {
        	strWebUrl = savedInstanceState.getString("web_url") ;
            }

        edt_search_link_url.setOnEditorActionListener(new OnEditorActionListener(){  
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
		
				if (EditorInfo.IME_ACTION_GO==actionId)
				{
					onClick(findViewById(R.id.btn_go));
				
				}
				return false;
			} 

         }); 
        edtMessage.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (edtMessage.getText().toString().trim().length() > 0) {
                    	onClick(findViewById(R.id.btn_send_link));
                    }
                    return true;
                }
                return false;
            }
        });
        
        
		if (getIntent().getExtras() != null) {
			if (getIntent().getExtras().getString("web_share_url") != null) {
				strWebUrl = getIntent().getExtras().getString("web_share_url");
				getIntent().removeExtra("web_share_url");
			}
			
			iGroupID = getIntent().getExtras().getInt("group_id");
			

		}
		webview_share.getSettings().setJavaScriptEnabled(true);
		if (!URLUtil.isValidUrl(strWebUrl))
		{
			edtURL.setText(strWebUrl);
			edt_search_link_url.setText(edtURL.getText().toString());
			findViewById(R.id.search_bar).setVisibility(View.VISIBLE);
			edt_search_link_url.requestFocus();
		}
		else
		{
			webview_share.loadUrl(strWebUrl);
			edtURL.setText(strWebUrl);
			edt_search_link_url.setText(strWebUrl);
		}
		
		//Corrects issue where webview site with input box not accepting foucus
		webview_share.setOnTouchListener(new View.OnTouchListener() { 
			public boolean onTouch(View v, MotionEvent event) {
			           switch (event.getAction()) { 
			               case MotionEvent.ACTION_DOWN: 
			               case MotionEvent.ACTION_UP: 
			                   if (!v.hasFocus()) { 
			                       v.requestFocus(); 
			                   } 
			                   break; 
			           } 
			           return false; 
			        }
			});

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == RC_UPLOAD_WS_IMAGE) {
				int iUploadStatus = data.getExtras().getInt("upload_complete",0);
				if (iUploadStatus ==1)
					finish();
				else
				{
					send_link.setEnabled(true);
					Toast.makeText(this, "There was a problem shareing your link", Toast.LENGTH_SHORT).show();
				}
					
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("web_url", strWebUrl);
		super.onSaveInstanceState(outState);
	}

	public void onClick(View v) {

		if (v.getId() == R.id.btn_go) {
			String strEditURL = edt_search_link_url.getText().toString();
			if (Patterns.WEB_URL.matcher(strEditURL).matches())  {
				findViewById(R.id.search_bar).setVisibility(View.GONE);
				webview_share.loadUrl(URLUtil.guessUrl(strEditURL));
				edt_search_link_url.setText(URLUtil.guessUrl(strEditURL));
		        InputMethodManager imm = (InputMethodManager) gc_ShareWebLink.this.getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.hideSoftInputFromWindow(edt_search_link_url.getWindowToken(), 0);
				edtMessage.requestFocus();
			}
				else {
				Toast.makeText(this, "Invalid Web Address", Toast.LENGTH_SHORT).show();
				}
					
		} 
		
		if (v.getId() == R.id.btn_send_link) {
			if (Patterns.WEB_URL.matcher(edtURL.getText().toString()).matches()) 
				captureWebView();
			else {
				Toast.makeText(this, "Invalid Web Address", Toast.LENGTH_SHORT).show();
			}
		}
		
		if (v.getId() == R.id.edt_link_url){
			edt_search_link_url.setText(edtURL.getText().toString());
			findViewById(R.id.search_bar).setVisibility(View.VISIBLE);
			edt_search_link_url.requestFocus();
		}
		
		if (v.getId()== R.id.btn_stop) {
			webview_share.stopLoading();
			Toast.makeText(this, "Stopping..", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void captureWebView() {
		send_link.setEnabled(false);
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtMessage.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edt_search_link_url.getWindowToken(), 0);
		
        webview_share.stopLoading();
        
		// determine if we can access storage card
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {

		} else {
			send_link.setEnabled(true);
			Toast.makeText(gc_ShareWebLink.this,"Can't currently access SD card ", Toast.LENGTH_LONG)
					.show();
			return;
		}
		
		if (checkGCDirectory()==false) {
			return;
		}
		
		String mPath = Environment.getExternalStorageDirectory() + File.separator + var.gcImageDir + File.separator + "tmp_share_url.png";
		

		webview_share.setDrawingCacheEnabled(true);

		Bitmap bitmap = Bitmap.createBitmap(webview_share.getDrawingCache());
		webview_share.setDrawingCacheEnabled(false);
		FileOutputStream fout = null;
		File imageFile = new File(mPath);
		int fileSize = 0;
		try {
		    fout = new FileOutputStream(imageFile);
		    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
		    fileSize = (int) imageFile.length();
		    fout.flush();
		    fout.close();

		} catch (FileNotFoundException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

		Uri attachedWSImage = Uri.fromFile(new File(mPath));

	
		boolean compressUpload = false;
		if (fileSize>1048576)
			compressUpload = true;
		
		
		
		if (attachedWSImage!=null)
		{

		Intent intent = new Intent(this, gc_UploadImage.class); 
	    	   intent.putExtra("imageUri", attachedWSImage);
	    	   intent.putExtra("msg", edtMessage.getText().toString().trim());
	    	   intent.putExtra("imagetype", "W");
	    	   intent.putExtra("website", edtURL.getText().toString());
	    	   intent.putExtra("groupid",iGroupID);
	    	   intent.putExtra("InSample", compressUpload);
	    	   startActivityForResult(intent, RC_UPLOAD_WS_IMAGE);
	       return;
		}
		
	}
	
    public class webClient extends WebViewClient {

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
            Toast.makeText(gc_ShareWebLink.this, "Error Loading page..", Toast.LENGTH_LONG).show();
            progURL.setVisibility(View.GONE);
            imgStop.setVisibility(View.GONE);
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
			strWebUrl = url;
            edtURL.setText(url);
            progURL.getIndeterminateDrawable().setColorFilter(Color.parseColor(getResources().getString(R.color.gc_font_white)), android.graphics.PorterDuff.Mode.MULTIPLY); 
            progURL.setVisibility(View.VISIBLE);
            imgStop.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
			strWebUrl = url;
            edtURL.setText(url);
            progURL.setVisibility(View.GONE);
            imgStop.setVisibility(View.GONE);

        }

    }
    private boolean checkGCDirectory() {
		File folder = new File(Environment.getExternalStorageDirectory() + File.separator + var.gcImageDir);
		boolean bHasGCDirectory = false;
		folder.delete();  //This will delete the directory if empty
		if (!folder.exists()) {
		    folder.mkdir();
		}
	
		if (folder.exists()) {
			bHasGCDirectory = true;
		} else {
			Toast.makeText(gc_ShareWebLink.this,"Error creating directory ", Toast.LENGTH_SHORT).show();
			bHasGCDirectory = false;
		}
		return bHasGCDirectory;
    }
	

}
