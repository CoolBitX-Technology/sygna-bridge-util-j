package com.coolbitx.sygna.bridge.model;

import com.coolbitx.sygna.util.DateUtils;

public class Permission extends Packet {

	public Permission(String signature, String transfer_id, String permission_status) {
		super(signature);
		this.transfer_id = transfer_id;
		this.permission_status = permission_status;
	}
	public Permission(String signature, String transfer_id, String permission_status,long expire_date) {
		super(signature);
		this.transfer_id = transfer_id;
		this.permission_status = permission_status;
		this.expire_date = expire_date;
	}

	private String transfer_id;
	private String permission_status;
	private long expire_date = 0l;

	public String getTransfer_id() {
		return transfer_id;
	}

	public String getPermission_status() {
		return permission_status;
	}
	public long getExpire_date() {
		return expire_date;
	}
	@Override
	public void check() throws Exception {
		super.check();
		if(this.expire_date != 0l) {
			DateUtils.checkExpireDateValid(this.expire_date);
		}
	} 

}
