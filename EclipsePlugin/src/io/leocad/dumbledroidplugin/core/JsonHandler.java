package io.leocad.dumbledroidplugin.core;

import io.leocad.dumbledroidplugin.exceptions.InvalidContentException;
import io.leocad.dumbledroidplugin.exceptions.InvalidUrlException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonHandler {

	public static Map<String, String> parseJsonToFiles(HttpURLConnection connection, boolean isPojo, String className) throws InvalidUrlException, InvalidContentException {
		
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
		
		Map<String, String> fileMap = new HashMap<String, String>();
		
		if (className == null || className.trim().equals("")) {
			className = "DumbledroidJsonModel";
		}

		if (jsonObj != null) {
			processObjectFileMap(jsonObj, fileMap, isPojo, className);
		} else {
			processArrayFileMap(jsonArray, fileMap, isPojo, className);
		}
		
		return fileMap;
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
	
	private static void processObjectFileMap(JSONObject jsonObj, Map<String, String> fileMap, boolean isPojo, String className) {
		
		StringBuffer fileBuffer = new StringBuffer();
		StringBuffer gettersBuffer = new StringBuffer();
		StringBuffer settersBuffer = new StringBuffer();
		
		fileBuffer.append("public class ").append(className).append(" {\n");
		
		@SuppressWarnings("unchecked")
		Iterator<String> keys = jsonObj.keys();
		while (keys.hasNext()) {
			
			String key = keys.next();
			Object object = jsonObj.get(key);
			
			fileBuffer.append("\n    ")
			.append(isPojo? "public " : "private ")
			
			// TODO Handle JSONObject & JSONArray
			.append(object.getClass().getSimpleName())
			.append(" ").append(key).append(";");
			
			//Accessor methods
			if (!isPojo) {
				
				String keyCamelCase = Character.toUpperCase(key.charAt(0)) + key.substring(1);
				
				gettersBuffer.append("\n    public ")
				.append(object.getClass().getSimpleName())
				.append(" get").append(keyCamelCase)
				.append("() {\n        return this.").append(key).append(";\n    }\n");
				
				settersBuffer.append("\n    public void set")
				.append(keyCamelCase)
				.append("(")
				.append(object.getClass().getSimpleName())
				.append(" ")
				.append(key)
				.append(") {\n        this.")
				.append(key)
				.append(" = ")
				.append(key)
				.append(";\n    }\n");
			}
		}
		
		fileBuffer.append("\n")
		.append(gettersBuffer)
		.append(settersBuffer)
		.append("\n}");
		fileMap.put(className, fileBuffer.toString());
		System.out.println(fileBuffer.toString());
	}
	
	private static void processArrayFileMap(JSONArray jsonArray, Map<String, String> fileMap, boolean isPojo, String className) {
		
	}
}
