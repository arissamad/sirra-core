package com.sirra.server.sirrapresentation;

import java.io.*;
import java.security.*;

public class MD5Calculator {
	
	public static String getMD5Checksum(String str)
	{
		try {
			byte[] b = createChecksum(new ByteArrayInputStream(str.getBytes("UTF-8")));
			return combineBytes(b);
		} catch(UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static byte[] createChecksum(InputStream is)
	{
		try {
			byte[] buffer = new byte[1024];
			MessageDigest complete = MessageDigest.getInstance("MD5");
			int numRead;
			
			do {
				numRead = is.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);
			is.close();
			return complete.digest();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String combineBytes(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}
}
