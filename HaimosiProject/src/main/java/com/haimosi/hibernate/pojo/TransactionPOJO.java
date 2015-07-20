/*
 * 
 */
package com.haimosi.hibernate.pojo;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import logia.utility.json.annotaion.JsonKey;

import org.hibernate.annotations.DynamicUpdate;

import com.google.gson.JsonObject;
import com.haimosi.define.Constant;
import com.haimosi.param.ParamDefine;

/**
 * The Class TransactionPOJO.
 * 
 * @author Paul Mai
 */
@Entity
@Table(name = "transaction", catalog = "paulmai")
@DynamicUpdate(value = true)
@logia.utility.json.annotaion.JsonObject
public class TransactionPOJO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The amount. */
	@Column(name = "amount", nullable = false)
	@JsonKey(key = ParamDefine.TRANSACTION_AMOUNT)
	private float             amount;

	/** The id transaction. */
	@Id
	@GeneratedValue
	@Column(name = "idtransaction", nullable = false)
	@JsonKey(key = ParamDefine.TRANSACTION_ID)
	private Integer           idTransaction;

	/** The item. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "iditem", nullable = false)
	private ItemPOJO          item;

	/** The method. */
	@Column(name = "method", nullable = false)
	@JsonKey(key = ParamDefine.METHOD)
	private byte              method;

	/** The photo. */
	@Column(name = "photo", nullable = true, length = 50)
	private String            photo;

	/** The quantity. */
	@Column(name = "quantity", nullable = false)
	@JsonKey(key = ParamDefine.TRANSACTION_QUANTITY)
	private float             quantity;

	/** The status. */
	@Column(name = "status", nullable = false)
	@JsonKey(key = ParamDefine.TRANSACTION_STATUS)
	private byte              status;

	/** The time. */
	@Column(name = "time", nullable = false)
	private Date              time;

	/** The user. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "iduser", nullable = false)
	private UserPOJO          user;

	/** The is like. */
	@JsonKey(key = ParamDefine.TRANSACTION_STATUS_LIKE)
	boolean                   isLike;

	/**
	 * Instantiates a new transaction pojo.
	 */
	public TransactionPOJO() {

	}

	/**
	 * Instantiates a new transaction pojo.
	 *
	 * @param idTransaction the id transaction
	 * @param user the user
	 * @param item the item
	 * @param quantity the quantity
	 * @param amount the amount
	 * @param method the method
	 * @param time the time
	 * @param status the status
	 * @param photo the photo
	 * @param like the like
	 */
	public TransactionPOJO(Integer idTransaction, UserPOJO user, ItemPOJO item, float quantity, float amount, byte method, Date time, byte status,
			String photo, boolean like) {
		this.idTransaction = idTransaction;
		this.user = user;
		this.item = item;
		this.quantity = quantity;
		this.amount = amount;
		this.method = method;
		this.time = time;
		this.status = status;
		this.photo = photo;
		this.isLike = like;
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
	public Integer getIdTransaction() {
		return this.idTransaction;
	}

	/**
	 * Gets the item.
	 *
	 * @return the item
	 */
	public ItemPOJO getItem() {
		return this.item;
	}

	/**
	 * Gets the method.
	 *
	 * @return the method
	 */
	public byte getMethod() {
		return this.method;
	}

	/**
	 * Gets the photo.
	 *
	 * @return the photo
	 */
	public String getPhoto() {
		return this.photo;
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
	 * Gets the status.
	 *
	 * @return the status
	 */
	public byte getStatus() {
		return this.status;
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
	 * Gets the time as string.
	 *
	 * @return the time as string
	 */
	@JsonKey(key = ParamDefine.TRANSACTION_TIME)
	public String getTimeAsString() {
		SimpleDateFormat sdf = new SimpleDateFormat(Constant.PATTERN_YYYY_MM_DD_HH_MM_SS);
		return sdf.format(this.time);
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public UserPOJO getUser() {
		return this.user;
	}

	/**
	 * Checks if is like.
	 *
	 * @return the isLike
	 */
	public boolean isLike() {
		return this.isLike;
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
	public void setIdTransaction(Integer idTransaction) {
		this.idTransaction = idTransaction;
	}

	/**
	 * Sets the item.
	 *
	 * @param item the item to set
	 */
	public void setItem(ItemPOJO item) {
		this.item = item;
	}

	/**
	 * Sets the like.
	 *
	 * @param isLike the isLike to set
	 */
	public void setLike(boolean isLike) {
		this.isLike = isLike;
	}

	/**
	 * Sets the method.
	 *
	 * @param method the method to set
	 */
	public void setMethod(byte method) {
		this.method = method;
	}

	/**
	 * Sets the photo.
	 *
	 * @param photo the photo to set
	 */
	public void setPhoto(String photo) {
		this.photo = photo;
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
	 * Sets the status.
	 *
	 * @param status the status to set
	 */
	public void setStatus(byte status) {
		this.status = status;
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
	 * Sets the time.
	 *
	 * @param time the new time
	 * @throws ParseException the parse exception
	 */
	@JsonKey(key = ParamDefine.TRANSACTION_TIME)
	public void setTime(String time) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(Constant.PATTERN_YYYY_MM_DD_HH_MM_SS);
		this.time = sdf.parse(time);
	}

	/**
	 * Sets the user.
	 *
	 * @param user the user to set
	 */
	public void setUser(UserPOJO user) {
		this.user = user;
	}

	/**
	 * To json.
	 *
	 * @return the json object
	 */
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty(ParamDefine.TRANSACTION_AMOUNT, this.amount);
		json.addProperty(ParamDefine.TRANSACTION_ID, this.idTransaction);
		json.addProperty(ParamDefine.TRANSACTION_QUANTITY, this.quantity);

		SimpleDateFormat sdf = new SimpleDateFormat(Constant.PATTERN_YYYY_MM_DD_HH_MM);
		json.addProperty(ParamDefine.TRANSACTION_TIME, sdf.format(this.time));

		return json;
	}
}
