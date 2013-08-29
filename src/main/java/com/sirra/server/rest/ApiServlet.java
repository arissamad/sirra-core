package com.sirra.server.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import org.json.JSONObject;
import org.reflections.Reflections;

import com.sirra.server.json.JsonUtil;
import com.sirra.server.session.SirraSession;

/**
 * All API calls are directed to this servlet first, which then finds the right ApiBase handler
 * based on a matching path.
 * 
 * Calls the appropriate method in based on annotations (GET, POST, PUT or DELETE).
 * 
 * There are three ways to pass in parameters:
 * 
 *   - Regular GET parameters. These are available from ApiBase via convenience methods.
 *   - PATH parameters. These are available from ApiBase via "pathParameters" variable.
 *   - A special GET parameter called "parameters". This is an array and maps to the API method parameters.
 *     For example, if the api method is "doApi(String a, int b)", then the rest call can pass in 
 *     ?parameters=["A String", 5].
 * 
 * @author aris
 */
@WebServlet(urlPatterns = {"/api/*"})
public class ApiServlet extends HttpServlet {

	protected static Map<String, Class<? extends ApiBase>> lookup;
	
	protected static String packageBase = "com.sirra";
	
	/**
	 * Set the root package where sirra-core will search for your API classes.
	 * 
	 * @param incomingPackageBase e.g. "com.sirra"
	 */
	public static void setAPIPackageBase(String incomingPackageBase) {
		packageBase = incomingPackageBase;
	}

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	execute(request, response);
    }

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	execute(request, response);
    }

	@Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	execute(request, response);
    }

	@Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	execute(request, response);
    }

    protected void execute(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
    	String apiPath = request.getPathInfo();
    	HttpType httpMethod = HttpType.valueOf(request.getMethod());
    	
    	Map<String, String[]> mm =  request.getParameterMap();
    	for (Entry<String, String[]> e : mm.entrySet()) {
    	    System.out.println(" --- " + e.getKey());
    	}
    	
    	String parameterString = request.getParameter("parameters");
    	List<Object> parameters = (List<Object>) JsonUtil.getInstance().parse(parameterString);
    	
    	Map<String, String> parameterMap = processParameters(request);

    	List<String> pathList = new ArrayList();
    	String[] pathElements = apiPath.split("/");
    	for(String pathElement: pathElements) {
    		if(pathElement.equals("")) continue;
    		pathList.add(pathElement.toLowerCase());
    	}
    	
    	List<String> pathParameters = new ArrayList();
    	Class clazz = processPath(pathList, pathParameters);
    	
    	StringBuffer parameterDebug = new StringBuffer();
    	Iterator<String> parameterIterator = parameterMap.keySet().iterator();
    	while(parameterIterator.hasNext()) {
    		String key = parameterIterator.next();
    		parameterDebug.append(key + ": ");
    		parameterDebug.append(parameterMap.get(key));
    		
    		if(parameterIterator.hasNext()) parameterDebug.append(", ");
    	}
    	
    	System.out.println("\n--------- API Call Begin: " + httpMethod.name() + " " + apiPath + " - Parameters: [" + parameterDebug.toString() + "] --------- ");
    	SirraSession.start(request, response);
    	
    	try {
    		ApiBase apiBase = (ApiBase) clazz.newInstance();
    		
    		apiBase.setPathParameters(pathParameters);
    		apiBase.setParameterMap(parameterMap);
    		
    		Object result = null;
    		if(httpMethod == HttpType.GET) {
    			result = AnnotatedMethodCaller.call(apiBase, GET.class, parameters);
    			
    		} else if(httpMethod == HttpType.POST) {
    			result = AnnotatedMethodCaller.call(apiBase, POST.class, parameters);
    		} else if(httpMethod == HttpType.PUT) {
    			result = AnnotatedMethodCaller.call(apiBase, PUT.class, parameters);
    		} else if(httpMethod == HttpType.DELETE) {
    			result = AnnotatedMethodCaller.call(apiBase, DELETE.class, parameters);
    		} else {
    			result = AnnotatedMethodCaller.call(apiBase, GET.class, parameters);
    		}

    		String responseStr = formatResponse(result);
    		
    		response.getWriter().write(responseStr);
    		return;
    	} catch(Exception e) {
    		throw new RuntimeException(e);
    	} finally {
    		SirraSession.end();
    		System.out.println("--------- API Call END: " + httpMethod.name() + " " + apiPath +" ---------");
    	}
    }
    
    protected Class processPath(List<String> pathList, List<String> pathParameters) {
    	// Try longest first, and get progressively shorter
    	
    	for(int i=pathList.size(); i>=0; i--) {
    		
    		StringBuffer str = new StringBuffer("/");
    		for(int j=0; j<i; j++) {
    			str.append(pathList.get(j));
    			
    			if(j < i-1) {
    				str.append("/");
    			}
    		}
    		
    		Class clazz = getCorrespondingClass(str.toString());
    		if(clazz != null) {
    			
    			for(int k=i; k<pathList.size(); k++) {
    				pathParameters.add(pathList.get(k));
    			}
    			
    			return clazz;
    		}
    	}
    	
    	throw new RuntimeException("Cannot find API Class in " + packageBase + ": " + pathList);
    }
    
    protected Class<? extends ApiBase> getCorrespondingClass(String restPath) {
    	
    	// Currently this does not allow for dynamic updates of REST classes in development mode.
    	if(lookup == null) {
    		lookup = new HashMap();
    		
	    	Reflections reflections = new Reflections(packageBase);
	    	Set<Class<? extends ApiBase>> apiClasses = reflections.getSubTypesOf(ApiBase.class);
	    	
	    	// For each class, figure out the path.
	    	for(Class<? extends ApiBase> apiClass: apiClasses) {
	    		System.out.println("Class is " + apiClass);
	    		String fullName = apiClass.getCanonicalName();
	    		
	    		String endPath = fullName.substring(fullName.indexOf(".api.") + 5);
	    		String[] pieces = endPath.split("\\.");
	    		
	    		StringBuffer path = new StringBuffer("/");
	    		
	    		List<String> piecesList = new ArrayList();
	    		
	    		String lastPiece = "";
	    		for(String piece: pieces) {
	    			
	    			// For example, teachers.Teachers, the path is "/teachers".
	    			if(piece.toLowerCase().equals(lastPiece)) break;
	    			piecesList.add(piece.toLowerCase());
	    			
	    			lastPiece = piece;
	    		}
	    		
	    		Iterator<String> it = piecesList.iterator();
	    		while(it.hasNext()) {
	    			String pathElement = it.next();
	    			path.append(pathElement);
	    			
	    			if(it.hasNext()) path.append("/");
	    		}
	    		
	    		lookup.put(path.toString(), apiClass);
	    	}
    	}

    	return lookup.get(restPath);
    }
    
    protected Map<String, String> processParameters(HttpServletRequest request) {

    	Map<String, String[]> requestMap = request.getParameterMap();
    	Map<String, String> parameterMap = new TreeMap();
    	
    	System.out.println("Parameters:");
    	for(String key: requestMap.keySet()) {
    		String[] values = requestMap.get(key);
    		String value = null;
    		if(values.length > 0) value = values[0];
    		
    		parameterMap.put(key, value);
    	}
    	
    	return parameterMap;
    }
    
    protected String formatResponse(Object returnValue) {
    	Object json;
    	
		if(returnValue == null) {
			json = new JSONObject();
		} else {
			json = JsonUtil.getInstance().convert(returnValue);
		}
		
		return json.toString();
	}
}
