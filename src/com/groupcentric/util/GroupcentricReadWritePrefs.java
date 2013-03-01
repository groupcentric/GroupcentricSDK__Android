package com.groupcentric.util;

import android.content.Context;
import android.content.SharedPreferences;

public class GroupcentricReadWritePrefs {
    public static final String PREFS_NAME = "MyGCPrefsFile";

    /**
   	 * Author support@groupcentric.com 
	 *	<br>
	 * Purpose: Used to store and retrieve local settings
	 * <br>
	 * Notes: (Return value always String)
	 * <br>
	 * Revisions:
     * 
     * @param context      (Context)
     * @param readWrite    (Type of operation - 'Read' 'Write')
     * @param userOption   (Key name To Be Saved)
     * @param settingValue (value Of Key)
     * @return String
     */
    public String readWriteUserSetting(Context context,String readWrite, String userOption, String settingValue) {

  	  SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        String usersetting = ""; 
        
        if (readWrite == "Read" )
        {
      	  String strUsersetting = settings.getString(userOption,"");  
      	  usersetting = strUsersetting ;
        } //End Read setting
          
        if (readWrite == "Write" )
        {
      	  SharedPreferences.Editor editor = settings.edit();      
      	  editor.putString(userOption,settingValue.toString());      
      
      	  editor.commit();
            usersetting ="Write";
        } //End Write Setting
          
          return usersetting;

    } // end readWriteUserSetting   
    
  
}
