package com.haimosi.param;

import javax.ws.rs.WebApplicationException;

import com.haimosi.exception.BadParamException;

/**
 * The Class LongParam.
 * 
 * @author Paul Mai
 */
public class LongParam extends AbstractParam<Long> {

	/**
	 * Instantiates a new long param.
	 *
	 * @param param the param
	 * @throws WebApplicationException the web application exception
	 */
	public LongParam(String param) throws BadParamException {
		super(param);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nowktv.servlet.param.AbstractParam#parse(java.lang.String)
	 */
	@Override
	protected Long parse(String param) throws Throwable {
		return Long.valueOf(param);
	}

}
