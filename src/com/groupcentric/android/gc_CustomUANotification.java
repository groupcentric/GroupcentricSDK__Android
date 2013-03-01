package com.groupcentric.android;

import java.util.Map;
import com.urbanairship.push.BasicPushNotificationBuilder;

public class gc_CustomUANotification extends BasicPushNotificationBuilder {

	@Override
	public int getNextId(String arg0, Map<String, String> map) {
		if (map.containsKey("com.urbanairship.push.STRING_EXTRA")) { 
			this.constantNotificationId = 0;
			String val = map.get("com.urbanairship.push.STRING_EXTRA");
			if (val != null) {
				if (val.indexOf("GroupID") > -1) {
					int iPassedGroupID = 0;
					try {
						iPassedGroupID = Integer.parseInt(val.replace(
								"GroupID", ""));
					} catch (NumberFormatException nfe) {
						System.out.println("Could not parse " + nfe);
					}
					this.constantNotificationId = iPassedGroupID;
				}
			}

		} //
		return super.getNextId(arg0, map);
	}
}
