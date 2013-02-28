package io.leocad.dumbledoreexample.models;

import java.io.Serializable;

public class Suit implements Serializable {

	private static final long serialVersionUID = 3422767378715300945L;
	
	private String color;
	private boolean cloak;
	
	public String getColor() {
		return color;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
	
	public boolean hasCloak() {
		return cloak;
	}
	
	public void setCloak(boolean cloak) {
		this.cloak = cloak;
	}
}
