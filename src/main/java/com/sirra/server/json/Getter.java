package com.sirra.server.json;

import java.lang.reflect.*;

/**
 * Sets an object's field according to these rules:
 * 
 *  - If there is a setter method, call that.
 *  - Otherwise set the attribute directly.
 * 
 * @author aris
 *
 */
public class Getter {

	public static Object get(Object obj, String attributeName) {
		String getterName = "get" + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
		
		try {
			Method method = obj.getClass().getMethod(getterName, Object.class);
			return method.invoke(obj);
		} catch(NoSuchMethodException e) {
			// Okay move on
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		try {
			Field field = obj.getClass().getDeclaredField(attributeName);
			field.setAccessible(true);
			return field.get(obj);
		} catch(NoSuchFieldException e) {
			// Okay move on
		} catch(Exception e) {
			System.out.println("Whoops");
		}
		
		return null;
	}
}
