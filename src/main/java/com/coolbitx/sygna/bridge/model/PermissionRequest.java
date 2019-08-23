package com.coolbitx.sygna.bridge.model;

import com.google.gson.JsonObject;

public class PermissionRequest extends Packet {

	private String private_info;
	private JsonObject transaction;
	private String data_dat;

	public String getPrivate_info() {
		return private_info;
	}

	public JsonObject getTransaction() {
		return transaction;
	}

	public String getData_dat() {
		return data_dat;
	}

	
}
