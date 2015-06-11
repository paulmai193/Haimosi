package com.haimosi.exception;

import javax.ws.rs.WebApplicationException;

import com.google.gson.JsonObject;
import com.haimosi.define.StatusCode;
import com.haimosi.param.ParamDefine;

/**
 * The Class ProcessException.
 * 
 * @author Paul Mai
 */
public class ProcessException extends WebApplicationException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The error status. */
	private JsonObject        errorStatus;

	/**
	 * Instantiates a new process exception.
	 *
	 * @param statuscode the statuscode
	 */
	public ProcessException(int statuscode) {
		this.errorStatus = new JsonObject();
		this.errorStatus.add(ParamDefine.RESULT, StatusCode.printStatus(statuscode));
	}

	public ProcessException(JsonObject status) {
		this.errorStatus = status;
	}

	public ProcessException(Throwable t) {
		super(t);
		this.errorStatus = new JsonObject();
		this.errorStatus.add(ParamDefine.RESULT, StatusCode.INTERNAL_ERROR.printStatus(t.toString()));
	}

	/**
	 * Gets the error status.
	 *
	 * @return the error status
	 */
	public JsonObject getErrorStatus() {
		return this.errorStatus;
	}

}
