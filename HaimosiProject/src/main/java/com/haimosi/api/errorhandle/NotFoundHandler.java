package com.haimosi.api.errorhandle;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.gson.JsonObject;
import com.haimosi.define.StatusCode;
import com.haimosi.param.ParamDefine;
import com.sun.jersey.api.NotFoundException;

/**
 * The Class NotFoundHandler.
 * 
 * @author Paul Mai
 */
@Provider
public class NotFoundHandler implements ExceptionMapper<NotFoundException> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
	 */
	@Override
	public Response toResponse(NotFoundException e) {
		JsonObject status = new JsonObject();
		status.add(ParamDefine.RESULT, StatusCode.BAD_REQUEST.printStatus());
		return Response.status(Status.OK).entity(status.toString()).build();
	}
}
