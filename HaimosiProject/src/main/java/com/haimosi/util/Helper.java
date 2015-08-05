package com.haimosi.util;

import java.util.Calendar;
import java.util.Date;

/**
 * The Class Helper.
 */
public final class Helper {

	/**
	 * Gets the remain time in day.
	 *
	 * @return the remain time in day (miliseconds)
	 */
	public static long getRemainTimeInDay() {
		long remain;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		remain = calendar.getTimeInMillis() - System.currentTimeMillis();

		return remain;
	}

}
