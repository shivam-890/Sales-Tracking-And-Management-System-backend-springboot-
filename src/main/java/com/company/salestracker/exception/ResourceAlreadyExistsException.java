package com.company.salestracker.exception;

public class ResourceAlreadyExistsException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResourceAlreadyExistsException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ResourceAlreadyExistsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public ResourceAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ResourceAlreadyExistsException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ResourceAlreadyExistsException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
