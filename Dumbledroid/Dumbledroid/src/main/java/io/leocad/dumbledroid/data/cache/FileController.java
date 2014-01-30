package io.leocad.dumbledroid.data.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

public class FileController {

	private static final String DUMBLEDROID_CACHE_DIR = "dumbledroid";

	private final Context mContext;

	public FileController(Context ctx) {
		mContext = ctx;
	}

	public FileInputStream getFileInputStream(String fileName) throws FileNotFoundException {
		final File file = new File(getCacheDir(), fileName);
		return new FileInputStream(file);
	}

	public FileOutputStream getFileOutputStream(String fileName) throws FileNotFoundException {
		final File file = new File(getCacheDir(), fileName);
		file.getParentFile().mkdirs();
		return new FileOutputStream(file);
	}

	public void erase(String fileName) {
		final File file = new File(getCacheDir(), fileName);
		file.delete();
	}

	private File getCacheDir() {
		final File cacheDir = isExternalStorageAvailable() ? getExternalCacheDir() : mContext.getCacheDir();
		return new File(cacheDir, DUMBLEDROID_CACHE_DIR);
	}

	@TargetApi(8)
	private File getExternalCacheDir() {
		if (Build.VERSION.SDK_INT >= 8) {
			final File externalCacheDir = mContext.getExternalCacheDir();
			if(externalCacheDir != null) {
				return externalCacheDir;
			}
		}
		final String cacheDirPath = "Android" + File.separator + "data" + File.separator + mContext.getPackageName() + File.separator + "cache";
		return new File(Environment.getExternalStorageDirectory(), cacheDirPath);
	}

	private static boolean isExternalStorageAvailable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !isExternalStorageRemovable();
	}

	@TargetApi(9)
	private static boolean isExternalStorageRemovable() {
		if (Build.VERSION.SDK_INT >= 9) {
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}
}