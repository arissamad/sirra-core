package com.sirra.server.rest;

public class Caster {
	
	public static Object cast(Class parameterType, Object value) {
		
		if(parameterType.equals(int.class)) {
			if(value == null) {
				return 0;
			} else {
				return Integer.parseInt(value.toString());
			}
		} else if(parameterType.equals(Integer.class)) {
			if(value == null) {
				return null;
			} else {
				return Integer.parseInt(value.toString());
			}
		}
		else if(parameterType.equals(double.class)) {
			if(value == null) {
				return 0.0d;
			} else {
				return Double.parseDouble(value.toString());
			}
		}
		else if(parameterType.equals(String.class)) {
			return value.toString();
		}
		else if(parameterType.equals(Boolean.class)) {
			return Boolean.parseBoolean(value.toString());
		}
		return value;
	}
}
