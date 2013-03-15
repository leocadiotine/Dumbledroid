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
			Object object = attribute.getData();
			String fieldTypeName = ClassMapper.getPrimitiveTypeName(object); // Attributes can only be primitives
			
			ClassWriter.appendFieldDeclaration(fileBuffer, key, fieldTypeName, isPojo, gettersBuffer, settersBuffer);
		}
		
		fileBuffer.append("\n");
		
		ClassWriter.appendConstructor(fileBuffer, file, urlAddress, cacheDuration, isAbstractModel);
		ClassWriter.appendAccessorMethods(fileBuffer, gettersBuffer, settersBuffer);
		ClassWriter.appendInheritAbstractMethods(fileBuffer, false, isAbstractModel);
		ClassWriter.appendClassEnd(fileBuffer);

		FileUtils.write(file, fileBuffer.toString());
	}
}
