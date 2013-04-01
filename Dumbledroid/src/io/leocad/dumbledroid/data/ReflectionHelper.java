package io.leocad.dumbledroid.data;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

public class ReflectionHelper {

	public static Field getField(Class<?> fieldClass, String fieldName) throws NoSuchFieldException {

		try {
			Field field = fieldClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field;
			
		} catch (NoSuchFieldException e) {
			Class<?> superclass = fieldClass.getSuperclass();
			
			if (superclass == null) {
				throw new NoSuchFieldException();
			}
			
			return getField(superclass, fieldName);
		}
	}

	public static Field[] getAllFields(Class<?> fieldClass) {
		
		Field[] fields = fieldClass.getDeclaredFields();
		for (Field f : fields) {
			f.setAccessible(true);
		}
		
		Class<?> superclass = fieldClass.getSuperclass();
		
		if (superclass == null) {
			return fields;
		}
		
		return concatArrays(fields, getAllFields(superclass));
	}

	@SuppressWarnings("unchecked")
	private static <T> T[] concatArrays(T[] firstArray, T[] secondArray) {
		T[] result = (T[]) Array.newInstance(firstArray.getClass().getComponentType(), firstArray.length + secondArray.length);
		
		System.arraycopy(firstArray, 0, result, 0, firstArray.length);
		System.arraycopy(secondArray, 0, result, firstArray.length, secondArray.length);
		
		return result;
	}
}
