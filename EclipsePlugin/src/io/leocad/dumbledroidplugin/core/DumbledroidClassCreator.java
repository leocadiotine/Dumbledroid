package io.leocad.dumbledroidplugin.core;

import io.leocad.dumbledroidplugin.exceptions.InvalidContentException;
import io.leocad.dumbledroidplugin.exceptions.InvalidUrlException;
import io.leocad.dumbledroidplugin.exceptions.UnsupportedContentTypeException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

public class DumbledroidClassCreator {

	public static void create(String urlAddress, boolean isPojo, long cacheDuration, IFile file, IProgressMonitor monitor) throws UnsupportedContentTypeException, InvalidUrlException, InvalidContentException {

		monitor.beginTask("Validating URL…", 2);

		HttpURLConnection connection = validateAndOpenConnection(urlAddress);

		boolean isJson = isJson(connection);
		monitor.worked(1);

		monitor.setTaskName("Writing files…");
		
		InputStream is;
		try {
			is = connection.getInputStream();
		} catch (IOException e) {
			throw new InvalidUrlException();
		}

		if (isJson) {
			JsonReverseReflector.parseJsonToFiles(is, urlAddress, isPojo, cacheDuration, file);
		} else {
			XmlReverseReflector.parseXmlToFiles(is, urlAddress, isPojo, cacheDuration, file);
		}
		
		monitor.worked(1);
	}

	private static HttpURLConnection validateAndOpenConnection(String urlAddress) throws InvalidUrlException {

		URL url = null;
		try {
			url = new URL(urlAddress);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			// Will never happen. The URL was already validated on UrlInputPage
		}

		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();
		} catch (IOException e) {
			// e.printStackTrace();
			throw new InvalidUrlException();
		}

		return connection;
	}

	private static boolean isJson(HttpURLConnection connection) throws UnsupportedContentTypeException {

		String contentType = connection.getContentType();

		if (contentType.contains("json")) {
			return true;
		} else if (contentType.contains("xml")) {
			return false;
		}

		throw new UnsupportedContentTypeException(contentType);
	}
}
