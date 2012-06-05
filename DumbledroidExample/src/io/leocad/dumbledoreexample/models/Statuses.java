package io.leocad.dumbledoreexample.models;

import io.leocad.dumbledroid.data.AbstractModel;
import io.leocad.dumbledroid.data.DataType;

import java.util.List;

public class Statuses extends AbstractModel {

	private static final long serialVersionUID = 1L;
	
	public List<Status> statuses;

	public Statuses() {
		super("https://api.twitter.com/1/statuses/public_timeline.xml?count=3&include_entities=true", 60 * 60 * 1000); //1 hour cache
	}
	
	@Override
	protected DataType getDataType() {
		return DataType.XML;
	}
}
