package io.leocad.dumbledroidplugin.core;

import io.leocad.dumbledroidplugin.exceptions.InvalidContentException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
		// Twin children are sibling elements with the same name. We don't want to proccess all of them, just the first.
		List<String> twinChildren = new ArrayList<String>();
		
		while (iterator.hasNext()) {
			
			Element child = (Element) iterator.next();
			String key = child.getName();
			
			if (twinChildren.contains(key)) {
				continue;
			}
			
			String fieldTypeName;
			
			if (child.isTextOnly() && child.attributeCount() == 0) {
				
				// Check for Type 1 primitive arrays (see getArrayChild())
				Element arrayChild = getArrayChild(child);
				
				if (arrayChild == null) {
					
					// Not an array
					fieldTypeName = ClassMapper.getPrimitiveTypeNameByCasting(child.getStringValue());
					if (fieldTypeName == null) {
						fieldTypeName = "String";
					}
				
				} else {
					// Array of primitive types
					ClassWriter.appendListImport(fileBuffer);
					
					fieldTypeName = ClassMapper.getWrapperTypeNameByCasting(child.getStringValue());
					if (fieldTypeName == null) {
						fieldTypeName = "String";
					}
					fieldTypeName = String.format("List<%s>", fieldTypeName);
					
					// Mark the twin nodes, so the reverse reflection don't count them again
					twinChildren.add(key);
				}
				
			} else {
				// Not a primitive. Recursion ahead.
				fieldTypeName = mapField(file, child, fileBuffer, key, isPojo, cacheDuration, twinChildren);
			}

			ClassWriter.appendFieldDeclaration(fileBuffer, key, fieldTypeName, isPojo, gettersBuffer, settersBuffer);
		}
		
		// There is a special type of XML nodes with attributes, no children but a value.
		// Something like this: <generator uri="http://www.flickr.com/">Flickr</generator>
		// This kind of object won't be qualified as a primitive type nor will enter the
		// loop above. Thus, we need to check specifically for them:
		if (element.attributeCount() > 0 && element.isTextOnly()) {
			
			final String stringValue = element.getStringValue();
			if (stringValue != null && !stringValue.trim().equals("")) {
				
				String fieldTypeName = ClassMapper.getPrimitiveTypeNameByCasting(stringValue);
				if (fieldTypeName == null) {
					fieldTypeName = "String";
				}
				ClassWriter.appendFieldDeclaration(fileBuffer, "value", fieldTypeName, isPojo, gettersBuffer, settersBuffer);
			}
		}
		fileBuffer.append("\n");
		
		ClassWriter.appendConstructor(fileBuffer, file, urlAddress, cacheDuration, isAbstractModel);
		ClassWriter.appendAccessorMethods(fileBuffer, gettersBuffer, settersBuffer);
		ClassWriter.appendInheritAbstractMethods(fileBuffer, false, isAbstractModel);
		ClassWriter.appendClassEnd(fileBuffer);

		FileUtils.write(file, fileBuffer.toString());
	}
	
	private static String mapField(IFile file, Element element, StringBuffer fileBuffer, String key, boolean isPojo, long cacheDuration, List<String> twinChildren) {

		String fieldTypeName;
		if (element.hasContent()) {
			
			Element arrayChild = getArrayChild(element);
			
			if (arrayChild == null) {

				// Nested object (not an array)
				fieldTypeName = ClassWriter.uppercaseFirstChar(key);
				
				IFile newFile = file.getParent().getFile(new Path(fieldTypeName + ".java"));
				FileUtils.create(newFile);
				processXmlObjectFile((Element) element, false, null, isPojo, cacheDuration, newFile);
				
			} else {
				// XML array
				ClassWriter.appendListImport(fileBuffer);
				
				if (!arrayChild.isTextOnly()) { // Non-primitive type

					final String childTypeName;
					
					// If the array is of type 1 (see getArrayChild()), the childTypeName is based on the name of the element.
					if (arrayChild == element) { //Type 1
						childTypeName = ClassWriter.uppercaseFirstChar(key);
						twinChildren.add(key);
						
					} else {
						// But if it's of type 2, the key is the name of the child itself
						childTypeName = ClassWriter.uppercaseFirstChar( arrayChild.getName() );
					}
					
					fieldTypeName = String.format("List<%s>", childTypeName);
	
					//Create files for the children
					IFile newFile = file.getParent().getFile(new Path(childTypeName + ".java"));
					FileUtils.create(newFile);
					processXmlObjectFile(arrayChild, false, null, isPojo, cacheDuration, newFile);

				} else {
					fieldTypeName = ClassMapper.getWrapperTypeNameByCasting(arrayChild.getStringValue());
					if (fieldTypeName == null) {
						fieldTypeName = "String";
					}
					
					fieldTypeName = String.format("List<%s>", fieldTypeName);
				}
			}

		} else {
			// No children
			
			if (element.attributeCount() > 0) {
				// An empty element with attributes is like a nested object
				fieldTypeName = ClassWriter.uppercaseFirstChar(key);
				
				IFile newFile = file.getParent().getFile(new Path(fieldTypeName + ".java"));
				FileUtils.create(newFile);
				processXmlObjectFile((Element) element, false, null, isPojo, cacheDuration, newFile);
				
				// And like the other nested objects, we need to check if it's part of an array,
				if (getArrayChild(element) != null) {
					ClassWriter.appendListImport(fileBuffer);
					fieldTypeName = String.format("List<%s>", fieldTypeName);
					twinChildren.add(element.getName());
				}
			
			} else {
				fieldTypeName = "String";
			}
		}
		return fieldTypeName;
	}
	
	private static Element getArrayChild(Element element) {
		/*
		 * In XML, we can have two kinds of arrays:
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
		 * To determine that, we must check if an element has siblings with the same name as it. If so,
		 * it's an array of the type 1.
		 */
		
		// Type 1: element with siblings with the same name
		String name = element.getName();
		Element parent = element.getParent();
		if (parent.elements(name).size() > 1) {
			return element;
		}
		
		// Type 2: element with children with the same name
		@SuppressWarnings("unchecked")
		List<Element> children = element.elements();
		
		if (!children.isEmpty()) {
			
			Element child = children.get(0);
			
			if (child != null && element.elements(child.getName()).size() > 1) {
				return child;
			}
		}
		
		// Not an array
		return null;
	}
}
