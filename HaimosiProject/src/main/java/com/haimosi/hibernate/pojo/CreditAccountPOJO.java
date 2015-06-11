package com.haimosi.hibernate.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import logia.utility.json.annotaion.JsonKey;
import logia.utility.string.EncryptionUtils;

import org.hibernate.annotations.DynamicUpdate;

import com.google.gson.JsonObject;
import com.haimosi.define.Config;
import com.haimosi.param.ParamDefine;

/**
 * The Class CreditAccountPOJO.
 * 
 * @author Paul Mai
 */
@Entity
@Table(name = "creditaccount", catalog = "paulmai")
@DynamicUpdate(value = true)
@logia.utility.json.annotaion.JsonObject
public class CreditAccountPOJO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id credit account. */
	@Id
	@GeneratedValue
	@Column(name = "idcreditaccount", nullable = false)
	@JsonKey(key = ParamDefine.CARD_ID)
	private Integer           idCreditAccount;

	/** The card name. */
	@Column(name = "cardname", nullable = false, length = 30)
	@JsonKey(key = ParamDefine.CARD_NAME)
	private String            cardName;

	/** The card number. */
	@Column(name = "number", nullable = false, length = 16)
	@JsonKey(key = ParamDefine.CARD_NUMBER)
	private String            cardNumber;

	/** The expire date. */
	@Column(name = "expiredate", nullable = false, length = 7)
	@JsonKey(key = ParamDefine.EXPIRE)
	private String            expireDate;

	/** The cvv number. */
	@Column(name = "cvvnumber", nullable = false, length = 4)
	@JsonKey(key = ParamDefine.CVV_NUMBER)
	private String            cvvNumber;

	/** The user. */
	@OneToOne(fetch = FetchType.EAGER)
	@PrimaryKeyJoinColumn
	private UserPOJO          user;

	/**
	 * Instantiates a new credit account pojo.
	 */
	public CreditAccountPOJO() {

	}

	/**
	 * Instantiates a new credit account pojo.
	 *
	 * @param idCard the id card
	 * @param name the card name
	 * @param number the card number, have 16-degree
	 * @param expire the expire, like mm/yyyy
	 * @param cvv the cvv number
	 */
	public CreditAccountPOJO(Integer idCard, String name, String number, String expire, String cvv) {
		this.idCreditAccount = idCard;
		this.cardName = name;
		this.cardNumber = number;
		this.expireDate = expire;
		this.cvvNumber = cvv;
	}

	/**
	 * Gets the card name.
	 *
	 * @return the cardName
	 */
	public String getCardName() {
		String s = EncryptionUtils.decode(this.cardName, Config.encrypt_password);
		if (s != null) {
			return s;
		}
		else {
			return this.cardName;
		}
	}

	/**
	 * Gets the card number.
	 *
	 * @return the cardNumber
	 */
	public String getCardNumber() {
		String s = EncryptionUtils.decode(this.cardNumber, Config.encrypt_password);
		if (s != null) {
			return s;
		}
		else {
			return this.cardNumber;
		}
	}

	/**
	 * Gets the cvv number.
	 *
	 * @return the cvvNumber
	 */
	public String getCvvNumber() {
		String s = EncryptionUtils.decode(this.cvvNumber, Config.encrypt_password);
		if (s != null) {
			return s;
		}
		else {
			return this.cvvNumber;
		}
	}

	/**
	 * Gets the expire date.
	 *
	 * @return the expireDate
	 */
	public String getExpireDate() {
		String s = EncryptionUtils.decode(this.expireDate, Config.encrypt_password);
		if (s != null) {
			return s;
		}
		else {
			return this.expireDate;
		}
	}

	/**
	 * Gets the id credit account.
	 *
	 * @return the idCreditAccount
	 */
	public Integer getIdCreditAccount() {
		return this.idCreditAccount;
	}

	/**
	 * Sets the card name.
	 *
	 * @param cardName the cardName to set
	 */
	public void setCardName(String cardName) {
		String s = EncryptionUtils.encode(cardName, Config.encrypt_password);
		if (s != null) {
			this.cardName = s;
		}
		else {
			this.cardName = cardName;
		}
	}

	/**
	 * Sets the card number.
	 *
	 * @param cardNumber the cardNumber to set
	 */
	public void setCardNumber(String cardNumber) {
		String s = EncryptionUtils.encode(cardNumber, Config.encrypt_password);
		if (s != null) {
			this.cardName = s;
		}
		else {
			this.cardNumber = cardNumber;
		}
	}

	/**
	 * Sets the cvv number.
	 *
	 * @param cvvNumber the cvvNumber to set
	 */
	public void setCvvNumber(String cvvNumber) {
		String s = EncryptionUtils.encode(cvvNumber, Config.encrypt_password);
		if (s != null) {
			this.cardName = s;
		}
		else {
			this.cvvNumber = cvvNumber;
		}
	}

	/**
	 * Sets the expire date.
	 *
	 * @param expireDate the expireDate to set
	 */
	public void setExpireDate(String expireDate) {
		String s = EncryptionUtils.encode(expireDate, Config.encrypt_password);
		if (s != null) {
			this.cardName = s;
		}
		else {
			this.expireDate = expireDate;
		}
	}

	/**
	 * Sets the id credit account.
	 *
	 * @param idCreditAccount the idCreditAccount to set
	 */
	public void setIdCreditAccount(Integer idCreditAccount) {
		this.idCreditAccount = idCreditAccount;
	}

	/**
	 * To json.
	 *
	 * @return the json object
	 */
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty(ParamDefine.CARD_ID, this.getIdCreditAccount());
		json.addProperty(ParamDefine.CARD_NAME, this.getCardName());
		json.addProperty(ParamDefine.CARD_NUMBER, this.getCardNumber());
		json.addProperty(ParamDefine.CVV_NUMBER, this.getCvvNumber());
		json.addProperty(ParamDefine.EXPIRE, this.getExpireDate());

		return json;
	}

}
