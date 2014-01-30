package com.sirra.server.staticfiles.cache;

import javax.servlet.http.*;

public abstract class Data {
	protected String mimeType;
	
	public void superProcess(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public abstract void process(HttpServletResponse response);
}
