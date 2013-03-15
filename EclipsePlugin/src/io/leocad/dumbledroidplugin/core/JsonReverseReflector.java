package io.leocad.dumbledroidplugin.core;

import io.leocad.dumbledroidplugin.exceptions.InvalidContentException;
import io.leocad.dumbledroidplugin.exceptions.InvalidUrlException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonReverseReflector {

	public static void parseJsonToFiles(InputStream is, String url, boolean isPojo, long cacheDuration, IFile file) throws InvalidUrlException, InvalidContentException {

		String jsonString = getJsonString(is);

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
				throw new InvalidContentException("JSON");
			}
		}

		if (jsonObj != null) {
			processJsonObjectFile(jsonObj, true, url, isPojo, cacheDuration, file);
		} else {
			processJsonArrayFile(jsonArray, true, url, isPojo, cacheDuration, file);
		}
	}

	private static String getJsonString(InputStream is) throws InvalidUrlException {

		StringBuilder total = new StringBuilder();

		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(is));

			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}


		} catch (IOException e) {
			throw new InvalidUrlException();
		}

		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return total.toString();
	}

	private static void processJsonObjectFile(JSONObject jsonObj, boolean isAbstractModel, String url, boolean isPojo, long cacheDuration, IFile file) {

		StringBuffer fileBuffer = new StringBuffer();
		StringBuffer gettersBuffer = new StringBuffer();
		StringBuffer settersBuffer = new StringBuffer();

		ClassWriter.appendPackageDeclaration(fileBuffer, file);
		ClassWriter.appendImportStatements(fileBuffer, isAbstractModel);
		ClassWriter.appendClassDeclaration(fileBuffer, file, isAbstractModel);

		// Fields declaration
		@SuppressWarnings("unchecked")
		Iterator<String> keys = jsonObj.keys();
		while (keys.hasNext()) {

			String key = keys.next();
			Object object = jsonObj.get(key);
			String fieldTypeName = ClassMapper.getPrimitiveTypeName(object);

			if (fieldTypeName == null) {
				// Not a primitive. Recursion ahead.

				if (object instanceof JSONObject) {

					fieldTypeName = ClassWriter.uppercaseFirstChar(key);

					IFile newFile = file.getParent().getFile(new Path(fieldTypeName + ".java"));
					FileUtils.create(newFile);
					processJsonObjectFile((JSONObject) object, false, null, isPojo, cacheDuration, newFile);

				} else if (object instanceof JSONArray) {

					ClassWriter.appendListImport(fileBuffer);

					JSONArray array = (JSONArray) object;
					Object child = array.get(0);

					if (child == null) { // Empty array
						fieldTypeName = "List<Object>";

					} else if (child instanceof JSONObject) { // Non-primitive type
						final String childTypeName = ClassWriter.getArrayChildTypeName(key);
						fieldTypeName = String.format("List<%s>", childTypeName);

						//Create files for the children
						IFile newFile = file.getParent().getFile(new Path(childTypeName + ".java"));
						FileUtils.create(newFile);
						processJsonObjectFile((JSONObject) child, false, null, isPojo, cacheDuration, newFile);

					} else {
						fieldTypeName = String.format("List<%s>", child.getClass().getSimpleName());
					}

				} else {
					//Unknown class
					fieldTypeName = object.getClass().getSimpleName();
				}
			}

			ClassWriter.appendFieldDeclaration(fileBuffer, key, fieldTypeName, isPojo, gettersBuffer, settersBuffer);
		}
		fileBuffer.append("\n");

		ClassWriter.appendConstructor(fileBuffer, file, url, cacheDuration, isAbstractModel);
		ClassWriter.appendAccessorMethods(fileBuffer, gettersBuffer, settersBuffer);
		ClassWriter.appendInheritAbstractMethods(fileBuffer, true, isAbstractModel);
		ClassWriter.appendClassEnd(fileBuffer);

		FileUtils.write(file, fileBuffer.toString());
	}

	private static void processJsonArrayFile(JSONArray jsonArray, boolean isAbstractModel, String url, boolean isPojo, long cacheDuration, IFile file) {

	}
}
