package io.leocad.dumbledroidplugin.core;

import io.leocad.dumbledroidplugin.exceptions.InvalidContentException;

import java.io.InputStream;
import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;

public class XmlReverseReflector {

	public static void parseXmlToFiles(InputStream is, String urlAddress, boolean isPojo, long cacheDuration, IFile file) throws InvalidContentException {
		
		SAXReader reader = new SAXReader();
		Document document;
		
		try {
			document = reader.read(is);
		} catch (DocumentException e) {
			throw new InvalidContentException("XML");
		}
		
		//The root node is always unique, never an array
		Element rootElement = document.getRootElement();
		processXmlObjectFile(rootElement, true, urlAddress, isPojo, cacheDuration, file);
	}

	private static void processXmlObjectFile(Element element, boolean isAbstractModel, String urlAddress, boolean isPojo, long cacheDuration, IFile file) {
		
		StringBuffer fileBuffer = new StringBuffer();
		StringBuffer gettersBuffer = new StringBuffer();
		StringBuffer settersBuffer = new StringBuffer();

		ClassWriter.appendPackageDeclaration(fileBuffer, file);
		ClassWriter.appendImportStatements(fileBuffer, isAbstractModel);
		ClassWriter.appendClassDeclaration(fileBuffer, file, isAbstractModel);

		// Fields declaration
		// First, the attributes
		@SuppressWarnings("unchecked")
		Iterator<Attribute> attributeIterator = element.attributeIterator();
		while (attributeIterator.hasNext()) {
			Attribute attribute = (Attribute) attributeIterator.next();
			
			String key = attribute.getName();
			String fieldTypeName = ClassMapper.getPrimitiveTypeNameByCasting(attribute.getValue());
			 // Attributes can only be primitives
			if (fieldTypeName == null) {
				fieldTypeName = "String";
			}
			
			ClassWriter.appendFieldDeclaration(fileBuffer, key, fieldTypeName, isPojo, gettersBuffer, settersBuffer);
		}
		
		// Then the child nodes
		@SuppressWarnings("unchecked")
		Iterator<Element> iterator = element.elementIterator();
		while (iterator.hasNext()) {
			
			Element child = (Element) iterator.next();
			String key = child.getName();
			String fieldTypeName;
			
			if (child.isTextOnly()) {
				fieldTypeName = ClassMapper.getPrimitiveTypeNameByCasting(child.getStringValue());
				if (fieldTypeName == null) {
					fieldTypeName = "String";
				}
				
			} else {
				// Not a primitive. Recursion ahead.
				fieldTypeName = mapField(file, child, fileBuffer, key, isPojo, cacheDuration);
			}

			ClassWriter.appendFieldDeclaration(fileBuffer, key, fieldTypeName, isPojo, gettersBuffer, settersBuffer);
		}
		fileBuffer.append("\n");
		
		ClassWriter.appendConstructor(fileBuffer, file, urlAddress, cacheDuration, isAbstractModel);
		ClassWriter.appendAccessorMethods(fileBuffer, gettersBuffer, settersBuffer);
		ClassWriter.appendInheritAbstractMethods(fileBuffer, false, isAbstractModel);
		ClassWriter.appendClassEnd(fileBuffer);

		FileUtils.write(file, fileBuffer.toString());
	}
	
	private static String mapField(IFile file, Element element, StringBuffer fileBuffer, String key, boolean isPojo, long cacheDuration) {

		String fieldTypeName;
		if (element.hasContent()) {

			fieldTypeName = ClassWriter.uppercaseFirstChar(key);

			IFile newFile = file.getParent().getFile(new Path(fieldTypeName + ".java"));
			FileUtils.create(newFile);
			processXmlObjectFile((Element) element, false, null, isPojo, cacheDuration, newFile);

//		} else if (object instanceof JSONArray) { // TODO
//
//			ClassWriter.appendListImport(fileBuffer);
//
//			JSONArray array = (JSONArray) object;
//			Object child = array.get(0);
//
//			if (child == null) { // Empty array
//				fieldTypeName = "List<Object>";
//
//			} else if (child instanceof JSONObject) { // Non-primitive type
//				final String childTypeName = ClassWriter.getArrayChildTypeName(key);
//				fieldTypeName = String.format("List<%s>", childTypeName);
//
//				//Create files for the children
//				IFile newFile = file.getParent().getFile(new Path(childTypeName + ".java"));
//				FileUtils.create(newFile);
//				processJsonObjectFile((JSONObject) child, false, null, isPojo, cacheDuration, newFile);
//
//			} else {
//				fieldTypeName = String.format("List<%s>", child.getClass().getSimpleName());
//			}

		} else {
			// No children
			fieldTypeName = "String";
		}
		return fieldTypeName;
	}
}
