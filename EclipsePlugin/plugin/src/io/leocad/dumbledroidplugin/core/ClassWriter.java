package io.leocad.dumbledroidplugin.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;

public class ClassWriter {

	public static void appendPackageDeclaration(StringBuffer fileBuffer, IFile file) {
		fileBuffer.append("package ")
		.append(FileUtils.getPackageName(file))
		.append(";\n\n");
	}

	public static void appendImportStatements(StringBuffer fileBuffer, boolean isAbstractModel) {

		if (isAbstractModel) {
			fileBuffer.append("import io.leocad.dumbledroid.data.AbstractModel;\n")
			.append("import io.leocad.dumbledroid.data.DataType;\n\n");

		} else {
			fileBuffer.append("import java.io.Serializable;\n\n");
		}
	}

	public static void appendClassDeclaration(StringBuffer fileBuffer, IFile file, boolean isAbstractModel) {

		fileBuffer.append("public class ")
		.append(FileUtils.getFileNameWithoutExtension(file))
		.append(isAbstractModel? " extends AbstractModel": " implements Serializable")
		.append(" {\n");
	}
	
	public static void appendFieldDeclaration(StringBuffer fileBuffer, String fieldName, String fieldTypeName, boolean isPojo, StringBuffer gettersBuffer, StringBuffer settersBuffer) {
		
		fileBuffer.append("\n    ")
		.append(isPojo? "public " : "private ")
		
		.append(fieldTypeName)
		.append(" ").append(fieldName).append(";");
		
		// Buffer the accessor methods for posterior writing
		if (!isPojo) {
			
			String fieldCamelCase = uppercaseFirstChar(fieldName);
			
			gettersBuffer.append("\n    public ")
			.append(fieldTypeName)
			.append(" get").append(fieldCamelCase)
			.append("() {\n        return this.").append(fieldName).append(";\n    }\n");
			
			settersBuffer.append("\n    public void set")
			.append(fieldCamelCase)
			.append("(")
			.append(fieldTypeName)
			.append(" ")
			.append(fieldName)
			.append(") {\n        this.")
			.append(fieldName)
			.append(" = ")
			.append(fieldName)
			.append(";\n    }\n");
		}
	}

	public static void appendConstructor(StringBuffer fileBuffer, IFile file, String url, long cacheDuration, boolean isAbstractModel) {
		
		if (isAbstractModel) {
			fileBuffer.append("\n    public ")
			.append(FileUtils.getFileNameWithoutExtension(file))
			.append("() {\n        super(\"")
			.append(url)
			.append("\"");
	
			if (cacheDuration > 0) {
				fileBuffer.append(", ")
				.append(cacheDuration);
			}
	
			fileBuffer.append(");\n    }\n");
		}
	}
	
	public static void appendOverridenLoad(StringBuffer fileBuffer, String urlQueryString, boolean isAbstractModel) {

		if (isAbstractModel && urlQueryString != null) {
			
			appendImport(fileBuffer, "import android.content.Context;");
			appendImport(fileBuffer, "import java.util.List;");
			appendImport(fileBuffer, "import java.util.Vector;");
			appendImport(fileBuffer, "import org.apache.http.NameValuePair;");
			appendImport(fileBuffer, "import org.apache.http.message.BasicNameValuePair;");
			
			Map<String, String> params = queryToParams(urlQueryString);
			StringBuffer paramsAddBuffer = new StringBuffer();
			
			fileBuffer.append("\n    public void load(Context context, ");
			
			Set<String> keySet = params.keySet();
			for (String key : keySet) {
				
				String value = params.get(key);
				String type = ClassMapper.getPrimitiveTypeNameByCasting(value);
				
				fileBuffer.append(type == null? "String": type)
				.append(" ").append(key).append(", ");
				
				paramsAddBuffer.append("        params.add( new BasicNameValuePair(\"")
				.append(key).append("\", ");
				
				if (type != null) { // Primitive type
					paramsAddBuffer.append("String.valueOf(").append(key).append(")");
				} else {
					paramsAddBuffer.append(key);
				}
				
				paramsAddBuffer.append(") );\n");
			}
			// Remove last comma and space
			fileBuffer.delete(fileBuffer.length() -2, fileBuffer.length());
			
			fileBuffer.append(") throws Exception {\n\n        List<NameValuePair> params = new Vector<NameValuePair>();\n");
			fileBuffer.append(paramsAddBuffer);
			fileBuffer.append("\n        super.load(context, params);\n    }\n");
		}
	}

	public static void appendAccessorMethods(StringBuffer fileBuffer, StringBuffer gettersBuffer, StringBuffer settersBuffer) {

		fileBuffer.append(gettersBuffer)
		.append(settersBuffer);
	}

	public static void appendInheritAbstractMethods(StringBuffer fileBuffer, boolean isJson, boolean isAbstractModel) {

		if (isAbstractModel) {
			fileBuffer.append("\n    @Override\n    protected DataType getDataType() {\n        return DataType.")
			.append(isJson? "JSON": "XML")
			.append(";\n    }\n");
		}
	}

	public static void appendClassEnd(StringBuffer fileBuffer) {
		
		fileBuffer.append("}");
	}
	
	public static String uppercaseFirstChar(String fieldName) {
		
		return Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
	}

	public static void appendListImport(StringBuffer fileBuffer) {
		appendImport(fileBuffer, "import java.util.List;");
	}
	
	private static void appendImport(StringBuffer fileBuffer, String importString) {
		
		if (fileBuffer.indexOf(importString) == -1) {
			
			//Find the position of the first import and put just before that.
			int importPos = fileBuffer.indexOf("import");
			
			fileBuffer.insert(importPos -1, "\n" + importString);
		}
	}

	public static String getArrayChildTypeName(String fieldName) {

		fieldName = uppercaseFirstChar(fieldName);
		
		if (fieldName.endsWith("s")) {
			return fieldName.substring(0, fieldName.length() -1);
		
		} else {
			return fieldName + "Item";
		}
	}
	
	private static Map<String, String> queryToParams(String urlQueryString) {

		Map<String,String> map = new HashMap<String, String>();
		String[] keysAndValues = urlQueryString.split("&");
		
		for (String keyValue : keysAndValues) {
			
			String[] keyValueSplit = keyValue.split("=");
			map.put(keyValueSplit[0], keyValueSplit[1]);
		}
		
		return map;
	}
}
