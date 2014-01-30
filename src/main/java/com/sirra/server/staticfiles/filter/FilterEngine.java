package com.sirra.server.staticfiles.filter;

import java.util.*;

import javax.servlet.http.*;

/**
 * Some static files require special handling. A simple filter framework to handle that.
 * 
 * @author aris
 */
public class FilterEngine {
	protected List<Filter> filters;
	
	
	protected static FilterEngine instance;
	
	public static FilterEngine getInstance() {
		if(instance == null) {
			instance = new FilterEngine();
		}
		
		return instance;
	}
	
	private FilterEngine() {
		filters = new ArrayList();
	}
	
	// Call this in your bootstrap code
	public static void addFilter(Filter filter) {
		getInstance().filters.add(filter);
	}
	
	public void filter(String path, HttpServletRequest request, HttpServletResponse response) {
		for(Filter filter: filters) {
			filter.process(path, request, response);
		}
	}
}
