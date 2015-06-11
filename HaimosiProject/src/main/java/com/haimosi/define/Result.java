package com.haimosi.define;

import logia.utility.json.annotaion.JsonKey;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.haimosi.exception.BadStatusException;
import com.haimosi.param.ParamDefine;

/**
 * The Class Result.
 * 
 * @author Paul Mai
 */
@logia.utility.json.annotaion.JsonObject
public class Result {

	/** The code. */
	@JsonKey(key = ParamDefine.STATUS_CODE)
	int    code;

	/** The name. */
	@JsonKey(key = ParamDefine.STATUS_CONTENT)
	String name;

	/**
	 * Instantiates a new result.
	 */
	public Result() {

	}

	/**
	 * Instantiates a new status code.
	 *
	 * @param status the status in json type
	 * @throws BadStatusException the bad status exception
	 */
	public Result(JsonObject status) throws BadStatusException {
		if (status.has(ParamDefine.RESULT)) {
			status = (JsonObject) status.get(ParamDefine.RESULT);
		}
		if (status.has(ParamDefine.STATUS_CODE) && status.has(ParamDefine.STATUS_CONTENT)) {
			this.code = status.get(ParamDefine.STATUS_CODE).getAsInt();
			this.name = status.get(ParamDefine.STATUS_CONTENT).getAsString();
		}
		else {
			throw new BadStatusException();
		}
	}

	/**
	 * Instantiates a new status code.
	 *
	 * @param string the status string
	 * @throws BadStatusException the bad status exception
	 */
	public Result(String string) throws BadStatusException {
		JsonParser parser = new JsonParser();
		JsonObject status = (JsonObject) parser.parse(string);
		if (status.has(ParamDefine.RESULT)) {
			status = (JsonObject) status.get(ParamDefine.RESULT);
		}
		if (status.has(ParamDefine.STATUS_CODE) && status.has(ParamDefine.STATUS_CONTENT)) {
			this.code = status.get(ParamDefine.STATUS_CODE).getAsInt();
			this.name = status.get(ParamDefine.STATUS_CONTENT).getAsString();
		}
		else {
			throw new BadStatusException();
		}
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return this.code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
