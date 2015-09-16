package com.haimosi.api.apps.thirdparty.data;

import logia.utility.json.annotaion.JsonKey;
import logia.utility.json.annotaion.JsonObject;

@JsonObject
public class StripeEventData {

	@JsonKey(key = "object")
	StripeRefundData refund;

	public StripeRefundData getRefund() {
		return refund;
	}

	public void setRefund(StripeRefundData refund) {
		this.refund = refund;
	}
}
