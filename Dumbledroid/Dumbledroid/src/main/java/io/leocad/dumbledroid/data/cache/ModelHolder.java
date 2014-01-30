package io.leocad.dumbledroid.data.cache;

import io.leocad.dumbledroid.data.AbstractModel;

import java.io.Serializable;

public class ModelHolder implements Serializable {

	private static final long serialVersionUID = -884696290834846261L;
	
	public AbstractModel model;
	public long timestamp;

	public ModelHolder(AbstractModel model, long timestamp) {
		this.model = model;
		this.timestamp = timestamp;
	}

	public boolean isExpired() {

		long elapsedTime = System.currentTimeMillis() - timestamp;

		return elapsedTime > model.cacheDuration;
	}
}
