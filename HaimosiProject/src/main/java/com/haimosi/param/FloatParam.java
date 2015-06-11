package com.haimosi.param;

import javax.ws.rs.WebApplicationException;

import com.haimosi.exception.BadParamException;

/**
 * The Class FloatParam.
 * 
 * @author Paul Mai
 */
public class FloatParam extends AbstractParam<Float> {

	/**
	 * Instantiates a new float param.
	 *
	 * @param param the param
	 * @throws WebApplicationException the web application exception
	 */
	public FloatParam(String param) throws BadParamException {
		super(param);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nowktv.servlet.param.AbstractParam#parse(java.lang.String)
	 */
	@Override
	protected Float parse(String param) throws Throwable {
		return Float.valueOf(param);
	}

}
