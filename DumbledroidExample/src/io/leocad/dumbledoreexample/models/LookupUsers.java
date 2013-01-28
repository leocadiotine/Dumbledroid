package io.leocad.dumbledoreexample.models;

import io.leocad.dumbledroid.data.AbstractModel;
import io.leocad.dumbledroid.data.DataType;

import java.util.List;

public class LookupUsers extends AbstractModel {

	private static final long serialVersionUID = 1L;
	
	public List<User> users;

	public LookupUsers() {
		super("http://api.twitter.com/1/users/lookup.xml?screen_name=twitterapi,twitter&include_entities=true", 60 * 60 * 1000); //1 hour cache
	}
	
	@Override
	protected DataType getDataType() {
		return DataType.XML;
	}
}
