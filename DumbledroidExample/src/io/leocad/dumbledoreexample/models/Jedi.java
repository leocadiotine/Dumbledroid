package io.leocad.dumbledoreexample.models;

import io.leocad.dumbledroid.data.AbstractModel;
import io.leocad.dumbledroid.data.DataType;

public class Jedi extends AbstractModel {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String surname;
	private String ability;
	private String master;
	private String father;

	public Jedi() {
		super("http://dl.dropbox.com/u/5135185/presentation/jedi.json", 15 * 60 * 1000); //15 min cache
	}
	
	@Override
	protected DataType getDataType() {
		return DataType.JSON;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getAbility() {
		return ability;
	}

	public void setAbility(String ability) {
		this.ability = ability;
	}

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public String getFather() {
		return father;
	}

	public void setFather(String father) {
		this.father = father;
	}
}
