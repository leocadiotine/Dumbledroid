package io.leocad.dumbledroid.data;

import java.lang.reflect.Field;

public class ReflectionHelper {


	public static Field getField(Class<?> fieldClass, String fieldName) throws NoSuchFieldException {
		final Field field = fieldClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
	}

	public static Field getFieldInHierarchy(Class<?> fieldClass, String fieldName) throws NoSuchFieldException {
		try {
			return getField(fieldClass, fieldName);
		} catch (NoSuchFieldException e) {
			final Class<?> superClass = fieldClass.getSuperclass();
			if(superClass == null) {
				throw e;
			}
			return getFieldInHierarchy(superClass, fieldName);
		}
	}

	public static Field[] getAllFields(Class<?> fieldClass) {
		final Field[] fields = fieldClass.getDeclaredFields();
		for(final Field field : fields) {
			field.setAccessible(true);
		}
		return fields;
	}
}