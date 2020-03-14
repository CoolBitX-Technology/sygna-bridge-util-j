package com.coolbitx.sygna.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map.Entry;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

public class HttpClient {

    private enum RequestMethod {
        GET, POST;
    }

    public static JsonObject post(String url, JsonObject header, JsonObject postData, int timeout) throws Exception {
        return fetch(url, RequestMethod.POST, header, postData, timeout);
    }

    public static JsonObject get(String url, JsonObject header, int timeout) throws Exception {
        return fetch(url, RequestMethod.GET, header, null, timeout);
    }

    private static JsonObject fetch(String url, RequestMethod method, JsonObject header, JsonObject postData,
            int timeout) throws Exception {
        JsonObject result = null;
        HttpURLConnection conn = null;
        try {
            URL u = new URL(url);
            conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod(method.name());
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            /// Header
            for (Entry<String, JsonElement> entry : header.entrySet()) {
                JsonElement element = entry.getValue();
                if (element.isJsonPrimitive()) {
                    JsonPrimitive value = element.getAsJsonPrimitive();
                    if (value.isString()) {
                        conn.setRequestProperty(entry.getKey(), value.getAsString());
                    } else {
                        throw new JsonSyntaxException("Can't parse value: " + value);
                    }
                } else {
                    throw new JsonSyntaxException("Can't parse element: " + element);
                }

            }
            conn.setRequestProperty("Accept", "application/json");
            if (RequestMethod.GET == method) {
                conn.setRequestProperty("Content-length", "0");
                conn.connect();
            } else if (RequestMethod.POST == method) {
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                String jsonStr = postData.toString();
                writer.write(jsonStr);
                writer.close();
            } else {
                throw new Exception("Unexpected HTTP Method:" + method.name());
            }

            int status = conn.getResponseCode();
            System.out.printf("%s %s [%d]\n", method, url, status);
            InputStreamReader isr;
            if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_CREATED) {
                isr = new InputStreamReader(conn.getInputStream());
            } else {
                isr = new InputStreamReader(conn.getErrorStream());
            }
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            try {
                result = new Gson().fromJson(sb.toString(), JsonObject.class);
            } catch (Exception ex) {
                System.out.println("Json parse target:" + sb.toString());
                throw ex;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
