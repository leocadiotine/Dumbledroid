package io.leocad.dumbledroidplugin.core;


public class ClassMapper {

	public static String getPrimitiveTypeName(Object object) {
		
		Class<?> objClass = object.getClass();
		
		if (objClass == Integer.class) {
			return "int";
			
		} else if (objClass == Long.class) {
			return "long";
			
		} else if (objClass == Double.class) {
			return "double";
			
		} else if (objClass == Boolean.class) {
			return "boolean";
		}
		
		return null;
	}
	
	public static String getPrimitiveTypeNameByCasting(String value) {
		
		try {
			Integer.valueOf(value);
			return "int";
		} catch (NumberFormatException e) {}
		
		try {
			Long.valueOf(value);
			return "long";
		} catch (NumberFormatException e) {}
		
		try {
			Double.valueOf(value); // Prefer double over float
			return "double";
		} catch (NumberFormatException e) {}
		
		final String valueLower = value.toLowerCase();
		if (valueLower.equals("true") || valueLower.equals("false")) {
			return "boolean";
		}
		
		return null;
	}
	
	public static String getWrapperTypeNameByCasting(String value) {
		
		try {
			Integer.valueOf(value);
			return "Integer";
		} catch (NumberFormatException e) {}
		
		try {
			Long.valueOf(value);
			return "Long";
		} catch (NumberFormatException e) {}
		
		try {
			Double.valueOf(value); // Prefer double over float
			return "Double";
		} catch (NumberFormatException e) {}
		
		final String valueLower = value.toLowerCase();
		if (valueLower.equals("true") || valueLower.equals("false")) {
			return "Boolean";
		}
		
		return null;
	}
}
