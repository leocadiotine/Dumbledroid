package io.leocad.dumbledroid.data;

import io.leocad.dumbledroid.data.xml.Node;
import io.leocad.dumbledroid.data.xml.SaxParser;
import io.leocad.dumbledroid.net.HttpLoader;
import io.leocad.dumbledroid.net.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.xml.sax.SAXException;

import android.content.Context;


public class DataController {

	public static void load(Context ctx, AbstractModel receiver, DataType dataType, List<NameValuePair> params, HttpMethod method)
			throws Exception {

		InputStream is = HttpLoader.makeRequest(receiver.url, receiver.encoding, params, method);

		switch (dataType) {
		case JSON:
			processJson(receiver, is);
			break;
		
		case XML:
			processXml(receiver, is);
			break;
		}
	}

	private static void processJson(AbstractModel receiver, InputStream is)
			throws IOException, JSONException, InstantiationException {

		String content = HttpLoader.streamToString(is);
		JsonReflector.reflectJsonString(receiver, content);
	}
	
	private static void processXml(AbstractModel receiver, InputStream is) throws SAXException, IOException, SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		
		Node parsedNode = SaxParser.parse(is, receiver.encoding);
		XmlReflector.reflectXmlObject(receiver, parsedNode);
	}
}
