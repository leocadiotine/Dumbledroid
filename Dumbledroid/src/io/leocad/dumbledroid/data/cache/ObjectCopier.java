package io.leocad.dumbledroid.data.cache;

import io.leocad.dumbledroid.data.ReflectionHelper;

import java.lang.reflect.Field;

public class ObjectCopier {

	public static boolean copy(Object src, Object dest) {

		Class<?> srcClass = src.getClass();
		Class<?> destClass = dest.getClass();

		if (!srcClass.getName().equals(destClass.getName())) {
			return false;
		}

		Field[] srcFields = ReflectionHelper.getAllFields(srcClass);

		for (Field srcField : srcFields) {
			try {
				Field destField = ReflectionHelper.getField(destClass, srcField.getName());
				destField.set(dest, srcField.get(src));
			}
			catch (Exception e) {

			}
		}

		return true;
	}
}
