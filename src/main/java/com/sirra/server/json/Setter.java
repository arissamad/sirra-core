package com.sirra.server.json;

import java.lang.reflect.*;
import java.util.*;

import org.reflections.*;

import com.sirra.server.rest.*;

/**
 * Sets an object's attribute according to these rules:
 * 
 * 	- If there is a setter method, call that.
 *  - Otherwise set the attribute directly.
 * 
 * @author aris
 */
public class Setter {
	
	public static void set(Object obj, String attributeName, Object value) {
		
		String setterName = "set" + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
		
		try {
			Set<Method> methods = ReflectionUtils.getAllMethods(obj.getClass(), ReflectionUtils.withName(setterName));
			
			if(methods.size() > 0) {
				Method method = methods.iterator().next();
				
				Class<?>[] paramTypes = method.getParameterTypes();

				Object castedValue = value;
				if(paramTypes.length == 1) {
					if(value != null && paramTypes[0] != value.getClass()) {
						castedValue = Caster.cast(paramTypes[0], value);
					}
					method.invoke(obj, castedValue);	
				}
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		try {
			Field field = obj.getClass().getDeclaredField(attributeName);
			field.setAccessible(true);
			field.set(obj, value);
			return;
		} catch(NoSuchFieldException e) {
			// Okay move on
		} catch(Exception e) {
			System.out.println("Whoops");
		}
	}
}
