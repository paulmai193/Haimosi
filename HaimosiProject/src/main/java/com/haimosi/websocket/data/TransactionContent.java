package com.haimosi.websocket.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import logia.utility.json.JsonTool;
import logia.utility.json.annotaion.JsonKey;

import com.google.gson.JsonObject;
import com.haimosi.define.Constant;
import com.haimosi.param.ParamDefine;

@logia.utility.json.annotaion.JsonObject
public class TransactionContent {

	/** The id transaction. */
	@JsonKey(key = ParamDefine.TRANSACTION_ID)
	private int   idTransaction;

	/** The quantity. */
	@JsonKey(key = ParamDefine.TRANSACTION_QUANTITY)
	private float quantity;

	/** The amount. */
	@JsonKey(key = ParamDefine.TRANSACTION_AMOUNT)
	private float amount;

	/** The time. */
	@JsonKey(key = ParamDefine.TRANSACTION_TIME)
	private Date  time;

	/**
	 * Instantiates a new obj content.
	 */
	public TransactionContent() {

	}

	/**
	 * Instantiates a new obj content.
	 *
	 * @param id the id
	 * @param quantity the quantity
	 * @param amount the amount
	 * @param time the time
	 */
	public TransactionContent(int id, float quantity, float amount, Date time) {
		this.amount = amount;
		this.idTransaction = id;
		this.quantity = quantity;
		this.time = time;
	}

	/**
	 * Instantiates a new obj content.
	 *
	 * @param json the json
	 * @throws ParseException the parse exception
	 */
	public TransactionContent(JsonObject json) throws ParseException {
		this.fromJson(json);
	}

	/**
	 * From json.
	 *
	 * @param json the json
	 * @throws ParseException the parse exception
	 */
	public void fromJson(JsonObject json) throws ParseException {
		this.amount = json.get(ParamDefine.TRANSACTION_AMOUNT).getAsFloat();
		this.idTransaction = json.get(ParamDefine.TRANSACTION_ID).getAsInt();
		this.quantity = json.get(ParamDefine.TRANSACTION_QUANTITY).getAsFloat();
		SimpleDateFormat sdf = new SimpleDateFormat(Constant.PATTERN_YYYY_MM_DD_HH_MM_SS);
		this.time = sdf.parse(json.get(ParamDefine.TRANSACTION_TIME).getAsString());
	}

	/**
	 * Gets the amount.
	 *
	 * @return the amount
	 */
	public float getAmount() {
		return this.amount;
	}

	/**
	 * Gets the id transaction.
	 *
	 * @return the idTransaction
	 */
	public int getIdTransaction() {
		return this.idTransaction;
	}

	/**
	 * Gets the quantity.
	 *
	 * @return the quantity
	 */
	public float getQuantity() {
		return this.quantity;
	}

	/**
	 * Gets the time.
	 *
	 * @return the time
	 */
	public Date getTime() {
		return this.time;
	}

	/**
	 * Sets the amount.
	 *
	 * @param amount the amount to set
	 */
	public void setAmount(float amount) {
		this.amount = amount;
	}

	/**
	 * Sets the id transaction.
	 *
	 * @param idTransaction the idTransaction to set
	 */
	public void setIdTransaction(int idTransaction) {
		this.idTransaction = idTransaction;
	}

	/**
	 * Sets the quantity.
	 *
	 * @param quantity the quantity to set
	 */
	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}

	/**
	 * Sets the time.
	 *
	 * @param time the time to set
	 */
	public void setTime(Date time) {
		this.time = time;
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
