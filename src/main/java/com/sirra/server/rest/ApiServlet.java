package com.sirra.server.rest;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import javax.ws.rs.*;

import org.json.*;
import org.reflections.*;

import com.sirra.server.json.*;
import com.sirra.server.session.*;

/**
 * All API calls are directed to this servlet first, which then finds the right ApiBase handler
 * based on a matching path.
 * 
 * Calls the appropriate method in based on annotations (GET, POST, PUT or DELETE). 
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

    	String parameterString = request.getParameter("parameters");
    	
    	System.out.println("\n--------- API Call Begin: " + httpMethod.name() + " " + apiPath + " " + parameterString + " --------- ");
    	SirraSession.start(request, response);
    	
    	List<Object> parameters = (List<Object>) JsonUtil.getInstance().parse(parameterString);
    	
    	try {
	    	
	    	List<String> pathList = new ArrayList();
	    	String[] pathElements = apiPath.split("/");
	    	for(String pathElement: pathElements) {
	    		if(pathElement.equals("")) continue;
	    		pathList.add(pathElement.toLowerCase());
	    	}
	    	
	    	Class clazz = processPath(pathList);
	    	
	    	try {
	    		ApiBase apiBase = (ApiBase) clazz.newInstance();
	    		
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
	    	}
	    	
	    	//response.getWriter().write("[api not found] The date is: " + new Date().toString());
    	}
    	finally {
    		SirraSession.end();
    		System.out.println("--------- API Call END: " + httpMethod.name() + " " + apiPath +" ---------");
    	}
    }
    
    protected Class processPath(List<String> pathList) {
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
