package com.haimosi.param;

import java.util.regex.Pattern;

import com.haimosi.exception.BadParamException;

/**
 * The Class KeywordsParam.
 * 
 * @author Paul Mai
 */
public class ContactParam extends AbstractParam<String> {

	/** The Constant CONTACT_EMAIL. */
	public static final byte    CONTACT_EMAIL = 1;

	/** The Constant CONTACT_PHONE. */
	public static final byte    CONTACT_PHONE = 2;

	/** The Constant EMAIL_PATTERN. */
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	/** The Constant PHONE_PATTERN. */
	private static final String PHONE_PATTERN = "\\d+";

	/** The contact type. */
	private byte                contactType;

	/**
	 * Instantiates a new keywords param.
	 *
	 * @param param the param
	 * @throws BadParamException the bad param exception
	 */
	public ContactParam(String param) throws BadParamException {
		super(param);
	}

	/**
	 * Gets the contact type.
	 *
	 * @return the contact type
	 */
	public byte getContactType() {
		return this.contactType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nowktv.servlet.param.AbstractParam#toString()
	 */
	@Override
	public String toString() {
		return "{\"Contact\":" + this.getValue() + ", \"Type\":" + this.contactType + "}";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nowktv.servlet.param.AbstractParam#parse(java.lang.String)
	 */
	@Override
	protected String parse(String param) throws Throwable {
		if (Pattern.compile(ContactParam.EMAIL_PATTERN).matcher(param).matches()) {
			this.contactType = ContactParam.CONTACT_EMAIL;
			return param;
		}
		else if (Pattern.compile(ContactParam.PHONE_PATTERN).matcher(param).matches()) {
			this.contactType = ContactParam.CONTACT_PHONE;
			return param;
		}
		else {
			throw new Throwable("Not email format");
		}
	}

}
