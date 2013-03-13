package io.leocad.dumbledroidplugin.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class FileUtils {
	
	public static String getPackageName(IFile file) {
		
		return file.getProjectRelativePath()
			.toString()
			.replace("src/", "")
			.replace('/', '.');
	}

	public static void write(IFile file, String string, IProgressMonitor monitor) {
		
		ByteArrayInputStream is = new ByteArrayInputStream(string.getBytes());
		
		try {
			file.setContents(is, IFile.FORCE, monitor);
		} catch (CoreException e) {
			
			e.printStackTrace();
		}
		
		try {
			is.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

}
