package com.haimosi.param;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.WebApplicationException;

import com.haimosi.define.Constant;
import com.haimosi.exception.BadParamException;

/**
 * The Class DayParam.
 * 
 * @author Paul Mai
 */
public class DayParam extends AbstractParam<Date> {

	/**
	 * Instantiates a new day param.
	 *
	 * @param param the param
	 * @throws WebApplicationException the web application exception
	 */
	public DayParam(String param) throws BadParamException {
		super(param);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nowktv.servlet.param.AbstractParam#parse(java.lang.String)
	 */
	@Override
	protected Date parse(String param) throws Throwable {
		SimpleDateFormat sdf = new SimpleDateFormat(Constant.PATTERN_YYYY_MM_DD_HH_MM_SS);
		try {
			return sdf.parse(param);
		}
		catch (Exception e) {
			sdf = new SimpleDateFormat(Constant.PATTERN_YYYY_MM_DD_HH_MM);
			try {
				return sdf.parse(param);
			}
			catch (Exception e1) {
				sdf = new SimpleDateFormat(Constant.PATTERN_YYYY_MM_DD);
				try {
					return sdf.parse(param);
				}
				catch (Exception e2) {
					throw new Throwable("Not day format");
				}
			}
		}

	}

}
