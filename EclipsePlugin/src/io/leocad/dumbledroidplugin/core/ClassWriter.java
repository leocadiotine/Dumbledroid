package io.leocad.dumbledroidplugin.core;

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
			
			String fieldCamelCase = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
			
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

	public static void appendConstructor(StringBuffer fileBuffer, IFile file, String url, long cacheDuration) {
		
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

	public static void appendAccessorMethods(StringBuffer fileBuffer, StringBuffer gettersBuffer, StringBuffer settersBuffer) {

		fileBuffer.append(gettersBuffer)
		.append(settersBuffer);
	}

	public static void appendInheritAbstractMethods(StringBuffer fileBuffer, boolean isJson) {

		fileBuffer.append("\n    @Override\n    protected DataType getDataType() {\n        return DataType.")
		.append(isJson? "JSON": "XML")
		.append(";\n    }\n");
	}

	public static void appendClassEnd(StringBuffer fileBuffer) {
		
		fileBuffer.append("}");
	}

}
