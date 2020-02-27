package com.coolbitx.sygna.bridge.enums;

import com.google.gson.annotations.SerializedName;

public enum RejectMessage {
	@SerializedName("unsupported_currency")
	BVRC001("unsupported_currency"), 
	@SerializedName("service_downtime")
	BVRC002("service_downtime"), 
	@SerializedName("exceed_trading_volume")
	BVRC003("exceed_trading_volume"),
	@SerializedName("compliance_check_fail")
	BVRC004("compliance_check_fail"), 
	@SerializedName("other")
	BVRC999("other"), NULL(null);

	private String rejectMessage;

	RejectMessage(String rejectMessage) {
		this.rejectMessage = rejectMessage;
	}

	public String getRejectMessage() {
		return this.rejectMessage;
	}
}
