package com.haimosi.api.apps.thirdparty.data;

import logia.utility.json.annotaion.JsonKey;
import logia.utility.json.annotaion.JsonObject;

@JsonObject
public class StripeRefundData {

	@JsonKey(key = "id")
	String  idCharge;

	@JsonKey(key = "amount")
	Integer amount;

	/**
	 * @return the idCharge
	 */
	public String getIdCharge() {
		return idCharge;
	}

	/**
	 * @param idCharge the idCharge to set
	 */
	public void setIdCharge(String idCharge) {
		this.idCharge = idCharge;
	}

	/**
	 * @return the amount
	 */
	public Integer getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Integer amount) {
		this.amount = amount;
	}

}
