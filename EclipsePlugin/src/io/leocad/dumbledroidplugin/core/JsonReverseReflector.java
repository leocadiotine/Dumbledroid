package io.leocad.dumbledroidplugin.core;

import io.leocad.dumbledroidplugin.exceptions.InvalidContentException;
import io.leocad.dumbledroidplugin.exceptions.InvalidUrlException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonReverseReflector {

	public static void parseJsonToFiles(HttpURLConnection connection, String url, boolean isPojo, long cacheDuration, IFile file, IProgressMonitor monitor) throws InvalidUrlException, InvalidContentException {
		
		String jsonString = getJsonString(connection);
		
		JSONObject jsonObj = null;
		JSONArray jsonArray = null;
		
		try {
			jsonObj = new JSONObject(jsonString);
		
		} catch (JSONException e) {
			//Not a JsonObject. Try an array…
			try {
				jsonArray = new JSONArray(jsonString);
				
			} catch (JSONException e2) {
				//Not a valid JSON.
				throw new InvalidContentException("json");
			}
		}
		
		if (jsonObj != null) {
			processObjectFileMap(jsonObj, url, isPojo, cacheDuration, file, monitor);
		} else {
			processArrayFileMap(jsonArray, url, isPojo, cacheDuration, file, monitor);
		}
	}

	private static String getJsonString(HttpURLConnection connection) throws InvalidUrlException {

		try {
			InputStream is = connection.getInputStream();

			BufferedReader r = new BufferedReader(new InputStreamReader(is));
			
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			
			return total.toString();
			
		} catch (IOException e) {
			throw new InvalidUrlException();
		}
	}
	
	private static void processObjectFileMap(JSONObject jsonObj, String url, boolean isPojo, long cacheDuration, IFile file, IProgressMonitor monitor) {
		
		StringBuffer fileBuffer = new StringBuffer();
		StringBuffer gettersBuffer = new StringBuffer();
		StringBuffer settersBuffer = new StringBuffer();
		
		ClassWriter.appendPackageDeclaration(fileBuffer, file);
		ClassWriter.appendImportStatements(fileBuffer, true);
		ClassWriter.appendClassDeclaration(fileBuffer, file, true);
		
		// Fields declaration
		@SuppressWarnings("unchecked")
		Iterator<String> keys = jsonObj.keys();
		while (keys.hasNext()) {
			
			String key = keys.next();
			Object object = jsonObj.get(key);
			// TODO Handle JSONObject & JSONArray
			final String fieldClassName = object.getClass().getSimpleName();
			
			ClassWriter.appendFieldDeclaration(fileBuffer, key, fieldClassName, isPojo, gettersBuffer, settersBuffer);
		}
		fileBuffer.append("\n");
		
		ClassWriter.appendConstructor(fileBuffer, file, url, cacheDuration);
		ClassWriter.appendAccessorMethods(fileBuffer, gettersBuffer, settersBuffer);
		ClassWriter.appendInheritAbstractMethods(fileBuffer, true);
		ClassWriter.appendClassEnd(fileBuffer);
		
		monitor.worked(1);
		monitor.setTaskName("Writing file(s)…");
		
		FileUtils.write(file, fileBuffer.toString(), monitor);
	}
	
	private static void processArrayFileMap(JSONArray jsonArray, String url, boolean isPojo, long cacheDuration, IFile file, IProgressMonitor monitor) {
		
	}
}
