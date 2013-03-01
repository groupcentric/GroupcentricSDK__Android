package com.groupcentric.android;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.groupcentric.util.GroupcentricReadWritePrefs;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

public class gc_UAIntentReceiver extends BroadcastReceiver {

	private static final String logTag = "PushSample";
	
	public static final String IncomingGCPushMSG = "incomingGCPushMSG";
	private gc_Variables var;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(PushManager.ACTION_PUSH_RECEIVED)) {
			int id = intent.getIntExtra(PushManager.EXTRA_NOTIFICATION_ID, 0);
			//Alert the groupcentric details and list broadcast recievers that a push message came in
	         Intent i = new Intent();
	         i.setAction(IncomingGCPushMSG);
	         i.putExtra("extra", PushManager.EXTRA_ALERT);
	         i.putExtra("msg",  PushManager.EXTRA_ALERT);
	         i.putExtra("Notification_ID", id);
	         context.sendBroadcast(i);
		} else if (action.equals(PushManager.ACTION_NOTIFICATION_OPENED)) {
			if(intent.getExtras().size() > 0)
			{
				//look for the default extra key that ua allows developers to use
				String val = intent.getStringExtra("com.urbanairship.push.STRING_EXTRA");
				if (val==null)
					val = "";
				if(val.indexOf("GroupID")> -1)
				{
					//its a groupid so open up that group
					//check for a userid, if no userid cuz user logged out then dont put them into group details and instead send to main activity of app
					var = new gc_Variables(); //finish instantiating the GroupcentricVariables object which will give access to the Groupcentric UserID and APIKey
					if(var.getGroupcentricUserID(context) > 0)
					{
						int groupid = Integer.parseInt(val.replace("GroupID", ""));
						Intent launch = new Intent(Intent.ACTION_MAIN);
						launch.setClass(UAirship.shared().getApplicationContext(),gc_GroupDetails.class);
						launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						//launch.putExtra("groupid",groupid);
						var.setGroupIDToOpen(UAirship.shared().getApplicationContext(),groupid);
						UAirship.shared().getApplicationContext().startActivity(launch);
					}
					else
					{
						//not sure what it might be but just open the app in general
						launchAppHomeActivity();
					}
				}
				else
				{
					//not sure what it might be but just open the app in general
				 launchAppHomeActivity();
				}
			}
			else
			{
				//just open the app in general
				launchAppHomeActivity();
			}
			
		} else if (action.equals(PushManager.ACTION_REGISTRATION_FINISHED)) {
			//ua registration finished so this grabs the ua token and updates the users Groupcentric account with it
			GroupcentricReadWritePrefs rwPrefs = new GroupcentricReadWritePrefs();
			rwPrefs.readWriteUserSetting(UAirship.shared().getApplicationContext(), "Write","UAAPID",intent.getStringExtra(PushManager.EXTRA_APID));
		    Intent iUpdateTokenService = new Intent(context, gc_UpdateUATokenService.class);
		    context.startService(iUpdateTokenService);			
		}
	}

	private void launchAppHomeActivity() {
		Intent i = UAirship.shared().getApplicationContext().getPackageManager().getLaunchIntentForPackage(UAirship.shared().getPackageName() );
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		UAirship.shared().getApplicationContext().startActivity(i);
	}

	/**
	 * Log the values sent in the payload's "extra" dictionary.
	 * 
	 * @param intent
	 *            A PushManager.ACTION_NOTIFICATION_OPENED or
	 *            ACTION_PUSH_RECEIVED intent.
	 */
	private void logPushExtras(Intent intent) {
		Set<String> keys = intent.getExtras().keySet();
		for (String key : keys) {
			// ignore standard C2DM extra keys
			List<String> ignoredKeys = (List<String>) Arrays.asList(
					"collapse_key",// c2dm collapse key
					"from",// c2dm sender
					PushManager.EXTRA_NOTIFICATION_ID,// int id of generated
														// notification
														// (ACTION_PUSH_RECEIVED
														// only)
					PushManager.EXTRA_PUSH_ID,// internal UA push id
					PushManager.EXTRA_ALERT);// ignore alert
			if (ignoredKeys.contains(key)) {
				continue;
			}
		}
	}

}
