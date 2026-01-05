package com.lucast.vetcare.fiscal.exception;

public class FiscalException extends Exception {

	private static final long serialVersionUID = -1L;
	
	private final String path;
	
	public FiscalException(String message) {
		super(message);
		this.path = "undefined";
	}
	
	public FiscalException(String path, String message) {
		super(message);
		this.path = path;
	}

	public FiscalException(String message, Throwable cause) {
		super(message, cause);
		this.path = "undefined";
	}

	public String getPath() {
		return path;
	}
}
