package com.sirra.server.rest;

import java.lang.reflect.*;
import java.util.*;

import org.reflections.*;

/**
 * Assists with finding and calling methods annotated with GET, PUT, POST or DELETE.
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
		
		Object[] parameterArray = new Object[0];
		if(parameters != null) {
			parameterArray = parameters.toArray();
		}
		
		try {
			return method.invoke(apiBase, parameterArray);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
