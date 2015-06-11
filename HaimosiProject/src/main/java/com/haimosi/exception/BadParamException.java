package com.haimosi.exception;

import javax.ws.rs.WebApplicationException;

/**
 * The Class BadParamException.
 * 
 * @author Paul Mai
 */
public class BadParamException extends WebApplicationException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new bad param exception.
	 */
	public BadParamException() {
		super(new Throwable("Invalid parameter"));
	}

	/**
	 * Instantiates a new bad param exception.
	 * 
	 * @param t the Throwable
	 */
	public BadParamException(Throwable t) {
		super(t);
	}
}
