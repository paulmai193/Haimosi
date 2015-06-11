package com.haimosi.param;

import javax.ws.rs.WebApplicationException;

import com.haimosi.exception.BadParamException;

/**
 * The Class IndexParam.
 * 
 * @author Paul Mai
 */
public class IndexParam extends AbstractParam<Integer> {

	/**
	 * Instantiates a new index param.
	 *
	 * @param param the param
	 * @throws WebApplicationException the web application exception
	 */
	public IndexParam(String param) throws BadParamException {
		super(param);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nowktv.servlet.param.AbstractParam#parse(java.lang.String)
	 */
	@Override
	protected Integer parse(String param) throws Throwable {
		if (param == null || param.trim().equals("") || param.trim().equals("0")) {
			param = "1";
		}

		return Math.abs(Integer.valueOf(param));
	}

}
