package com.groupcentric.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	
	public static String getMd5Hash(String input) {
        MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger number = new BigInteger(1,messageDigest);
        StringBuilder md5 = new StringBuilder(number.toString(16));       
        while (md5.length() < 32)
            md5.insert(0, '0');       
        return md5.toString();
	}

}