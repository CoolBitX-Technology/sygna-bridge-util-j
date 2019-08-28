package com.coolbitx.sygna.bridge.model;

public class Transaction extends Packet {

	private String transfer_id;
	private String txid;

	public Transaction(String transfer_id, String txid, String signature) {
		super(signature);
		this.transfer_id = transfer_id;
		this.txid = txid;
	}

	public String getTransfer_id() {
		return transfer_id;
	}

	public String getTxid() {
		return txid;
	}

}
