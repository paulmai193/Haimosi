package com.haimosi.api.util;

import com.haimosi.exception.BadParamException;

/**
 * The Interface CheckParameterInterface.
 * 
 * @author Paul Mai
 */
public interface CheckParameterInterface {

	/**
	 * Do check.
	 *
	 * @param parameters the parameters
	 * @return true, if successful
	 * @throws BadParamException the bad param exception
	 */
	public boolean doCheck(Object... parameters) throws BadParamException;
}
