package io.leocad.dumbledoreexample.models;

import io.leocad.dumbledroid.data.AbstractModel;
import io.leocad.dumbledroid.data.DataType;

import java.util.List;

public class Sith extends AbstractModel {

	private static final long serialVersionUID = 1L;
	
	private String side;
	private List<String> names;
	private Suit suit;
	private int kills;
	
	public Sith() {
		super("https://dl.dropbox.com/u/5135185/dumbledroid/sith.xml", 15 * 60 * 1000); //15 min cache
	}
	
	@Override
	protected DataType getDataType() {
		return DataType.XML;
	}

	public String getSide() {
		return side;
	}

	public void setSide(String side) {
		this.side = side;
	}

	public String getNames() {
		
		StringBuffer sb = new StringBuffer();
		
		for (String name : names) {
			sb.append(name)
			.append(", ");
		}
		
		//Remove the last comma and space (", ")
		int sbLength = sb.length();
		sb.delete(sbLength-2, sbLength);
		
		return sb.toString();
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	public Suit getSuit() {
		return suit;
	}

	public void setSuit(Suit suit) {
		this.suit = suit;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
