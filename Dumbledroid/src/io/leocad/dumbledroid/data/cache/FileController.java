package io.leocad.dumbledroid.data.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

public class FileController {

	private static String EXTERNAL_DATA_DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/data/data/";

	private final Context mContext;
	private String mExternalDataDirectory;

	public FileController(Context ctx) {
		mContext = ctx;

		if (isSdCardAvailable()) {
			mExternalDataDirectory = EXTERNAL_DATA_DIRECTORY + mContext.getPackageName() + "/";

			//Create the directory (if it doesn't exists)
			File directory = new File(mExternalDataDirectory);
			if (!directory.exists()) {
				directory.mkdirs();
			}
		}
	}

	public FileInputStream getFileInputStream(String filename) throws FileNotFoundException {
		FileInputStream fis = null;

		if (isSdCardAvailable()) {
			fis = new FileInputStream( new File(mExternalDataDirectory + filename) );
		} else {
			fis = mContext.openFileInput(filename);
		}

		return fis;
	}

	public FileOutputStream getFileOutputStream(String filename) throws IOException {
		FileOutputStream fos = null;

		if (isSdCardAvailable()) {

			File file = new File(mExternalDataDirectory + filename);

			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);

		} else {
			fos = mContext.openFileOutput(filename, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
		}

		return fos;
	}

	public void erase(String fileName) {
		File file = null;

		if (isSdCardAvailable()) {
			file = new File(mExternalDataDirectory + fileName);

		} else  {
			file = mContext.getFileStreamPath(fileName);
		}

		file.delete();
	}

	private static boolean isSdCardAvailable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}
}
