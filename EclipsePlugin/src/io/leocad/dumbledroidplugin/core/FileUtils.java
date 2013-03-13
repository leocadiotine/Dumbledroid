package io.leocad.dumbledroidplugin.core;

import org.eclipse.core.resources.IFile;

public class FileUtils {
	
	public static String getPackageName(IFile file) {
		
		return file.getProjectRelativePath()
			.toString()
			.replace("src/", "")
			.replace('/', '.');
	}

}
