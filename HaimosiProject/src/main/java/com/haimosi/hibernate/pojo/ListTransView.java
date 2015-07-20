package com.haimosi.hibernate.pojo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import logia.utility.json.annotaion.JsonKey;

import com.haimosi.define.Constant;
import com.haimosi.param.ParamDefine;

/**
 * The Class ListTransView.
 * 
 * @author Paul Mai
 */
@Entity
@Table(name = "listtransview", catalog = "paulmai")
@logia.utility.json.annotaion.JsonObject
/*
 * @Indexed
 * 
 * @AnalyzerDefs(value = { @AnalyzerDef(name = "index", tokenizer = @TokenizerDef(factory = WhitespaceTokenizerFactory.class), filters = {
 * 
 * @TokenFilterDef(factory = WordDelimiterFilterFactory.class, params = { @Parameter(name = "generateWordParts", value = "1"),
 * 
 * @Parameter(name = "generateNumberParts", value = "1"), @Parameter(name = "catenateWords", value = "1"),
 * 
 * @Parameter(name = "catenateNumbers", value = "1"), @Parameter(name = "catenateAll", value = "0"),
 * 
 * @Parameter(name = "splitOnCaseChange", value = "1") }),
 * 
 * @TokenFilterDef(factory = LowerCaseFilterFactory.class),
 * 
 * @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class),
 * 
 * @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = { @Parameter(name = "language", value = "English"),
 * 
 * @Parameter(name = "protected", value = "protwords.txt") }) }), })
 */
public class ListTransView implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id transaction. */
	@Id
	@Column(name = "idtransaction", nullable = false)
	@JsonKey(key = ParamDefine.TRANSACTION_ID)
	private int               idTransaction;

	/** The quantity. */
	@Column(name = "quantity", nullable = false)
	@JsonKey(key = ParamDefine.TRANSACTION_QUANTITY)
	private float             quantity;

	/** The amount. */
	@Column(name = "amount", nullable = false)
	@JsonKey(key = ParamDefine.TRANSACTION_AMOUNT)
	private float             amount;

	/** The time. */
	@Column(name = "time", nullable = false)
	// @Field(index = Index.YES)
	// @DateBridge(resolution = Resolution.SECOND)
	private Date              time;

	/** The photo. */
	@Column(name = "photo", nullable = true, length = 50)
	private String            photo;

	/** The status. */
	@Column(name = "status", nullable = false)
	@JsonKey(key = ParamDefine.TRANSACTION_STATUS)
	private byte              status;

	/** The method. */
	@Column(name = "method", nullable = false)
	@JsonKey(key = ParamDefine.METHOD)
	private byte              method;

	/** The name. */
	@JsonKey(key = ParamDefine.ITEM_NAME)
	// @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	// @Analyzer(definition = "index")
	private String            name;

	/** The unit. */
	@Column(name = "unit", nullable = false, length = 5)
	@JsonKey(key = ParamDefine.ITEM_UNIT)
	private String            unit;

	/**
	 * Instantiates a new list trans view.
	 */
	public ListTransView() {

	}

	/**
	 * Gets the id transaction.
	 *
	 * @return the idTransaction
	 */
	public int getIdTransaction() {
		return idTransaction;
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
	 * Gets the quantity.
	 *
	 * @return the quantity
	 */
	public float getQuantity() {
		return quantity;
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
	 * Gets the amount.
	 *
	 * @return the amount
	 */
	public float getAmount() {
		return amount;
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
	 * Gets the time.
	 *
	 * @return the time
	 */
	public Date getTime() {
		return time;
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
	 * Gets the photo.
	 *
	 * @return the photo
	 */
	public String getPhoto() {
		return photo;
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
	 * Gets the status.
	 *
	 * @return the status
	 */
	public byte getStatus() {
		return status;
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
	 * Gets the method.
	 *
	 * @return the method
	 */
	public byte getMethod() {
		return method;
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
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the unit.
	 *
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * Sets the unit.
	 *
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

}
