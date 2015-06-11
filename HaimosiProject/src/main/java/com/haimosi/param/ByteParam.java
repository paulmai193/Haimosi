package com.haimosi.param;

import javax.ws.rs.WebApplicationException;

import com.haimosi.exception.BadParamException;

/**
 * The Class ByteParam.
 * 
 * @author Paul Mai
 */
public class ByteParam extends AbstractParam<Byte> {

	/**
	 * Instantiates a new byte param.
	 *
	 * @param param the param
	 * @throws WebApplicationException the web application exception
	 */
	public ByteParam(String param) throws BadParamException {
		super(param);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nowktv.servlet.param.AbstractParam#parse(java.lang.String)
	 */
	@Override
	protected Byte parse(String param) throws Throwable {
		return Byte.valueOf(param);
	}

}
