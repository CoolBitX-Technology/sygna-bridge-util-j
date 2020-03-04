package com.coolbitx.sygna.bridge.model;

import com.coolbitx.sygna.util.Validator;

public abstract class Packet {
	private String signature;

	public Packet(String signature) {
		super();
		this.signature = signature;
	}

	public void check() throws Exception {
		Validator.validateSignature(signature);
	}

	public String getSignature() {
		return signature;
	}

}
