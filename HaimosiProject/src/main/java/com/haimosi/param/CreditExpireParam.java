package com.haimosi.param;

import java.util.regex.Pattern;

import com.haimosi.exception.BadParamException;

/**
 * The Class IpAddressParam.
 * 
 * @author Paul Mai
 */
public class CreditExpireParam extends AbstractParam<String> {

	/** The Constant EMAIL_PATTERN. */
	private static final String PATTERN = "(0?[1-9]|1[012])/((19|20)\\d\\d)";

	/**
	 * Instantiates a new ip address param.
	 *
	 * @param param the param
	 * @throws BadParamException the bad param exception
	 */
	public CreditExpireParam(String param) throws BadParamException {
		super(param);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nowktv.servlet.param.AbstractParam#parse(java.lang.String)
	 */
	@Override
	protected String parse(String param) throws Throwable {
		if (Pattern.compile(CreditExpireParam.PATTERN).matcher(param).matches()) {
			return param;
		}
		else {
			throw new Throwable("Not expire time format");
		}
	}

}
