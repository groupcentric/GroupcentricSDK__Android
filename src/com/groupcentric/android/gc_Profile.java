package com.groupcentric.android;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import com.groupcentric.api.BaseTaskGCActivity;
import com.groupcentric.api.model.GCProfile;
import com.groupcentric.api.ws.BaseTask;
import com.groupcentric.api.ws.WS21GetUserProfile;
import com.groupcentric.util.GroupcentricReadWritePrefs;
import com.groupcentric.util.ImageDownloader;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class gc_Profile extends BaseTaskGCActivity implements OnClickListener {
	/**
	 * @author support@groupcentric.com <br>
	 *         Purpose:Profile Activity <br>
	 *         Notes: <br>
	 *         Revisions:
	 * 
	 * 
	 */
	private gc_Variables var;
	TextView txtUserName;
	ImageView imgProfilePic;
    ImageDownloader imagedownloader;
    private static final int RC_SELECT_PICTURE           = 40;
    private static final int RC_TAKE_PICTURE      		 = 50;
    private static final int UPLOADED_IMAGE 		     = 100;
    private static final int RC_CHANGE_PROFILE_PICTURE   = 120;
    private static final int RC_SELECTED_PROFILE_PICTURE = 130;
    private static final int  RC_TAKE_PROFILE_PICTURE    = 140;

    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gc_activity_profile);
		var = new gc_Variables(); //finish instantiating the GroupcentricVariables object which will give access to the Groupcentric UserID and APIKey
		findViewById(R.id.img_settings).setOnClickListener(this);
		findViewById(R.id.img_feedback).setOnClickListener(this);
		findViewById(R.id.img_about).setOnClickListener(this);
		findViewById(R.id.img_tos).setOnClickListener(this);
		findViewById(R.id.imgBack).setOnClickListener(this);
		findViewById(R.id.img_logout).setOnClickListener(this);
		findViewById(R.id.lyt_feedback).setOnClickListener(this);
		findViewById(R.id.lyt_about).setOnClickListener(this);
		findViewById(R.id.lyt_tos).setOnClickListener(this);
		findViewById(R.id.lyt_settings).setOnClickListener(this);
		findViewById(R.id.lyt_exit).setOnClickListener(this);
		
		txtUserName = (TextView) findViewById(R.id.txtUserName);
		imgProfilePic = (ImageView) findViewById(R.id.img_smallProfilePic);
		imagedownloader = new ImageDownloader();
		imgProfilePic.setOnClickListener(this);

		final RelativeLayout lyt = (RelativeLayout) findViewById(R.id.lyt_userInfo);

		//Set the width\height of profile imageView to height row layout
		ViewTreeObserver vto = lyt.getViewTreeObserver(); 
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				imgProfilePic.getLayoutParams().height =  lyt.getHeight();
				imgProfilePic.getLayoutParams().width = lyt.getHeight();
			} 
		 
		}); 

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == RC_CHANGE_PROFILE_PICTURE) {
				if (data.getIntExtra("attchment_type", 0)==RC_SELECT_PICTURE)  //User wants to select from gallery
					selectProfilePicture();
				else if (data.getIntExtra("attchment_type", 0)== RC_TAKE_PICTURE)
						takeCameraPhoto(); 
			}
			
			
			if (requestCode == RC_SELECTED_PROFILE_PICTURE) {
					uploadSelectedProfilePicture(data.getData());
			}
			
			if (requestCode == UPLOADED_IMAGE) {
				int iUploadStatus = data.getExtras().getInt("upload_complete",0);
				if (iUploadStatus ==1)
					getMyProfile();
			}
			
			if (requestCode ==RC_TAKE_PROFILE_PICTURE) {
				try {
					File dir = Environment.getExternalStorageDirectory();
					GroupcentricReadWritePrefs rwPrefs = new GroupcentricReadWritePrefs();
					String strPhotoFileName = rwPrefs.readWriteUserSetting(
							this, "Read", "photo_filename", "");
					File yourFile = new File(dir + File.separator + var.gcImageDir, strPhotoFileName);
					if (!yourFile.exists()) {
						Toast.makeText(this,"Error retrieving photo", Toast.LENGTH_SHORT).show();
						return;
					}
					Uri imageUri = Uri.fromFile(yourFile);
					uploadSelectedProfilePicture(imageUri);
				} catch (Exception ex) {
					Toast.makeText(this,"Error "+ ex.getMessage(), Toast.LENGTH_SHORT).show();
				}	
			}
			
			}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void selectProfilePicture() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, RC_SELECTED_PROFILE_PICTURE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// get User Profile
		getMyProfile();
	}

	public void onClick(View v) {
		if (v.getId() == R.id.imgBack) {
			finish(); // Go back
		} else if (v.getId()==R.id.img_logout | v.getId()==R.id.lyt_exit) {
			logout();
		} else if (v.getId() == R.id.img_smallProfilePic){
			startActivityForResult(new Intent(this, gc_AttachItem.class)
			.putExtra("select_for_type", "profile")
			,RC_CHANGE_PROFILE_PICTURE
	         );
		} else if (v.getId() == R.id.img_settings | v.getId() == R.id.lyt_settings) {
			openProfileWebiew(this.getResources().getString(R.string.gc_profile_setteings) + "?uid="+ Integer.toString(var.getGroupcentricUserID(this)),"My Groupcentric Profile");
		} else if (v.getId() == R.id.img_feedback | v.getId() == R.id.lyt_feedback) {
			openProfileWebiew(this.getResources().getString(R.string.gc_profile_feedback) + "?uid="+ Integer.toString(var.getGroupcentricUserID(this)),"Groupcentric Feedback");
		} else if (v.getId() == R.id.img_about | v.getId() ==R.id.lyt_about) {
			openProfileWebiew(this.getResources().getString(R.string.gc_profile_about),"About Groupcentric");
		} else if (v.getId() == R.id.img_tos | v.getId() == R.id.lyt_tos) {
			openProfileWebiew(this.getResources().getString(R.string.gc_profile_tos),"Groupcentric TOS");
		} 
			
	}
	
	private void openProfileWebiew(String webURL, String header) {
        startActivity(new Intent(this, gc_ProfileWebView.class)
        .putExtra("web_url", webURL)
        .putExtra("header_text", header)
    );
	}

	public void onSuccess(BaseTask<?, ?, ?> task) {
		// Profile result
		if (task instanceof WS21GetUserProfile) {
			GCProfile gcprofile = ((WS21GetUserProfile) task).getActualResult();
			if (gcprofile != null) {
				txtUserName.setText(gcprofile.getName());
				imagedownloader.download(gcprofile.getPic(), imgProfilePic);
			} else
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.gc_login_fail),
						Toast.LENGTH_SHORT).show();
		}
	}

	private void getMyProfile() {
		startTask(new WS21GetUserProfile(this, var.getGroupcentricUserID(this), var.getGroupcentricAPIKey(this)));
	}

    public void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getResources().getString(R.string.gc_logout_prompt) + "?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    	//Logout
                    	var.logout(gc_Profile.this);
                    	finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    
	private void uploadSelectedProfilePicture(Uri imageUri) {
		Intent intent = new Intent(this, gc_UploadImage.class); 
 	   intent.putExtra("imageUri", imageUri);
 	   intent.putExtra("msg", "");
 	   intent.putExtra("imagetype", "U");
 	   intent.putExtra("groupid","");
 	   startActivityForResult(intent, UPLOADED_IMAGE);
	}
	
	public void refreshNotificationIndicator(int count,View vNotification) {
		if (count>0)
			vNotification.setBackgroundResource(R.drawable.gc_notifications_active);
		else
			vNotification.setBackgroundResource(R.drawable.gc_notifications_inactive);	
	}
	
	private void takeCameraPhoto() {
		// iTakePicureForChatGroup  = RC_TAKE_CHAT_PICTURE - Taking a chat picture
		// 							 RC_TAKE_GROUP_PICTURE - Taking a group picture 
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {

		} else {
			Toast.makeText(gc_Profile.this,"Can not currently access your SD card ", Toast.LENGTH_LONG)
					.show();
			return;
		}
		
		if (checkGCDirectory()==false) {
			return;
		}
		
		File imageDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + var.gcImageDir);

		String path = imageDirectory.toString().toLowerCase();
		String name = imageDirectory.getName().toLowerCase();
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, "Image");
		values.put(MediaStore.Images.Media.BUCKET_ID, path.hashCode());
		values.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, name);
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
		String strFileName = "GROUPcentric_" + getDateTime() + "_tmp.jpg";
		GroupcentricReadWritePrefs rwPrefs = new GroupcentricReadWritePrefs();
		rwPrefs.readWriteUserSetting(gc_Profile.this, "Write","photo_filename", strFileName);

		values.put("_data",Environment.getExternalStorageDirectory() + File.separator + var.gcImageDir + File.separator + strFileName);
		Uri imageCameraUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
		i.putExtra(MediaStore.EXTRA_OUTPUT, imageCameraUri);
		startActivityForResult(i, RC_TAKE_PROFILE_PICTURE);
		imageCameraUri = null;
    }

    private  final static String getDateTime()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh_mm_ss");
        df.setTimeZone(TimeZone.getTimeZone("EST"));
        return df.format(new Date());
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
			Toast.makeText(gc_Profile.this,"Error creating directory ", Toast.LENGTH_SHORT).show();
			bHasGCDirectory = false;
		}
		return bHasGCDirectory;
    }
}
