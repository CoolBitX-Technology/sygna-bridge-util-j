package com.coolbitx.sygna.bridge.model;

public class Permission extends Packet {

	public Permission(String signature, String transfer_id, String permission_status) {
		super(signature);
		this.transfer_id = transfer_id;
		this.permission_status = permission_status;
	}

	private String transfer_id;
	private String permission_status;

	public String getTransfer_id() {
		return transfer_id;
	}

	public String getPermission_status() {
		return permission_status;
	}

}
