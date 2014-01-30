package io.leocad.dumbledroid.data.xml;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class SaxParser {

	static {
		System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
	}

	public static Node parse(InputStream rawXML, String encoding) throws SAXException, IOException {

		XMLReader reader = XMLReaderFactory.createXMLReader();
		SaxHandler handler = new SaxHandler();
		reader.setContentHandler(handler);
		reader.setErrorHandler(handler);

		InputSource source = new InputSource(rawXML);
		source.setEncoding(encoding.toString());

		reader.parse(source);

		return handler.getRoot();
	}
}
