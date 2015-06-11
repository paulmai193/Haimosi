package com.haimosi.param;

import javax.ws.rs.WebApplicationException;

import com.haimosi.exception.BadParamException;

/**
 * The Class IntegerParam.
 * 
 * @author Paul Mai
 */
public class IntegerParam extends AbstractParam<Integer> {

	/**
	 * Instantiates a new integer param.
	 *
	 * @param param the param
	 * @throws WebApplicationException the web application exception
	 */
	public IntegerParam(String param) throws BadParamException {
		super(param);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nowktv.servlet.param.AbstractParam#parse(java.lang.String)
	 */
	@Override
	protected Integer parse(String param) throws Throwable {
		return Integer.valueOf(param);
	}

}
