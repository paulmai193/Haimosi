package com.haimosi.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

	/**
	 * Merge multiple JsonObject into one.
	 *
	 * @param json the JsonObject
	 * @return the united JsonObject
	 */
	public static JsonObject mergeJson(JsonObject... json) {
		JsonObject mergedJson = new JsonObject();
		for (JsonObject element : json) {
			for (Map.Entry<String, JsonElement> entry : element.entrySet()) {
				mergedJson.add(entry.getKey(), entry.getValue());
			}
		}
		return mergedJson;
	}

	/**
	 * Round double.
	 *
	 * @param number the original number
	 * @param numDig the number degree
	 * @return the rounded double
	 * @throws NumberFormatException the number format exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws NullPointerException the null pointer exception
	 */
	public static double roundDouble(double number, int numDig) throws NumberFormatException, IllegalArgumentException, NullPointerException {
		String pattern = "##0.";
		for (int i = 0; i < numDig; i++) {
			pattern = pattern.concat("0");
		}
		DecimalFormat df = new DecimalFormat("".concat(pattern).concat(";-").concat(pattern));
		df.setRoundingMode(RoundingMode.HALF_UP);
		double roundNumber = Double.parseDouble(df.format(number).replace(",", "."));
		return roundNumber;
	}

	/**
	 * Sort list by index.
	 *
	 * @param <T> the generic type
	 * @param list the list
	 * @param index the index
	 * @param size the size
	 * @return the list
	 * @throws IndexOutOfBoundsException the index out of bounds exception
	 */
	public static <T> List<T> sortListByIndex(Collection<T> list, int index, int size) throws IndexOutOfBoundsException {
		List<T> temp = new ArrayList<T>(list);
		if (list.size() > 0 && index > 0) {
			int start = (index - 1) * size;
			int records = 0;
			int n = list.size() / size;
			if (index <= n) {
				records = size;
				int end = start + records;
				temp = temp.subList(start, end);
			}
			else if (index - n == 1) {
				records = list.size() % size;
				int end = start + records;
				temp = temp.subList(start, end);
			}
		}
		return temp;
	}

}
