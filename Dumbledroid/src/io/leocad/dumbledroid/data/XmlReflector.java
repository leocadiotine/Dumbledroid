package io.leocad.dumbledroid.data;

import io.leocad.dumbledroid.data.xml.Node;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Vector;

import android.util.Log;

public class XmlReflector {

	public static void reflectXmlRootNode(Object model, Node node) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InstantiationException {

		//Check if the root node is an array
		Class<?> modelClass = model.getClass();
		Field rootNodeField = null;
		
		try {
			rootNodeField = modelClass.getField(node.name);
			
		} catch (NoSuchFieldException e) {
		}

		//The developer has declared a field to match the root node
		if (rootNodeField != null){
			
			if (rootNodeField.getType() == List.class) {
				processListField(model, rootNodeField, node);
			} else {
				reflectXmlObject(model, node);
			}
			
		} else {
			//The developer has mapped the root node to the object itself
			reflectXmlObject(model, node);
		}
	}

	private static void reflectXmlObject(Object model, Node node) {

		Class<?> modelClass = model.getClass();
		String fieldName = null;

		try {

			//Treat attributes like fields
			if (node.attributes != null) {
				for (String key : node.attributes.keySet()) {

					fieldName = key;
					String value = node.attributes.get(key);

					Field field = modelClass.getField(key);
					Class<?> type = field.getType();

					if (type == String.class) {
						field.set(model, value);

					} else if (type == boolean.class || type == Boolean.class) {
						field.set(model, Boolean.valueOf(value));

					} else if (type == int.class || type == Integer.class) {
						field.set(model, Integer.valueOf(value));

					} else if (type == double.class || type == Double.class) {
						field.set(model, Double.valueOf(value));

					}
				}
			}

			for (int i = 0; i < node.subnodes.size(); i++) {

				Node subnode = node.subnodes.get(i);

				fieldName = subnode.name;
				Field field = modelClass.getField(subnode.name);

				if (field.getType() == List.class) {
					processListField(model, field, subnode);

				} else {
					processSingleField(model, field, subnode);
				}

			}
			
		} catch (NoSuchFieldException e) {
			Log.w(XmlReflector.class.getName(), "Can not locate field named " + fieldName);

		} catch (IllegalArgumentException e) {
			Log.w(XmlReflector.class.getName(), "Can not put a String in the field named " + fieldName);

		} catch (IllegalAccessException e) {
			Log.w(XmlReflector.class.getName(), "Can not access field named " + fieldName);

		} catch (InstantiationException e) {
			Log.w(XmlReflector.class.getName(), "Can not create an instance of the type defined in the field named " + fieldName);
		}
	}

	private static void processSingleField(Object model, Field field, Node node) throws IllegalArgumentException, IllegalAccessException, InstantiationException {

		Class<?> type = field.getType();

		field.set(model, getObject(node, type));
	}

	private static Object getObject(Node node, Class<?> type) throws InstantiationException {

		if (type == String.class) {
			return node.text;

		} else if (type == boolean.class || type == Boolean.class) {
			return Boolean.valueOf(node.text);

		} else if (type == int.class || type == Integer.class) {
			return Integer.valueOf(node.text);

		} else if (type == double.class || type == Double.class) {
			return Double.valueOf(node.text);

		} else {
			Object obj;
			try {
				obj = type.newInstance();

			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}

			reflectXmlObject(obj, node);

			return obj;
		}
	}

	private static void processListField(Object object, Field field, Node node) throws IllegalArgumentException, IllegalAccessException, InstantiationException {

		ParameterizedType genericType = (ParameterizedType) field.getGenericType();
		Class<?> childrenType = (Class<?>) genericType.getActualTypeArguments()[0];

		field.set(object, getList(node, childrenType));
	}

	private static List<?> getList(Node node, Class<?> childrenType) throws InstantiationException {

		List<Object> list = new Vector<Object>(node.subnodes.size());

		for (int i = 0; i < node.subnodes.size(); i++) {

			Object child = null;
			Node subnode = node.subnodes.get(i);

			if (childrenType == List.class) {
				child = getList(subnode, childrenType);

			} else {
				child = getObject(subnode, childrenType);
			}

			list.add(child);
		}

		return list;
	}
}
