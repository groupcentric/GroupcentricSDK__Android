package com.groupcentric.android;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;

/**
 * @author support@groupcentric.com
 *	<br>
 * Purpose: Allow the user to select attachment type
 * <br>
 * Notes: 
 * <br>
 * Revisions:
 * 
 *
 */
public class gc_AttachItem extends Activity implements OnClickListener {
    private static final int RC_SELECT_PICTURE    = 40;
    private static final int RC_TAKE_PICTURE      = 50;
    private static final int RC_SET_LOCATION      = 60;
    private static final int RC_SHARE_LINK        = 140;
    String selectType = "";
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gc_activity_add_attachment);
		findViewById(R.id.btn_select_saved_picture).setOnClickListener(this);
		findViewById(R.id.btn_take_picture).setOnClickListener(this);
		findViewById(R.id.btn_setlocation).setOnClickListener(this);
		findViewById(R.id.btn_share_link).setOnClickListener(this);
		if (getIntent().getExtras() != null) {
			 if (getIntent().getExtras().getString("select_for_type")!=null)
					 selectType = getIntent().getExtras().getString("select_for_type");
    	}
		
        //Don't show the 'set location' button if we are adding attachment for group 
		if (selectType.equalsIgnoreCase("group"))
		{
			findViewById(R.id.btn_setlocation).setVisibility(View.GONE);
			findViewById(R.id.lyt_attachment).setBackgroundDrawable(this.getResources().getDrawable(R.drawable.gc_groupattachmentsbg));
		}
		
        //Don't show the 'set location' button if we are adding attachment for Profile
		if (selectType.equalsIgnoreCase("profile"))
		{
			findViewById(R.id.btn_setlocation).setVisibility(View.GONE);
			findViewById(R.id.btn_share_link).setVisibility(View.GONE);
			findViewById(R.id.lyt_attachment).setBackgroundDrawable(this.getResources().getDrawable(R.drawable.gc_profileattachmentsbg));
		}
		
		// If user does not have a camera do not how take picture button
	    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) & !getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
	    	findViewById(R.id.btn_take_picture).setVisibility(View.GONE);
	    }
		
         WindowManager.LayoutParams lp = getWindow().getAttributes(); 
         lp.dimAmount = 0.5f; 
         getWindow().setAttributes(lp); 
		 getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	}
	
	public void onClick(View v) {
		if (v.getId() == R.id.btn_select_saved_picture) {
			setResult(Activity.RESULT_OK, new Intent()
				.putExtra("attchment_type", RC_SELECT_PICTURE)
				.putExtra("select_for_type", selectType)
				);
			finish();
		} else if (v.getId() == R.id.btn_take_picture) {
			setResult(Activity.RESULT_OK, new Intent()
			.putExtra("attchment_type", RC_TAKE_PICTURE)
			.putExtra("select_for_type", selectType)
			);
			finish();
		} else if (v.getId() == R.id.btn_setlocation) {
			setResult(Activity.RESULT_OK, new Intent().putExtra("attchment_type", RC_SET_LOCATION));
			finish();
		} else if (v.getId() == R.id.btn_share_link) {
			setResult(Activity.RESULT_OK, new Intent().putExtra("attchment_type", RC_SHARE_LINK));
			finish(); 
		} 
	}
	

}
