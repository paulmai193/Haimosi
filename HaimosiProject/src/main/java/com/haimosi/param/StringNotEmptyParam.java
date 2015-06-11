package com.haimosi.param;

import com.haimosi.exception.BadParamException;

/**
 * The Class KeywordsParam.
 * 
 * @author Paul Mai
 */
public class StringNotEmptyParam extends AbstractParam<String> {

	/**
	 * Instantiates a new keywords param.
	 *
	 * @param param the param
	 * @throws BadParamException the bad param exception
	 */
	public StringNotEmptyParam(String param) throws BadParamException {
		super(param);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nowktv.servlet.param.AbstractParam#parse(java.lang.String)
	 */
	@Override
	protected String parse(String param) throws Throwable {
		if (param.trim().isEmpty()) {
			throw new BadParamException();
		}
		else {
			return param;
		}
	}

}
