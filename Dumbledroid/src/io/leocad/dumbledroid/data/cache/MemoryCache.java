package io.leocad.dumbledroid.data.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class MemoryCache {

	private static final MemoryCache INSTANCE = new MemoryCache();
	public static MemoryCache getInstance() {
		return INSTANCE;
	}

	private Map<String, SoftReference<ModelHolder>> mMap;

	private MemoryCache() {
		mMap = new HashMap<String, SoftReference<ModelHolder>>();
	}

	public void cache(String key, ModelHolder holder) {
		final SoftReference<ModelHolder> ref = new SoftReference<ModelHolder>(holder);
		mMap.put(key, ref);
	}

	public ModelHolder getCached(String key) {
		final SoftReference<ModelHolder> reference = mMap.get(key);
		if (reference == null) {
			return null;
		}
		final ModelHolder holder = reference.get();
		if(holder == null) {
			reference.clear();
			mMap.remove(key);
		}
		return holder;
	}
}