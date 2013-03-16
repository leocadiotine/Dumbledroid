package io.leocad.dumbledroidplugin.exceptions;

public class InvalidUrlException extends Exception {

	private static final long serialVersionUID = -309967699128351284L;

	@Override
	public String getMessage() {
		
		return "The provided URL does not point to a valid address. Please double check it. If it seems to be OK, check your internet connection.";
	}
}
