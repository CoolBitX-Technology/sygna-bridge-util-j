package com.coolbitx.sygna.bridge.model;

import com.google.gson.JsonObject;

public class PermissionRequest extends Packet {

	public PermissionRequest(String signature, String private_info, JsonObject transaction, String data_dt) {
		super(signature);
		this.private_info = private_info;
		this.transaction = transaction;
		this.data_dt = data_dt;
	}

	private String private_info;
	private JsonObject transaction;
	private String data_dt;

	public String getPrivate_info() {
		return private_info;
	}

	public JsonObject getTransaction() {
		return transaction;
	}

	public String getData_dt() {
		return data_dt;
	}

}
