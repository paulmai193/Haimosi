package com.haimosi.param;

import javax.ws.rs.WebApplicationException;

import com.haimosi.exception.BadParamException;

/**
 * The Class AbstractParam.
 *
 * @param <V> the value type
 * @author Paul Mai
 */
public abstract class AbstractParam<V> {

	/** The original param. */
	private final String originalParam;

	/** The value. */
	private final V      value;

	/**
	 * Instantiates a new abstract param.
	 *
	 * @param param the param
	 * @throws WebApplicationException the web application exception
	 */
	public AbstractParam(String param) throws BadParamException {
		this.originalParam = param;
		try {
			this.value = this.parse(param);
		}
		catch (Throwable t) {
			throw new BadParamException(t);
		}
	}

	/**
	 * Gets the original param.
	 *
	 * @return the original param
	 */
	public String getOriginalParam() {
		return this.originalParam;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public V getValue() {
		return this.value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.value.toString();
	}

	/**
	 * Parses the parameter to value.
	 *
	 * @param param the param
	 * @return the value
	 * @throws Throwable the throwable
	 */
	protected abstract V parse(String param) throws Throwable;

}
