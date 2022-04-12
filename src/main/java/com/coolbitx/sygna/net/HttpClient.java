package com.coolbitx.sygna.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Request.Builder;
import okhttp3.Response;

public class HttpClient {
	final static OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(10, TimeUnit.SECONDS)
			.writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

	public static Builder getBuilder(String url, JsonObject headers) {
		Builder builder = new Request.Builder().url(url);
		for (Entry<String, JsonElement> entry : headers.entrySet()) {
			JsonElement element = entry.getValue();
			if (element.isJsonPrimitive()) {
				JsonPrimitive value = element.getAsJsonPrimitive();
				if (value.isString()) {
					builder = builder.addHeader(entry.getKey(), value.getAsString());
				} else {
					throw new JsonSyntaxException("Can't parse value: " + value);
				}
			} else {
				throw new JsonSyntaxException("Can't parse element: " + element);
			}
		}
		return builder;
	}

	public static JsonObject execute(Request request, String method, String url) throws Exception {
		Response response = client.newCall(request).execute();
		int status = response.code();
		System.out.printf("%s %s [%d]\n", method, url, status);
		String jsonData = response.body().string();
		JsonObject result = null;
		try {
			result = new Gson().fromJson(jsonData, JsonObject.class);
		} catch (Exception ex) {
			System.out.println("Json parse target:" + jsonData);
			throw ex;
		}
		return result;
	}

	public static JsonObject get(String url, JsonObject headers) throws Exception {
		Request request = getBuilder(url, headers).build();
		return execute(request, "GET", url);
	}

	public static JsonObject post(String url, JsonObject headers, JsonObject body) throws Exception {
		RequestBody requestBody = RequestBody.create(body.toString(), MediaType.get("application/json; charset=utf-8"));
		Request request = getBuilder(url, headers).post(requestBody).build();
		return execute(request, "POST", url);
	}
}
