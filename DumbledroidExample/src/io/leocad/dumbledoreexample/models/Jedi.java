package io.leocad.dumbledoreexample.models;

import io.leocad.dumbledroid.data.AbstractModel;
import io.leocad.dumbledroid.data.DataType;

public class Jedi extends AbstractModel {

	private static final long serialVersionUID = 1L;
	
	public String name;
	public String surname;
	public String ability;
	public String master;
	public String father;

	public Jedi() {
		super("http://dl.dropbox.com/u/5135185/presentation/jedi.json", 15 * 60 * 1000); //15 min cache
	}
	
	@Override
	protected DataType getDataType() {
		return DataType.JSON;
	}
}
