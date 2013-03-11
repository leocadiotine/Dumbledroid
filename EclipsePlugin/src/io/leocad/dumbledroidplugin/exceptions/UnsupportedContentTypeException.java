package io.leocad.dumbledroidplugin.exceptions;

public class UnsupportedContentTypeException extends Exception {

	private static final long serialVersionUID = 7469994192742254679L;
	
	private String mContentType;
	
	public UnsupportedContentTypeException(String contentType) {
		mContentType = contentType;
	}

	@Override
	public String getMessage() {
		
		return String.format("'%s' is not a valid content type. Your URL should point to a valid JSON or XML content.", mContentType);
	}
}
