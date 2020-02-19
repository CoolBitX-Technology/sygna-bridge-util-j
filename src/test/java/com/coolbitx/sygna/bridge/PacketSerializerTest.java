package com.coolbitx.sygna.bridge;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.coolbitx.sygna.bridge.model.Permission;
import com.coolbitx.sygna.bridge.model.PermissionRequest;
import com.coolbitx.sygna.json.PacketSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PacketSerializerTest {
	Gson gson = new GsonBuilder()
			.registerTypeAdapter(Permission.class, new PacketSerializer())
			.registerTypeAdapter(PermissionRequest.class, new PacketSerializer())
			.create();
	
	@Test
	public void testPermissionSerialize() {
		Permission pemission = new Permission("signature","transfer_id","permission_status");
		JsonElement jsonElement = gson.toJsonTree(pemission,Permission.class);
		assertEquals(jsonElement.toString(), "{\"transfer_id\":\"transfer_id\",\"permission_status\":\"permission_status\",\"signature\":\"signature\"}");
		
		Permission pemission1 = new Permission("signature","transfer_id","permission_status",0l);
		JsonElement jsonElement1 = gson.toJsonTree(pemission1,Permission.class);
		assertEquals(jsonElement1.toString(), "{\"transfer_id\":\"transfer_id\",\"permission_status\":\"permission_status\",\"signature\":\"signature\"}");
		
		Permission pemission2 = new Permission("signature","transfer_id","permission_status",1582107090l);
		JsonElement jsonElement2 = gson.toJsonTree(pemission2,Permission.class);
		assertEquals(jsonElement2.toString(), "{\"transfer_id\":\"transfer_id\",\"permission_status\":\"permission_status\",\"expire_date\":1582107090,\"signature\":\"signature\"}");
		
		Permission pemission3 = new Permission("signature","transfer_id","permission_status",1582107090000l);
		JsonElement jsonElement3 = gson.toJsonTree(pemission3,Permission.class);
		assertEquals(jsonElement3.toString(), "{\"transfer_id\":\"transfer_id\",\"permission_status\":\"permission_status\",\"expire_date\":1582107090000,\"signature\":\"signature\"}");
	}
	
	@Test
	public void testPermissionRequestSerialize() {
		String permReqSig = "48f5220f45d65c7f586e5b147f3ebd7e1803c1a86183498b62d74151517311465a80a3d1b742df191ffaee6329192b6d63e49ea1598467c001cc732e8df6e566";
		String privateInfo = "0405a39f02fb74cb0a748ff70adf0e4b7a8910befbaa536682fd3e4d1feed551c4e5d27bf85e03836bbb975e83e620529139d31644cee17f60f089b5b44a89513c336e262dd2a686e6460339ec3a8eacbdeda77bbb7d18bec45bb4288bd959dae803176ddf535b634e6ea9367fffb5f811a10c540a4eacbd8ceb9a4b5631a1b64dfa7ac9c4f52dff09f14d18652e9eb30b0b4e56a145c6d912a89c5442a0673b83728cf6cbc527e3004eeaf609573edb2f2844c1ec68a1cf97917a9213ce695eb3624a32f7";
		String dataDate = "2019-07-29T06:29:00.123Z";
		JsonObject transaction = new JsonObject();
		
		JsonArray originator_addrs = new JsonArray();
		originator_addrs.add("3KvJ1uHPShhEAWyqsBEzhfXyeh1TXKAd7D");
			
		JsonArray beneficiary_addrs = new JsonArray();
		beneficiary_addrs.add("3F4ReDwiMLu8LrAiXwwD2DhH8U9xMrUzUf");
		
		transaction.addProperty("originator_vasp_code", "VASPUSNY1");
		transaction.add("originator_addrs", originator_addrs);
		transaction.addProperty("beneficiary_vasp_code", "VASPUSNY2");
		transaction.add("beneficiary_addrs", beneficiary_addrs);
		transaction.addProperty("transaction_currency", "0x80000000");
		transaction.addProperty("amount", 0.973);
		
		PermissionRequest permReq = new PermissionRequest(permReqSig, privateInfo, transaction, dataDate);
		JsonElement jsonElement = gson.toJsonTree(permReq,PermissionRequest.class);
		assertEquals(jsonElement.toString(), "{\"private_info\":\"0405a39f02fb74cb0a748ff70adf0e4b7a8910befbaa536682fd3e4d1feed551c4e5d27bf85e03836bbb975e83e620529139d31644cee17f60f089b5b44a89513c336e262dd2a686e6460339ec3a8eacbdeda77bbb7d18bec45bb4288bd959dae803176ddf535b634e6ea9367fffb5f811a10c540a4eacbd8ceb9a4b5631a1b64dfa7ac9c4f52dff09f14d18652e9eb30b0b4e56a145c6d912a89c5442a0673b83728cf6cbc527e3004eeaf609573edb2f2844c1ec68a1cf97917a9213ce695eb3624a32f7\",\"transaction\":{\"originator_vasp_code\":\"VASPUSNY1\",\"originator_addrs\":[\"3KvJ1uHPShhEAWyqsBEzhfXyeh1TXKAd7D\"],\"beneficiary_vasp_code\":\"VASPUSNY2\",\"beneficiary_addrs\":[\"3F4ReDwiMLu8LrAiXwwD2DhH8U9xMrUzUf\"],\"transaction_currency\":\"0x80000000\",\"amount\":0.973},\"data_dt\":\"2019-07-29T06:29:00.123Z\",\"signature\":\"48f5220f45d65c7f586e5b147f3ebd7e1803c1a86183498b62d74151517311465a80a3d1b742df191ffaee6329192b6d63e49ea1598467c001cc732e8df6e566\"}");
		
		PermissionRequest permReq1 = new PermissionRequest(permReqSig, privateInfo, transaction, dataDate,0l);
		JsonElement jsonElement1 = gson.toJsonTree(permReq1,PermissionRequest.class);
		assertEquals(jsonElement1.toString(), "{\"private_info\":\"0405a39f02fb74cb0a748ff70adf0e4b7a8910befbaa536682fd3e4d1feed551c4e5d27bf85e03836bbb975e83e620529139d31644cee17f60f089b5b44a89513c336e262dd2a686e6460339ec3a8eacbdeda77bbb7d18bec45bb4288bd959dae803176ddf535b634e6ea9367fffb5f811a10c540a4eacbd8ceb9a4b5631a1b64dfa7ac9c4f52dff09f14d18652e9eb30b0b4e56a145c6d912a89c5442a0673b83728cf6cbc527e3004eeaf609573edb2f2844c1ec68a1cf97917a9213ce695eb3624a32f7\",\"transaction\":{\"originator_vasp_code\":\"VASPUSNY1\",\"originator_addrs\":[\"3KvJ1uHPShhEAWyqsBEzhfXyeh1TXKAd7D\"],\"beneficiary_vasp_code\":\"VASPUSNY2\",\"beneficiary_addrs\":[\"3F4ReDwiMLu8LrAiXwwD2DhH8U9xMrUzUf\"],\"transaction_currency\":\"0x80000000\",\"amount\":0.973},\"data_dt\":\"2019-07-29T06:29:00.123Z\",\"signature\":\"48f5220f45d65c7f586e5b147f3ebd7e1803c1a86183498b62d74151517311465a80a3d1b742df191ffaee6329192b6d63e49ea1598467c001cc732e8df6e566\"}");
		
		PermissionRequest permReq2 = new PermissionRequest(permReqSig, privateInfo, transaction, dataDate,1582107794l);
		JsonElement jsonElement2 = gson.toJsonTree(permReq2,PermissionRequest.class);
		assertEquals(jsonElement2.toString(), "{\"private_info\":\"0405a39f02fb74cb0a748ff70adf0e4b7a8910befbaa536682fd3e4d1feed551c4e5d27bf85e03836bbb975e83e620529139d31644cee17f60f089b5b44a89513c336e262dd2a686e6460339ec3a8eacbdeda77bbb7d18bec45bb4288bd959dae803176ddf535b634e6ea9367fffb5f811a10c540a4eacbd8ceb9a4b5631a1b64dfa7ac9c4f52dff09f14d18652e9eb30b0b4e56a145c6d912a89c5442a0673b83728cf6cbc527e3004eeaf609573edb2f2844c1ec68a1cf97917a9213ce695eb3624a32f7\",\"transaction\":{\"originator_vasp_code\":\"VASPUSNY1\",\"originator_addrs\":[\"3KvJ1uHPShhEAWyqsBEzhfXyeh1TXKAd7D\"],\"beneficiary_vasp_code\":\"VASPUSNY2\",\"beneficiary_addrs\":[\"3F4ReDwiMLu8LrAiXwwD2DhH8U9xMrUzUf\"],\"transaction_currency\":\"0x80000000\",\"amount\":0.973},\"data_dt\":\"2019-07-29T06:29:00.123Z\",\"expire_date\":1582107794,\"signature\":\"48f5220f45d65c7f586e5b147f3ebd7e1803c1a86183498b62d74151517311465a80a3d1b742df191ffaee6329192b6d63e49ea1598467c001cc732e8df6e566\"}");
		
		PermissionRequest permReq3 = new PermissionRequest(permReqSig, privateInfo, transaction, dataDate,1582107847000l);
		JsonElement jsonElement3 = gson.toJsonTree(permReq3,PermissionRequest.class);
		assertEquals(jsonElement3.toString(), "{\"private_info\":\"0405a39f02fb74cb0a748ff70adf0e4b7a8910befbaa536682fd3e4d1feed551c4e5d27bf85e03836bbb975e83e620529139d31644cee17f60f089b5b44a89513c336e262dd2a686e6460339ec3a8eacbdeda77bbb7d18bec45bb4288bd959dae803176ddf535b634e6ea9367fffb5f811a10c540a4eacbd8ceb9a4b5631a1b64dfa7ac9c4f52dff09f14d18652e9eb30b0b4e56a145c6d912a89c5442a0673b83728cf6cbc527e3004eeaf609573edb2f2844c1ec68a1cf97917a9213ce695eb3624a32f7\",\"transaction\":{\"originator_vasp_code\":\"VASPUSNY1\",\"originator_addrs\":[\"3KvJ1uHPShhEAWyqsBEzhfXyeh1TXKAd7D\"],\"beneficiary_vasp_code\":\"VASPUSNY2\",\"beneficiary_addrs\":[\"3F4ReDwiMLu8LrAiXwwD2DhH8U9xMrUzUf\"],\"transaction_currency\":\"0x80000000\",\"amount\":0.973},\"data_dt\":\"2019-07-29T06:29:00.123Z\",\"expire_date\":1582107847000,\"signature\":\"48f5220f45d65c7f586e5b147f3ebd7e1803c1a86183498b62d74151517311465a80a3d1b742df191ffaee6329192b6d63e49ea1598467c001cc732e8df6e566\"}");
	}
}
