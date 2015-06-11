package com.haimosi.api.errorhandle;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.gson.JsonObject;
import com.haimosi.define.StatusCode;
import com.haimosi.exception.BadParamException;
import com.haimosi.param.ParamDefine;

/**
 * The Class BadParamHandler.
 * 
 * @author Paul Mai
 */
@Provider
public class BadParamHandler implements ExceptionMapper<BadParamException> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
	 */
	@Override
	public Response toResponse(BadParamException e) {
		JsonObject status = new JsonObject();
		status.add(ParamDefine.RESULT, StatusCode.BAD_PARAM.printStatus(e.getMessage()));
		return Response.status(Status.OK).entity(status.toString()).build();
	}
}
