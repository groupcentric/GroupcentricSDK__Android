package com.groupcentric.android;

import com.groupcentric.util.GroupcentricReadWritePrefs;
import android.app.Application;
import android.content.Context;

public class gc_Variables extends Application {
	GroupcentricReadWritePrefs rwPrefs = new GroupcentricReadWritePrefs();
	public static final String IncomingGCPushMSG = "incomingGCPushMSG";
	public static final String gcImageDir = "GROUPcentric";
	
    public gc_Variables() {
    }
	//GroupCentric User ID
    synchronized
	public int getGroupcentricUserID(Context context) {
		String GCUserID = rwPrefs.readWriteUserSetting(context, "Read","GroupcentricUserId", "");
		if (GCUserID == "")
			GCUserID = "0";
		return Integer.parseInt(GCUserID);
	}

	synchronized
	public void setGroupcentricUserID(Context context,int id) {
		String strGCUserID = Integer.toString(id);
		rwPrefs.readWriteUserSetting(context, "Write","GroupcentricUserId", strGCUserID);
	}
	
	// Get API Key
	synchronized
	public String getGroupcentricAPIKey(Context context) {
		return rwPrefs.readWriteUserSetting(context, "Read","GroupcentricAPIKey", "");
	}
	
	synchronized
	public void setGroupcentricAPIKey(Context context, String apikey)
	{
		rwPrefs.readWriteUserSetting(context, "Write","GroupcentricAPIKey", apikey);
	}
	
	public void logout(Context context) {
   //     PushManager.disablePush();
		rwPrefs.readWriteUserSetting(context, "Write","GroupcentricUserId", Integer.toString(0)); //Ret user id
	}

	synchronized
	public int getGroupIDToOpen(Context context) {
		String GroupIDToOpen = rwPrefs.readWriteUserSetting(context, "Read","GroupIDToOpen", "");
		if (GroupIDToOpen == "")
			GroupIDToOpen = "0";
		return Integer.parseInt(GroupIDToOpen);
	}
	
	synchronized
	public void setGroupIDToOpen(Context context,int id) {
		String GroupIDToOpen = Integer.toString(id);
		rwPrefs.readWriteUserSetting(context, "Write","GroupIDToOpen", GroupIDToOpen);
	}
	
}
