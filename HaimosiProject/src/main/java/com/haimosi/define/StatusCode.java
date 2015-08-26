package com.haimosi.define;

import com.google.gson.JsonObject;
import com.haimosi.param.ParamDefine;

/**
 * The Enum StatusCode.
 * 
 * @author Paul Mai
 */
public enum StatusCode {

	/** The bad param. */
	BAD_PARAM(303, "Bad parameter"),

	/** The bad request. */
	BAD_REQUEST(302, "Bad request"),

	/** The bad verify code. */
	BAD_VERIFY_CODE(304, "Bad verify code"),

	/** The deprecated. */
	DEPRECATED(401, "Deprecated, no longer support"),

	/** The exist account. */
	EXIST_ACCOUNT(301, "Exist account"),

	/** The internal error. */
	INTERNAL_ERROR(400, "Internal error"),

	/** The locked. */
	LOCKED(101, "Locked"),

	/** The must login. */
	MUST_LOGIN(100, "Must login"),

	/** The must verify. */
	MUST_VERIFY(103, "Must verify account"),

	/** The no content. */
	NO_CONTENT(201, "No content"),

	/** The success. */
	SUCCESS(200, "Success"),

	/** The time out. */
	TIME_OUT(102, "Session timeout"),

	/** The wrong account. */
	WRONG_ACCOUNT(300, "Wrong account");

	/** The code. */
	int    code;

	/** The content. */
	String content;

	/**
	 * Instantiates a new status code.
	 *
	 * @param code the code
	 * @param content the content
	 */
	StatusCode(int code, String content) {
		this.code = code;
		this.content = content;
	}

	/**
	 * Prints the status.
	 *
	 * @param code the code
	 * @return the json object
	 */
	public static JsonObject printStatus(int code) {
		for (StatusCode statusCode : StatusCode.values()) {
			if (statusCode.getCode() == code) {
				return statusCode.printStatus();
			}
		}
		return INTERNAL_ERROR.printStatus();
	}

	/**
	 * Prints the status.
	 *
	 * @param code the code
	 * @param message the message
	 * @return the json object
	 */
	public static JsonObject printStatus(int code, String message) {
		for (StatusCode statusCode : StatusCode.values()) {
			if (statusCode.getCode() == code) {
				if (message != null) {
					return statusCode.printStatus(message);
				}
				else {
					return statusCode.printStatus();
				}
			}
		}
		return INTERNAL_ERROR.printStatus();
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public int getCode() {
		return this.code;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * Prints the status.
	 *
	 * @return the json object
	 */
	public JsonObject printStatus() {
		JsonObject status = new JsonObject();
		status.addProperty(ParamDefine.STATUS_CODE, this.code);
		status.addProperty(ParamDefine.STATUS_CONTENT, this.content);

		return status;
	}

	/**
	 * Prints the status.
	 *
	 * @param message the message
	 * @return the json object
	 */
	public JsonObject printStatus(String message) {
		JsonObject status = new JsonObject();
		if (message == null) {
			status = this.printStatus();
		}
		else {
			status.addProperty(ParamDefine.STATUS_CODE, this.code);
			status.addProperty(ParamDefine.STATUS_CONTENT, message);
		}
		return status;
	}

}
