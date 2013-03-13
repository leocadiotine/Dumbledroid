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

public class JsonHandler {

	public static void parseJsonToFiles(HttpURLConnection connection, String url, boolean isPojo, IFile file, IProgressMonitor monitor) throws InvalidUrlException, InvalidContentException {
		
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
			processObjectFileMap(jsonObj, url, isPojo, file, monitor);
		} else {
			processArrayFileMap(jsonArray, url, isPojo, file, monitor);
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
	
	private static void processObjectFileMap(JSONObject jsonObj, String url, boolean isPojo, IFile file, IProgressMonitor monitor) {
		
		StringBuffer fileBuffer = new StringBuffer();
		StringBuffer gettersBuffer = new StringBuffer();
		StringBuffer settersBuffer = new StringBuffer();
		
		// Package declaration
		fileBuffer.append("package ")
		.append(FileUtils.getPackageName(file))
		.append(";\n\n")
		
		// Import statements
		.append("import io.leocad.dumbledroid.data.AbstractModel;\n")
		.append("import io.leocad.dumbledroid.data.DataType;\n\n");
		
		// Class declaration
		final String className = FileUtils.getFileNameWithoutExtension(file);
		fileBuffer.append("public class ")
		.append(className)
		.append(" extends AbstractModel {\n");
		
		// Fields declaration
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
		
		// Constructor
		// TODO Support cache
		.append("\n    public ")
		.append(className)
		.append("() {\n        super(\"")
		.append(url)
		.append("\");\n    }\n")

		// Accessor methods
		.append(gettersBuffer)
		.append(settersBuffer)
		
		// Inherited abstract methods
		.append("\n    @Override\n    protected DataType getDataType() {\n        return DataType.JSON;\n    }\n")
		
		// Class end
		.append("}");
		
		monitor.worked(1);
		monitor.setTaskName("Writing file(s)…");
		
		FileUtils.write(file, fileBuffer.toString(), monitor);
	}
	
	private static void processArrayFileMap(JSONArray jsonArray, String url, boolean isPojo, IFile file, IProgressMonitor monitor) {
		
	}
}
