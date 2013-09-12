package com.sirra.server.rest;

import java.lang.reflect.*;
import java.util.*;

import javax.ws.rs.*;

import org.reflections.*;

import com.sirra.server.rest.annotations.*;

/**
 * Assists with finding and calling methods annotated with GET, PUT, POST or DELETE.
 * 
 * There are two ways method parameters can be passed in. 
 *  - Via regular rest parameters, which then get mapped via the "Variables" annotation.
 *  - Via a "parameters" variable.
 * 
 * Otherwise, the parameters are not passed in via the method, and is made available via the ApiBase.getParameter() method.
 * 
 * @author aris
 */
public class AnnotatedMethodCaller {
	
	public static Object call(ApiBase apiBase, List<Object> parameters, Class annotationClass) {
		
		boolean firstParameterIsId = false;
		
		if(apiBase.getPathParameters().size() == 1) {
			firstParameterIsId = true;
		}
		
		Set<Method> methods = ReflectionUtils.getAllMethods(apiBase.getClass(), ReflectionUtils.withAnnotation(annotationClass));
		
		// Now filter BY_ID methods as necessary
		Iterator<Method> methodIt = methods.iterator();
		while(methodIt.hasNext()) {
			Method method = methodIt.next();
			
			if(firstParameterIsId && !method.isAnnotationPresent(BY_ID.class)) {
				methodIt.remove();
			} else if(!firstParameterIsId && method.isAnnotationPresent(BY_ID.class)) {
				methodIt.remove();
			}
		}
		
		if(methods.size() != 1) {
			throw new RuntimeException("For class " + apiBase.getClass().getSimpleName() + ", number of methods matching " + annotationClass + " and has BY_ID:" + firstParameterIsId + " should be 1 but it is: " + methods.size());
		}
		
		Method method = methods.iterator().next();
		
		List<Object> finalParameterList = new ArrayList();
		
		// Fill in ID parameter for GET_BY_ID. Other methods of specifying parameters, if applicable, will override this.
		
		if(firstParameterIsId) {
			if(apiBase.getPathParameters().size() == 1) {
				finalParameterList.add(apiBase.getPathParameters().get(0));
			}
		}
		
		// Method 1 of specifying method parameters.
		if(parameters != null) {
			finalParameterList.clear();
			finalParameterList.addAll(parameters);
		}
		
		// Method 2 of specifying method parameters: As query parameters (or, in the case of jQuery, as the data object).
		Parameters variableAnnotation = method.getAnnotation(Parameters.class);
		Class[] parameterTypes = method.getParameterTypes();
		if(variableAnnotation != null) {
			
			List<Object> values = new ArrayList();
			
			String[] parameterMapping = variableAnnotation.value();
			
			for(int i=0; i<parameterMapping.length; i++) {
				String parameterName = parameterMapping[i];
				
				// If value was not passed in via REST call, it will be null.
				String value = apiBase.getParameter(parameterName);
				
				// Special case for first parameter when it is GET_BY_ID
				if(i == 0 && firstParameterIsId) {
					if(apiBase.getPathParameters().size() == 1) {
						value = apiBase.getPathParameters().get(0);
					}
				}
				
				// Now cast value appropriately
				Class parameterType = parameterTypes[i];
				if(parameterType.equals(int.class)) {
					if(value == null) {
						values.add(0);
					} else {
						values.add(new Integer(value));
					}
				} else if(parameterType.equals(Integer.class)) {
					if(value == null) {
						values.add(null);
					} else {
						values.add(new Integer(value));
					}
				}
				else if(parameterType.equals(double.class)) {
					if(value == null) {
						values.add(0.0d);
					} else {
						values.add(new Double(value));
					}
				}
				else {
					values.add(value);	
				}
			}
			
			finalParameterList.clear();
			finalParameterList.addAll(values);
		}
		
		try {
			return method.invoke(apiBase, finalParameterList.toArray());
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected static String formatAnnotations(List<Class> annotations) {
		StringBuffer str = new StringBuffer();
		str.append("[");
		
		Iterator<Class> it = annotations.iterator();
		
		while(it.hasNext()) {
			Class annotation = it.next();
			
			str.append(annotation.getSimpleName());
			while(it.hasNext()) {
				str.append(", ");
			}
		}
		
		str.append("]");
		
		return str.toString();
	}
}