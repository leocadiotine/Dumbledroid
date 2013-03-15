package io.leocad.dumbledroidplugin.core;


public class ClassMapper {

	public static String getPrimitiveTypeName(Object object) {
		
		Class<?> objClass = object.getClass();
		
		if (objClass == Integer.class) {
			return "int";
			
		} else if (objClass == Long.class) {
			return "long";
			
		} else if (objClass == Float.class) {
			return "float";
			
		} else if (objClass == Double.class) {
			return "double";
			
		} else if (objClass == Boolean.class) {
			return "boolean";
			
		} else if (objClass == String.class) {
			return "String";
		}
		
		return null;
	}
}
