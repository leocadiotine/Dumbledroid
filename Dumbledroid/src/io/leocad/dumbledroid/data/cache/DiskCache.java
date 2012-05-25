package io.leocad.dumbledroid.data.cache;

import io.leocad.dumbledroid.data.AbstractModel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.util.Log;

public class DiskCache {

	private static final String TAG = "DiskCache";
	
	private static DiskCache INSTANCE;
	/**
	 * @param ctx ALWAYS USE getApplicationContext() here.
	 * @return
	 */
	public static DiskCache getInstance(Context ctx) {
		
		if (INSTANCE == null) {
			INSTANCE = new DiskCache(ctx);
		}
		
		return INSTANCE;
	}
	
	private FileController mFileCtrl;
	
	private DiskCache(Context ctx) {
		mFileCtrl = new FileController(ctx);
	}
	
	public void cache(String key, AbstractModel model) {
		
		String fileName = String.valueOf(key.hashCode());
		
		ModelHolder holder = new ModelHolder(model, System.currentTimeMillis());
		
		try {
			FileOutputStream fos = mFileCtrl.getFileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(holder);
			
		} catch (IOException e) {
			Log.w(TAG, "The file " + fileName + "couldn't be cached. Does this app have permission to ACCESS_EXTERNAL_STORAGE?", e);
		}
	}
	
	public AbstractModel getCachedOrNull(String key) {
		
		String fileName = String.valueOf(key.hashCode());
		try {
			FileInputStream fis = mFileCtrl.getFileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			ModelHolder holder = (ModelHolder) ois.readObject();
			
			if (holder == null || holder.isExpired()) {
				//Expired: erase file
				mFileCtrl.erase(fileName);
				Log.v(TAG, "Expired from disk! Deleting " + fileName);
				return null;
			}
			
			return holder.model;
		
		} catch (Exception e) {
			Log.w(TAG, "Can't access file: " + fileName, e);
			return null;
		}
	}
}
