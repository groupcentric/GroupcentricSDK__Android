package com.groupcentric.android;
//import com.groupcentric.android.api.R.color;
import com.groupcentric.api.ws.WS08RemoveGroup;

import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * @author support@groupcentric.com
 *	<br>
 * Purpose: Acquire GPS 
 * <br>
 * Notes: 
 * <br>
 * Revisions:
 * 
 *
 */
public class gc_GPS extends Activity implements OnClickListener,LocationListener {
	ImageView btnCancel;
	TextView txtGPSStatus;
	ProgressBar grsProg;
	String provider;
	private LocationManager myLocationManager;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gc_activity_gps);
		myLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria  = new Criteria();
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		provider = myLocationManager.getBestProvider(criteria, true);
		findViewById(R.id.btn_cancel).setOnClickListener(this);
		findViewById(R.id.gps_progressBar).setVisibility(View.GONE);
		txtGPSStatus = (TextView)  findViewById(R.id.txtGPSStatus);
        txtGPSStatus.setText(this.getResources().getString(R.string.gc_checking_gps_status));
        WindowManager.LayoutParams lp = getWindow().getAttributes(); 
        lp.dimAmount = 0.5f; 
        getWindow().setAttributes(lp); 
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        startListening();
        grsProg = (ProgressBar) findViewById(R.id.gps_progressBar);
        grsProg.getIndeterminateDrawable().setColorFilter(Color.parseColor(getResources().getString(R.color.gc_blue)), android.graphics.PorterDuff.Mode.MULTIPLY); 
	}
	
	@Override
	protected void onPause() {
		stopListening();
		finish();
		super.onPause();
	}
	
	public void onClick(View v) {
		if (v.getId() == R.id.btn_cancel) {
			finish();
		}
	}
	
	private void startListening() {
		
    	if (provider!=null) {
    		myLocationManager.requestLocationUpdates(provider, 5000, 0,this);
			txtGPSStatus.setText(this.getResources().getText(R.string.gc_loading_gps));
			findViewById(R.id.gps_progressBar).setVisibility(View.VISIBLE);
    	}
			else
			{
				showGSPNotEnabled();

			}
	}
	
	private void stopListening() {
		try{
			myLocationManager.removeUpdates(this);
		}
		catch (Exception ex){
		}
	}

	public void onLocationChanged(Location location) {
		String strLat = "";
		String strLon = "";
		strLat = Double.toString(location.getLatitude());
		strLon = Double.toString(location.getLongitude());
		setResult(Activity.RESULT_OK, new Intent()
			.putExtra("gps_lat", strLat)
			.putExtra("gps_lon", strLon)
		);
		stopListening();
		finish();
	}

	public void onProviderDisabled(String provider) {
		txtGPSStatus.setText(this.getResources().getText(R.string.gc_gps_disabled));
		findViewById(R.id.gps_progressBar).setVisibility(View.GONE);
		showGSPNotEnabled();
	}

	public void onProviderEnabled(String provider) {
		findViewById(R.id.gps_progressBar).setVisibility(View.VISIBLE);
		txtGPSStatus.setText(this.getResources().getText(R.string.gc_loading_gps));
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	private void showGSPNotEnabled(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your location services seem to be disabled.  Would you like to change these settings?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final ComponentName toLaunch = new ComponentName("com.android.settings", "com.android.settings.SecuritySettings");
                        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.setComponent(toLaunch);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent, 0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
	}
		

}
