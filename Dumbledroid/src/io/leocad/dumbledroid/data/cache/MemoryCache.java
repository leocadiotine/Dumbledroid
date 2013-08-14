package io.leocad.dumbledroid.data.cache;

import io.leocad.dumbledroid.data.AbstractModel;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class MemoryCache {

	private static MemoryCache sInstance;

	public static MemoryCache getInstance() {
		if (sInstance == null) {
			synchronized (MemoryCache.class) {
				if (sInstance == null) {
					sInstance = new MemoryCache();
				}
			}
		}
		return sInstance;
	}

	private Map<String, SoftReference<ModelHolder>> mMap;

	private MemoryCache() {
		mMap = new HashMap<String, SoftReference<ModelHolder>>();
	}

	public void cache(String key, AbstractModel model) {

		ModelHolder holder = new ModelHolder(model, System.currentTimeMillis());

		SoftReference<ModelHolder> ref = new SoftReference<ModelHolder>(holder);
		mMap.put(key, ref);
	}

	public AbstractModel getCachedOrNull(String key) {

		SoftReference<ModelHolder> softReference = mMap.get(key);

		if (softReference == null) {
			return null;
		}

		ModelHolder modelHolder = softReference.get();

		if (modelHolder == null || modelHolder.isExpired()) {
			//Clear from cache
			softReference.clear();
			mMap.remove(key);

			Log.v("MemoryCache", "Expired from memory! " + key);

			return null;
		}

		return modelHolder.model;
	}
}