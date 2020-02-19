package com.coolbitx.sygna.json;

import java.lang.reflect.Type;

import com.coolbitx.sygna.bridge.model.Packet;
import com.coolbitx.sygna.bridge.model.Permission;
import com.coolbitx.sygna.bridge.model.PermissionRequest;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PacketSerializer implements JsonSerializer<Packet> {

	@Override
	public JsonElement serialize(Packet src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jsonObj = (JsonObject) new GsonBuilder().create().toJsonTree(src);
		if(src instanceof PermissionRequest) {
			if (((PermissionRequest)src).getExpire_date() == 0l) {
				jsonObj.remove("expire_date");
			}
		}
		if(src instanceof Permission) {
			if (((Permission)src).getExpire_date() == 0l) {
				jsonObj.remove("expire_date");
			}
		}
		return jsonObj;
	}

}
