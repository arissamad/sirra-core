package com.sirra.server.staticfiles.filter.common;

import javax.servlet.http.*;

import com.sirra.server.staticfiles.filter.*;

/**
 * Sets caching for a week. 
 * 
 * @author aris
 */
public class CacheFilter extends Filter {

	protected String[] paths;
	
	public CacheFilter(String... incomingPaths) {
		this.paths = incomingPaths;
	}
	
	@Override
	public void process(String requestPath, HttpServletRequest request, HttpServletResponse response) {
		if(matches(requestPath, paths)) {
			response.setHeader("Cache-Control", "public, max-age=604800"); // 1 week
		}
	}	
}
