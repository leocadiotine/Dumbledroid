package io.leocad.dumbledroid.data;

import io.leocad.dumbledroid.data.xml.Node;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Vector;

import android.util.Log;

public class XmlReflector {

	public static void reflectXmlRootNode(Object model, Node node) throws IllegalArgumentException, IllegalAccessException, InstantiationException {

		//Check if the root node is an array
		Class<?> modelClass = model.getClass();
		Field rootNodeField = null;

		try {
			rootNodeField = modelClass.getDeclaredField(node.name);
			rootNodeField.setAccessible(true);

		} catch (NoSuchFieldException e) {
		}

		//The developer has declared a field to match the root node
		if (rootNodeField != null) {

			if (rootNodeField.getType() == List.class) {
				processListField(model, rootNodeField, node, null);
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

		//Treat attributes like fields
		if (node.attributes != null) {
			for (String key : node.attributes.keySet()) {

				String value = node.attributes.get(key);


				try {
					Field field = modelClass.getDeclaredField(key);
					field.setAccessible(true);

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
				} catch (NoSuchFieldException e) {
					Log.w(XmlReflector.class.getName(), "Can not locate field named " + key);

				} catch (IllegalAccessException e) {
					Log.w(XmlReflector.class.getName(), "Can not access field named " + key);

				}
			}
		}

		if (node.subnodes != null) {

			for (int i = 0; i < node.subnodes.size(); i++) {

				Node subnode = node.subnodes.get(i);
				accessFieldAndProcess(model, modelClass, subnode, node);
			}

		} else {
			//This node has no children (hope it has a wife).
			accessFieldAndProcess(model, modelClass, node, null);
		}
	}

	private static void accessFieldAndProcess(Object model, Class<?> modelClass, Node node, Node parentNode) {

		try {
			Field field = modelClass.getDeclaredField(node.name);
			field.setAccessible(true);

			if (field.getType() == List.class) {
				processListField(model, field, node, parentNode);

			} else {
				processSingleField(model, field, node);
			}

		} catch (NoSuchFieldException e) {
			Log.w(XmlReflector.class.getName(), "Can not locate field named " + node.name);

		} catch (IllegalAccessException e) {
			Log.w(XmlReflector.class.getName(), "Can not access field named " + node.name);

		} catch (InstantiationException e) {
			Log.w(XmlReflector.class.getName(), "Can not create an instance of the type defined in the field named " + node.name);
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

	private static void processListField(Object object, Field field, Node node, Node parentNode) throws IllegalArgumentException, IllegalAccessException, InstantiationException {

		ParameterizedType genericType = (ParameterizedType) field.getGenericType();
		Class<?> childrenType = (Class<?>) genericType.getActualTypeArguments()[0];

		field.set(object, getList(node, childrenType, parentNode));
	}

	private static List<?> getList(Node node, Class<?> childrenType, Node parentNode) throws InstantiationException {

		/*
		 * First, we need to determine which kind of list is this. In XML, we can have two kinds:
		 * 
		 * Type 1:
		 * <root>
		 *     <listChild>
		 *         <childMember1 />
		 *         <childMember2 />
		 *     </listChild>
		 *     <listChild>
		 *         <childMember1 />
		 *         <childMember2 />
		 *     </listChild>
		 * </root>
		 * 
		 * Type 2:
		 * <root>
		 *     <list>
		 *         <listChild>
		 *             <childMember1 />
		 *             <childMember2 />
		 *         </listChild>
		 *     </list>
		 * </root>
		 * 
		 * To determine that, we must check if a node has siblings with the same name as it. If so,
		 * it's a list of the type 1.
		 */

		List<Node> listNodes;
		if (parentNode == null) {
			//This is a root node and has no siblings. So it can only be a list of type 2
			listNodes = node.subnodes;
		} else {

			//Defaults to type 1
			listNodes = parentNode.getChildrenByName(node.name);

			if (listNodes.size() <= 1) {
				//Type 2
				listNodes = node.subnodes; 
				parentNode = node;
			}
		}

		if (listNodes != null) {

			List<Object> list = new Vector<Object>(listNodes.size());

			for (int i = 0; i < listNodes.size(); i++) {

				Object child = null;
				Node subnode = listNodes.get(i);

				if (childrenType == List.class) {
					child = getList(subnode, childrenType, parentNode);

				} else {
					child = getObject(subnode, childrenType);
				}

				list.add(child);
			}

			return list;
			
		} else {
			Log.w(XmlReflector.class.getName(), "The field named " + node.name + " has no children nor namesakes and has been declared as a List. Will be null.");
			return null;
		}
	}
}
