package com.sirra.server.staticfiles.cache;

import java.util.*;

import javax.servlet.http.*;

import com.sirra.server.*;

/**
 * Does not cache during Development mode.
 * 
 * @author aris
 */
public class StaticContentCache {
	
	protected Map<String, Data> cache;
	
	protected static StaticContentCache instance;
	
	public static StaticContentCache getInstance() {
		if(instance == null) {
			instance = new StaticContentCache();
		}
		return instance;
	}
	
	private StaticContentCache() {
		cache = new HashMap();
	}
	
	public boolean containsPath(String requestPathInfo) {
		if(Mode.get() == Mode.Development) return false;
		
		return cache.containsKey(requestPathInfo);
	}
	
	public void process(String requestPathInfo, HttpServletResponse response) {
		cache.get(requestPathInfo).process(response);
	}
	
	public void cacheString(String requestPathInfo, String mimeType, String content) {
		cache.put(requestPathInfo, new StringData(mimeType, content));
	}
	
	public void cacheBytes(String requestPathInfo, String mimeType, byte[] bytes) {
		cache.put(requestPathInfo, new ByteData(mimeType, bytes));
	}
}
