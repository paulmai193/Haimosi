package com.haimosi.api.errorhandle;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.gson.JsonObject;
import com.haimosi.exception.ProcessException;

/**
 * The Class NotFoundHandler.
 * 
 * @author Paul Mai
 */
@Provider
public class ProcessErrorHandler implements ExceptionMapper<ProcessException> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
	 */
	@Override
	public Response toResponse(ProcessException e) {
		JsonObject status = e.getErrorStatus();
		return Response.status(Status.OK).entity(status.toString()).build();
	}
}
