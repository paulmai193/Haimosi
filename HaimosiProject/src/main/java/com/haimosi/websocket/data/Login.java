package com.haimosi.websocket.data;

import java.text.ParseException;

import logia.utility.json.annotaion.JsonKey;

import com.google.gson.JsonObject;
import com.haimosi.param.ParamDefine;

/**
 * The Class Login.
 * 
 * @author Paul Mai
 */
@logia.utility.json.annotaion.JsonObject
public class Login {

	/** The email. */
	@JsonKey(key = ParamDefine.EMAIL)
	private String email;

	/** The password. */
	@JsonKey(key = ParamDefine.PASSWORD)
	private String password;

	/**
	 * Instantiates a new login.
	 */
	public Login() {

	}

	/**
	 * Instantiates a new login.
	 *
	 * @param json the json
	 * @throws ParseException the parse exception
	 */
	public Login(JsonObject json) throws ParseException {
		this.fromJson(json);
	}

	/**
	 * Instantiates a new login.
	 *
	 * @param email the email
	 * @param password the password
	 */
	public Login(String email, String password) {
		this.email = email;
		this.password = password;
	}

	/**
	 * From json.
	 *
	 * @param json the json
	 * @throws ParseException the parse exception
	 */
	public void fromJson(JsonObject json) throws ParseException {
		this.email = json.get(ParamDefine.EMAIL).getAsString();
		this.password = json.get(ParamDefine.PASSWORD).getAsString();
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Sets the email.
	 *
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Sets the password.
	 *
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
