package com.coolbitx.sygna.bridge.model;

public abstract class Packet {
	private String signature;

	public Packet(String signature) {
		super();
		this.signature = signature;
	}

	public void check() throws Exception {
		if (this.signature == null || this.signature.length() != 128) {
			throw new Exception("Expect signature length to be 128.");
		}
	}

	public String getSignature() {
		return signature;
	}

}
