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
 * - You have access to the GET parameters via hasParameter() and getParameter().
 * - You have acccess to the PATH variables via the "pathParameters" variable.
 * - Also see ApiServlet for information on mapping parameters to API method parameters.
 * 
 * @author aris
 */
public class ApiBase extends HttpServlet
{
	protected String method; // GET or POST
	
	// The GET parameters
	protected Map<String, String> parameters;
	
	// A path of "/teachers/1234" will result in this being set to ["1234"].
	protected List<String> pathParameters; 
	
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
	
	protected Object load(Class clazz, String id) {
		SirraSession ms = SirraSession.get();
		return ms.getHibernateSession().load(clazz, id);
	}

	// Convenience method to retrieve a GET parameter as a string
	protected String getParameter(String parameterName) {
		return parameters.get(parameterName);
	}
	
	protected void setParameterMap(Map<String, String> parameterMap) {
		this.parameters = parameterMap;
	}
	
	protected boolean hasParameter(String parameterName) {
		return parameters.containsKey(parameterName);
	}
	
	protected void setPathParameters(List<String> pathParameters) {
		this.pathParameters = pathParameters;
	}
	
	public List<String> getPathParameters() {
		return pathParameters;
	}
}