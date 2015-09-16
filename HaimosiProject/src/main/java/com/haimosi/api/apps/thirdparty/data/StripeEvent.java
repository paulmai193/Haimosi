package com.haimosi.api.apps.thirdparty.data;

import logia.utility.json.annotaion.JsonKey;
import logia.utility.json.annotaion.JsonObject;

@JsonObject
public class StripeEvent {

	@JsonKey(key = "id")
	private String          id;

	@JsonKey(key = "data")
	private StripeEventData data;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the data
	 */
	public StripeEventData getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(StripeEventData data) {
		this.data = data;
	}

}
