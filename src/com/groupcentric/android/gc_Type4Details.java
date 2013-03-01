package com.groupcentric.android;

import java.util.ArrayList;

import com.groupcentric.api.model.GCImageItem;
import com.groupcentric.api.model.Message;
import com.groupcentric.util.ImageDownloader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class gc_Type4Details extends Activity implements OnClickListener {
	/**
	 * @author support@groupcentric.com <br>
	 *         Purpose:Profile Activity <br>
	 *         Notes: <br>
	 *         Revisions:
	 * 
	 * 
	 */
	private gc_Variables var;
	TextView txtEventName;
	TextView txtWS;
	TextView txtDetails;
	TextView txtDate;
	TextView txtSubtitle;
	ImageView imgEventPic;
    WebView desc_webview;
	ImageDownloader imagedownloader;
	Message eventMessage = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gc_activity_event);
		var = new gc_Variables(); //finish instantiating the GroupcentricVariables object which will give access to the Groupcentric UserID and APIKey

		if (getIntent().getExtras() != null) {
			if (getIntent().getParcelableExtra("event_message") != null) {
				eventMessage = getIntent().getParcelableExtra("event_message");

			}
		}

		txtEventName = (TextView) findViewById(R.id.txtEventName);
		txtWS = (TextView) findViewById(R.id.txt_website);
		txtDate = (TextView) findViewById(R.id.txtDate);
		txtSubtitle = (TextView) findViewById(R.id.txt_objsubtitle);
		desc_webview = (WebView) findViewById(R.id.desc_webview);

		imgEventPic = (ImageView) findViewById(R.id.img_eventPic);
		imgEventPic.setOnClickListener(this);
		findViewById(R.id.imgBack).setOnClickListener(this);
		findViewById(R.id.btn_notification).setOnClickListener(this);
		findViewById(R.id.img_ws).setOnClickListener(this);
		imagedownloader = new ImageDownloader();

		txtEventName.setText(eventMessage.getVar_title());
		if (eventMessage.getVar_details().length()==0)
			findViewById(R.id.lyt_details).setVisibility(View.GONE);
        desc_webview.getSettings().setJavaScriptEnabled(true);
        desc_webview.loadDataWithBaseURL(null, eventMessage.getVar_details(), "text/html", "UTF-8", null);
        
		
		txtWS.setText(eventMessage.getVar1());
		if (eventMessage.getVar1().length()== 0)
		{
			findViewById(R.id.lyt_website).setVisibility(View.GONE);
			findViewById(R.id.lyt_additional).setVisibility(View.GONE);
		}
		txtSubtitle.setText(eventMessage.getVar_subtitle());
		if (eventMessage.getVar_subtitle().length()==0)
			findViewById(R.id.lyt_objsubTitle).setVisibility(View.GONE);
		
		txtDate.setText(eventMessage.getVar_date());
		if (eventMessage.getVar_date().length()==0)
				findViewById(R.id.lyt_date).setVisibility(View.GONE);
		
		imagedownloader.download(eventMessage.getImageurl(), imgEventPic);

		final RelativeLayout lyt = (RelativeLayout) findViewById(R.id.lyt_userInfo);

		// Set the width\height of profile imageView to height row layout
		ViewTreeObserver vto = lyt.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				imgEventPic.getLayoutParams().height = lyt.getHeight();
				imgEventPic.getLayoutParams().width = lyt.getHeight();
			}

		});

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void onClick(View v) {
		if (v.getId() == R.id.imgBack) {
			finish(); // Go back
		} else if (v.getId() == R.id.btn_notification) {
			startActivity(new Intent(this, gc_NotificationsList.class));
			finish();
		} else if (v.getId() == R.id.img_eventPic) {
	        ArrayList<GCImageItem> gcImageItems = new ArrayList<GCImageItem>();
	        GCImageItem gcimageItem = new GCImageItem();
	        gcimageItem.setImgURL(eventMessage.getImageurl());
	        gcImageItems.add(gcimageItem);
	        Bundle b = new Bundle();
	        b.putParcelableArrayList("group_pics", gcImageItems); 
	        Intent i = new Intent(this, gc_PictureViewer.class);
	        i.putExtras(b); 
	        i.putExtra("SelectedPic", eventMessage.getImageurl());
	        startActivity(i);
		} else if (v.getId() == R.id.img_ws) {
	        try {
	            String strURL = eventMessage.getVar1();
	            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strURL));
	            startActivity(intent);
			} catch (Exception ex) {
				Toast.makeText(this, "Error loading website",Toast.LENGTH_SHORT).show();
			}
		}

	}
	
}
