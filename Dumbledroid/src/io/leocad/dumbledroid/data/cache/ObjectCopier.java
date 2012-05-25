package io.leocad.dumbledroid.data.cache;

import java.lang.reflect.Field;

public class ObjectCopier {

	public static boolean copy(Object src, Object dest) {

		Class<?> srcClass = src.getClass();
		Class<?> destClass = dest.getClass();

		if (!srcClass.getName().equals(destClass.getName())) {
			return false;
		}

		Field[] srcFields = srcClass.getDeclaredFields();

		for (int i = 0; i < srcFields.length; i++) {
			Field srcField = srcFields[i];

			try {
				Field destField = destClass.getDeclaredField(srcField.getName());
				destField.set(dest, srcField.get(src));

			} catch (Exception e) {
			}
		}

		return true;
	}
}
