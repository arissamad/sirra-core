package com.sirra.server.staticfiles.cache;

import java.io.*;

import javax.servlet.http.*;

import org.apache.commons.io.*;

public class ByteData extends Data {
	protected byte[] bytes;
	
	public ByteData(String mimeType, byte[] incomingBytes) {
		bytes = incomingBytes;
	}
	
	public void process(HttpServletResponse response) {
		response.setContentType(mimeType);
		try {
			response.getOutputStream().write(bytes);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}
