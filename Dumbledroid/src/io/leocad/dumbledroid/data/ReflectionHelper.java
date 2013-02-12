package io.leocad.dumbledroid.data;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

public class ReflectionHelper {

	public static Field getField(Class<?> fieldClass, String fieldName) throws NoSuchFieldException {

		if (fieldClass == null) {
			throw new NoSuchFieldException();
		}

		try {
			Field field = fieldClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field;
		} catch (SecurityException e) {
			System.out.println("SecurityException");
			return null;
		} catch (NoSuchFieldException e) {
			System.out.println("NoSuchFieldException");
			return getField(fieldClass.getSuperclass(), fieldName);
		}
	}

	public static Field[] getAllFields(Class<?> fieldClass) {
		
		if (fieldClass == null) {
			//TODO This shouldn't be like that.
			return new Field[0];
		}
		
		Field[] fields = fieldClass.getDeclaredFields();
		for (Field f : fields) {
			f.setAccessible(true);
		}
		
		return concatArrays(fields, getAllFields(fieldClass.getSuperclass()));
	}

	@SuppressWarnings("unchecked")
	private static <T> T[] concatArrays(T[] firstArray, T[] secondArray) {
		T[] result = (T[]) Array.newInstance(firstArray.getClass().getComponentType(), firstArray.length + secondArray.length);
		
		System.arraycopy(firstArray, 0, result, 0, firstArray.length);
		System.arraycopy(secondArray, 0, result, firstArray.length, secondArray.length);
		
		return result;
	}
}
