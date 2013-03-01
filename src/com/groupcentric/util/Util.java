package com.groupcentric.util;

import java.util.ArrayList;
public class Util {
    private Util() {
    }
    public static long[] convertLongArray(ArrayList<Long> data) {
        int s = data.size();
        long[] res = new long[s];
        for (int i = 0; i < s; i++) {
            res[i] = data.get(i);
        }
        return res;
    }
    public static int[] convertIntegerArray(ArrayList<Integer> data) {
        int s = data.size();
        int[] res = new int[s];
        for (int i = 0; i < s; i++) {
            res[i] = data.get(i);
        }
        return res;
    }
    
	public static String getOnlyNumerics(String strNum) {
		/**
		* @author support@groupcentric.com
		*	<br>
		* Purpose:List Users Contacts
		* <br>
		* Notes: 
		* <br>
		* Revisions:
		* 
		*
		*/
	    if (strNum == null) {
	        return null;
	    }

	    StringBuilder strBuff = new StringBuilder();
	    char c;
	    
	    for (int i = 0; i < strNum.length() ; i++) {
	        c = strNum.charAt(i);
	        
	        if (Character.isDigit(c)) {
	            strBuff.append(c);
	        }
	    }
	    return strBuff.toString();
	}
}
