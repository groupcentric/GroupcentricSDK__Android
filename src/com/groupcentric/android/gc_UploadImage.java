package com.groupcentric.android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.view.WindowManager;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.groupcentric.util.Base64;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ProgressBar;
import android.widget.Toast;

public class gc_UploadImage extends Activity {
	/**
	 * @author support@groupcentric.com
	 *	<br>
	 * Purpose:Upload Images To GroupCentric
	 * <br>
	 * Notes: 
	 * <br>
	 * Revisions:
	 * 
	 *
	 */
    private static final int CAMERA_PICTURE = 2;
    String path = "";
    private String selectedImageFileName;
    java.io.InputStream inputStream;
    private String userID = "";
    private String groupID = "";
    private String imageType ="";
    private String msg ="";
    private String website = "";
    private Uri myImageUri;
    private boolean useInSample = true;
    private gc_Variables var;

   public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gc_activity_upload_photo);
        var = new gc_Variables(); //finish instantiating the GroupcentricVariables object which will give access to the Groupcentric UserID and APIKey
        userID = Integer.toString(var.getGroupcentricUserID(this));
        ((ProgressBar) findViewById(R.id.prg_upload_photo)).getIndeterminateDrawable().setColorFilter(Color.parseColor(getResources().getString(R.color.gc_blue)), android.graphics.PorterDuff.Mode.MULTIPLY); 

        WindowManager.LayoutParams lp = getWindow().getAttributes(); 
        lp.dimAmount = 0.5f; 
        getWindow().setAttributes(lp); 
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        if (getIntent().getExtras().getParcelable("imageUri") != null) {
           	myImageUri =  getIntent().getParcelableExtra("imageUri");
         	selectedImageFileName = getImageFilename();
            int		igroupID  =  getIntent().getExtras().getInt("groupid");
            groupID = String.valueOf(igroupID);
            if (getIntent().getExtras().getString("msg") != null) 
            	msg = getIntent().getExtras().getString("msg");
            imageType = getIntent().getExtras().getString("imagetype");
            
            if (getIntent().getExtras().getString("website") != null) 
            	website = getIntent().getExtras().getString("website");
            
            if (getIntent().getExtras().containsKey("InSample") )
            	useInSample = getIntent().getExtras().getBoolean("InSample");

            
            UploadPhotoTask task = new UploadPhotoTask();
            task.execute(new String[]{""});
        }
    	else
    	{
    		finish();
    	}
    	
    } // End on create

   public String getImageFilename() {
		String strFileName = "tmpImageName";
		Cursor cursor = this.getContentResolver().query(myImageUri, null, null,
				null, null);
		if (cursor == null) { //
			File imgFileName = new File(myImageUri.getPath());
			strFileName = imgFileName.getName();
		} else {
			cursor.moveToFirst();
			int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
			try {
				File aFile = new File(cursor.getString(idx));
				strFileName = aFile.getName();
			} catch (Exception ex) {

			}
		}

		return strFileName;
   }
    public String convertResponseToString(HttpResponse response)
        throws IllegalStateException, IOException {
        String res = "";
        StringBuffer buffer = new StringBuffer();
        inputStream = response.getEntity().getContent();

        int contentLength = (int) response.getEntity().getContentLength(); 
        if (contentLength < 0) {
        } else {
            byte[] data = new byte[512];
            int len = 0;
            try {
                while (-1 != (len = inputStream.read(data))) {
                    buffer.append(new String(data, 0, len)); 
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close(); // closing the stream
            } catch (IOException e) {
                e.printStackTrace();
            }
            res = buffer.toString(); // converting stringbuffer to string
        }
        return res;
    }



    private class UploadPhotoTask extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
        }

        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(String... args) {

            BitmapFactory.Options b = new BitmapFactory.Options();
            if (useInSample)
            	b.inSampleSize = 4;
           // Bitmap bitmap = BitmapFactory.decodeFile(args[0], b);
            Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeStream(gc_UploadImage.this.getContentResolver().openInputStream(myImageUri),null,b);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream); 
            // to which format you want.
            byte[] byte_arr = stream.toByteArray();
            String image_str = Base64.encodeBytes(byte_arr);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("image", image_str));
            nameValuePairs.add(new BasicNameValuePair("imagetype", imageType));
            nameValuePairs.add(new BasicNameValuePair("filename", selectedImageFileName));
            nameValuePairs.add(new BasicNameValuePair("userid", userID));
            nameValuePairs.add(new BasicNameValuePair("groupid", groupID));
            nameValuePairs.add(new BasicNameValuePair("msg", msg));
            nameValuePairs.add(new BasicNameValuePair("website",website));
            Calendar now = Calendar.getInstance();
            nameValuePairs.add(new BasicNameValuePair("devicedate",formatDateTime(now.getTime())));
            String serverResponse = "";

            try {

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://www.groupcentric.com/mobileImageUpload.ashx");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                serverResponse = convertResponseToString(response);
            } catch (Exception e) {
                serverResponse = e.getMessage();
            }
            return serverResponse;
        }

        // can use UI thread here

        protected void onPostExecute(String strResult) {
            if (strResult.contentEquals("1") == true) {
                Toast.makeText(gc_UploadImage.this, "Upload complete",Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK, new Intent().putExtra("upload_complete" , 1));
                finish();
            } else {
                Toast.makeText(gc_UploadImage.this,
                        "There was a problem uploading your photo" ,
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        
        String formatDateTime(Date date) {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            return dateTimeFormat.format(date);
        }
    }

}