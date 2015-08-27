package com.haimosi.api.util;

import com.haimosi.exception.BadParamException;

/**
 * The Class ImplCheckParameter.
 * 
 * @author Paul Mai
 */
public final class ImplCheckParameter implements CheckParameterInterface {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.haimosi.api.util.CheckParameterInterface#doCheck(java.lang.Object[])
	 */
	@Override
	public boolean doCheck(Object... parameters) throws BadParamException {
		for (Object parameter : parameters) {
			if (parameter == null) {
				throw new BadParamException();
			}
		}
		return true;
	}
}
