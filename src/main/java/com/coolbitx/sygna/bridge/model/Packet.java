package com.coolbitx.sygna.bridge.model;

public abstract class Packet {
	private String signature;

	public void check() throws Exception{
		if (this.signature.length() != 128) {
			throw new Exception("Expect signature length to be 128.");
		}
	}

	public String getSignature() {
		return signature;
	}

}
