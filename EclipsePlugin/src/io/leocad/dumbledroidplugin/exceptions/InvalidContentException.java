package io.leocad.dumbledroidplugin.exceptions;

public class InvalidContentException extends Exception {

	private static final long serialVersionUID = 8125988022163808304L;
	
	private String mContentType;
	
	public InvalidContentException(String contentType) {
		mContentType = contentType;
	}

	@Override
	public String getMessage() {
		
		return String.format("The provided '%s' is not invalid or corrupted. Please validate it here: %s", mContentType, getValidationUrl());
	}
	
	private String getValidationUrl() {
		return mContentType.toLowerCase().equals("json")?
			"http://jsonlint.com/":
			"http://www.w3schools.com/xml/xml_validator.asp";
	}
}
