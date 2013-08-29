package com.sirra.server.rest;

import java.lang.reflect.*;
import java.util.*;

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
	
	public static Object call(ApiBase apiBase, Class annotationClass, List<Object> parameters) {
		
		Set<Method> methods = ReflectionUtils.getAllMethods(apiBase.getClass(), ReflectionUtils.withAnnotation(annotationClass));
		
		if(methods.size() != 1) {
			throw new RuntimeException("Number of GET methods should be 1 but it is: " + methods.size());
		}
		
		Method method = methods.iterator().next();
		
		// Method 1 of specifying method parameters.
		Object[] parameterArray = new Object[0];
		if(parameters != null) {
			parameterArray = parameters.toArray();
		}
		
		// Method 2 of specifying method parameters.
		Parameters variableAnnotation = method.getAnnotation(Parameters.class);
		Class[] parameterTypes = method.getParameterTypes();
		if(variableAnnotation != null) {
			
			List<Object> values = new ArrayList();
			
			String[] parameterMapping = variableAnnotation.value();
			
			for(int i=0; i<parameterMapping.length; i++) {
				String parameterName = parameterMapping[i];
				
				// If value was not passed in via REST call, it will be null.
				String value = apiBase.getParameter(parameterName);
				
				// Now cast value appropriately
				Class parameterType = parameterTypes[i];
				if(parameterType.equals(int.class)) {
					if(value == null) {
						values.add(0);
					} else {
						values.add(new Integer(value));
					}
				} else if(parameterType.equals(double.class)) {
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
			parameterArray = values.toArray();
		}
		
		try {
			return method.invoke(apiBase, parameterArray);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
