package com.sirra.server.staticfiles.cache;

import java.io.*;

import javax.servlet.http.*;

public class StringData extends Data {
	protected String stringData;
	
	public StringData(String mimeType, String content) {
		this.mimeType = mimeType;
		this.stringData = content;
	}
	
	public void process(HttpServletResponse response) {
		response.setContentType(mimeType);
		
		try {
			response.getWriter().write(stringData);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}
