package io.leocad.dumbledroid.data.cache;

import io.leocad.dumbledroid.data.AbstractModel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.util.Log;

public class DiskCache {

	private static final String TAG = "DiskCache";

	private static DiskCache sInstance;
	/**
	 * @param ctx ALWAYS USE getApplicationContext() here.
	 * @return
	 */
	public static DiskCache getInstance(Context context) {
		if (sInstance == null) {
			synchronized (DiskCache.class) {
				if (sInstance == null) {
					sInstance = new DiskCache(context);
				}
			}
		}
		return sInstance;
	}

	private final FileController mFileCtrl;

	private DiskCache(Context ctx) {
		mFileCtrl = new FileController(ctx);
	}

	public void cache(String key, AbstractModel model) {
		final String fileName = getHashKey(key);
		final ModelHolder holder = new ModelHolder(model, System.currentTimeMillis());

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			fos = mFileCtrl.getFileOutputStream(fileName);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(holder);
		} catch (IOException e) {
			Log.w(TAG, "The file " + fileName + " couldn't be cached. Does this app have permission to ACCESS_EXTERNAL_STORAGE ?", e);
		} finally {
			if(fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					Log.w(TAG, Log.getStackTraceString(e));
				}
			}
			if(oos != null) {
				try {
					oos.flush();
					oos.close();
				} catch (IOException e) {
					Log.w(TAG, Log.getStackTraceString(e));
				}
			}
		}
	}

	public ModelHolder getCached(String key) {
		final String fileName = getHashKey(key);

		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = mFileCtrl.getFileInputStream(fileName);
			ois = new ObjectInputStream(fis);
			final ModelHolder holder = (ModelHolder) ois.readObject();
			return holder;
		} catch (IOException e) {
			Log.w(TAG, "Can't access file: " + fileName, e);
		} catch (ClassNotFoundException e) {
			Log.w(TAG, "Can't read object from file: " + fileName, e);
		} finally {
			if(fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					Log.w(TAG, Log.getStackTraceString(e));
				}
			}
			if(ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					Log.w(TAG, Log.getStackTraceString(e));
				}
			}
		}
		return null;
	}

	private static String getHashKey(String key) {
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
			mDigest.update(key.getBytes());
			return bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {}
		return String.valueOf(key.hashCode());
	}

	private static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < bytes.length; i++) {
			final String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}
}