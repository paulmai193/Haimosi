package com.haimosi.websocket.data;

import java.text.ParseException;

import logia.utility.json.JsonTool;
import logia.utility.json.annotaion.JsonKey;

import com.google.gson.JsonObject;
import com.haimosi.param.ParamDefine;

@logia.utility.json.annotaion.JsonObject
public class TransConfirmContent {

	/** The id transaction. */
	@JsonKey(key = ParamDefine.TRANSACTION_ID)
	private int    idTransaction;

	/** The photo. */
	@JsonKey(key = ParamDefine.TRANSACTION_PHOTO)
	private String photo;

	/**
	 * Instantiates a new obj content.
	 */
	public TransConfirmContent() {

	}

	/**
	 * Instantiates a new obj content.
	 *
	 * @param id the id
	 * @param url the url
	 */
	public TransConfirmContent(int id, String url) {
		this.idTransaction = id;
		this.photo = url;
	}

	/**
	 * Instantiates a new obj content.
	 *
	 * @param json the json
	 * @throws ParseException the parse exception
	 */
	public TransConfirmContent(JsonObject json) throws ParseException {
		this.fromJson(json);
	}

	/**
	 * From json.
	 *
	 * @param json the json
	 * @throws ParseException the parse exception
	 */
	public void fromJson(JsonObject json) throws ParseException {
		this.idTransaction = json.get(ParamDefine.TRANSACTION_ID).getAsInt();
		this.photo = json.get(ParamDefine.TRANSACTION_PHOTO).getAsString();
	}

	/**
	 * @return the idTransaction
	 */
	public int getIdTransaction() {
		return this.idTransaction;
	}

	/**
	 * @return the photo
	 */
	public String getPhoto() {
		return this.photo;
	}

	/**
	 * @param idTransaction the idTransaction to set
	 */
	public void setIdTransaction(int idTransaction) {
		this.idTransaction = idTransaction;
	}

	/**
	 * @param photo the photo to set
	 */
	public void setPhoto(String photo) {
		this.photo = photo;
	}

	/**
	 * To json.
	 *
	 * @return the json object
	 */
	public JsonObject toJson() {
		return JsonTool.toJsonObject(this);
	}
}
