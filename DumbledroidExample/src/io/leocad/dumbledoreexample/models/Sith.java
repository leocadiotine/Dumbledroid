package io.leocad.dumbledoreexample.models;

import io.leocad.dumbledroid.data.AbstractModel;
import io.leocad.dumbledroid.data.DataType;

import java.util.List;

public class Sith extends AbstractModel {

	private static final long serialVersionUID = 1L;
	
	public String side;
	public List<String> names;
	public Suit suit;
	public int kills;

	public Sith() {
		super("http://dl.dropbox.com/u/5135185/presentation/sith.xml", 60 * 60 * 1000); //1 hour cache
	}
	
	@Override
	protected DataType getDataType() {
		return DataType.XML;
	}
}
