package com.haimosi.param;

import logia.utility.string.StringUtils;

import com.haimosi.exception.BadParamException;

/**
 * The Class KeywordsParam.
 * 
 * @author Paul Mai
 */
public class KeywordsParam extends AbstractParam<String> {

	/**
	 * Instantiates a new keywords param.
	 *
	 * @param param the param
	 * @throws BadParamException the bad param exception
	 */
	public KeywordsParam(String param) throws BadParamException {
		super(param);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nowktv.servlet.param.AbstractParam#parse(java.lang.String)
	 */
	@Override
	protected String parse(String param) throws Throwable {
		String keywords = StringUtils.removeAccentLowerCase(param).trim();
		return keywords;
	}

}
