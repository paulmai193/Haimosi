package com.haimosi.param;

import javax.ws.rs.WebApplicationException;

import com.haimosi.exception.BadParamException;

/**
 * The Class Boolean.
 * 
 * @author Paul Mai
 */
public class BooleanParam extends AbstractParam<Boolean> {

	/**
	 * Instantiates a new boolean param.
	 *
	 * @param param the param
	 * @throws WebApplicationException the web application exception
	 */
	public BooleanParam(String param) throws BadParamException {
		super(param);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nowktv.servlet.param.AbstractParam#parse(java.lang.String)
	 */
	@Override
	protected Boolean parse(String param) throws Throwable {
		if ("1".equalsIgnoreCase(param) || "yes".equalsIgnoreCase(param) || "true".equalsIgnoreCase(param) || "on".equalsIgnoreCase(param)) {
			return true;
		}
		else {
			return false;
		}
	}

}
