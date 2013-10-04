package io.leocad.dumbledroid.data.cache;

import io.leocad.dumbledroid.data.ReflectionHelper;

import java.lang.reflect.Field;

public class ObjectCopier {

	public static boolean copy(Object src, Object dest) {
		return copyAux(src, dest, src.getClass(), dest.getClass());
	}

	public static boolean copyAux(Object src, Object dest, Class<?> srcClass, Class<?> destClass) {
		if(srcClass != destClass) {
			return false;
		}

		final Field[] srcFields = ReflectionHelper.getAllFields(srcClass);
		for(final Field srcField : srcFields) {
			try {
				final Field destField = ReflectionHelper.getField(destClass, srcField.getName());
				destField.set(dest, srcField.get(src));
			} catch (Exception e) {
				//Ignore this field but continue gracefully
			}
		}

		final Class<?> srcSuperClass = srcClass.getSuperclass();
		if(srcSuperClass != null) {
			return copyAux(src, dest, srcSuperClass, destClass.getSuperclass());
		}
		return true;
	}
}