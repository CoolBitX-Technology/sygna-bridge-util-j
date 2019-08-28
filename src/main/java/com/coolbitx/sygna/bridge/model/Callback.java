package com.coolbitx.sygna.bridge.model;

public class Callback extends Packet {

	public Callback(String signature, String callback_url) {
		super(signature);
		this.callback_url = callback_url;
	}

	private String callback_url;

	public String getCallback_url() {
		return callback_url;
	}

}
