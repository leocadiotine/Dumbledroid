package io.leocad.dumbledroidplugin.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class FileUtils {

	public static String getPackageName(IFile file) {

		String packageString = file.getProjectRelativePath()
				.toString()
				.replace("src/", "")
				.replace('/', '.');

		//Remove file name
		return packageString.substring(0, packageString.length() - file.getName().length() - 1);
	}

	public static String getFileNameWithoutExtension(IFile file) {

		return file.getName().substring(0, file.getName().length() - file.getFileExtension().length() - 1);
	}

	public static void write(IFile file, String contents) {
		doWrite(file, contents, false);
	}

	public static void create(IFile file) {
		doWrite(file, "", true);
	}

	private static void doWrite(IFile file, String contents, boolean create) {

		ByteArrayInputStream is = new ByteArrayInputStream(contents.getBytes());

		try {
			if (create) {
				file.create(is, true, null);
				
			} else {
				file.setContents(is, true, false, null);
			}
			
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
