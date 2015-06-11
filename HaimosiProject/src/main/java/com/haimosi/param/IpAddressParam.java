package com.haimosi.param;

import java.util.regex.Pattern;

import com.haimosi.exception.BadParamException;

/**
 * The Class IpAddressParam.
 * 
 * @author Paul Mai
 */
public class IpAddressParam extends AbstractParam<String> {

	/** The Constant EMAIL_PATTERN. */
	private static final String IP_ADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	/**
	 * Instantiates a new ip address param.
	 *
	 * @param param the param
	 * @throws BadParamException the bad param exception
	 */
	public IpAddressParam(String param) throws BadParamException {
		super(param);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nowktv.servlet.param.AbstractParam#parse(java.lang.String)
	 */
	@Override
	protected String parse(String param) throws Throwable {
		if (Pattern.compile(IpAddressParam.IP_ADDRESS_PATTERN).matcher(param).matches()) {
			return param;
		}
		else {
			throw new Throwable("Not IP address format");
		}
	}

}
