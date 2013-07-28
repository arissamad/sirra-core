package com.sirra.server.rest;

import java.util.*;

import javax.servlet.http.*;

import com.sirra.server.session.*;

/**
 * All API handler classes need to extend this class.
 * 
 * Simply annotate the appropriate methods with GET, POST, PUT or DELETE annotations.
 * Return any object that is supported by our JSON converter.
 * 
 * @author aris
 */
public class ApiBase extends HttpServlet
{
	protected String method; // GET or POST
	protected List<String> pathVariables; // e.g. a path of "/teachers/1234" will result in this being set to ["1234"].
	
	protected ApiBase() {
		// Do nothing special during instantiation
	}
	
	protected Map<String, Object> fail(String message) {
		Map<String, Object> result = new HashMap();
		result.put("isSuccessful", false);
		result.put("error", message);
		
		return result;
	}
	
	public Map<String, Object> success() {
		Map<String, Object> result = new HashMap();
		result.put("isSuccessful", true);
		
		return result;
	}
	
	protected void save(Object object) {
		SirraSession ms = SirraSession.get();
		ms.getHibernateSession().save(object);
	}
}