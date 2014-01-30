package com.sirra.server.staticfiles.filter;

import javax.servlet.http.*;

/**
 * Subclass this if you want to filter. Call FilterEngine.addFilter(...) in your server bootstrap.
 * 
 * @author aris
 *
 */
public abstract class Filter {

	public abstract void process(String path, HttpServletRequest request, HttpServletResponse response);
	
	/**
	 * Convenient path matcher method for use by subclasses.
	 */
	public boolean matches(String requestPath, String... paths) {
		requestPath = requestPath.toLowerCase();
		
		for(int i=0; i<paths.length; i++) {
			if(requestPath.indexOf(paths[i]) >= 0) return true;
		}
		
		return false;
	}
}
