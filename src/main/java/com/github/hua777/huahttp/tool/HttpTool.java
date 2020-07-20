package com.github.hua777.huahttp.tool;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpTool {

    public static String reqJson(
            String url,
            String method,
            HashMap<String, String> params,
            HashMap<String, Object> datas,
            HashMap<String, String> headers
    ) throws IOException {
        Gson gson = new Gson();
        StringBuilder result = new StringBuilder();
        URL realUrl = new URL(MapTool.concatQueryString(url, MapTool.toUrlQueryString(params)));
        HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Accept", "application/json; charset=UTF-8");
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        connection.setRequestMethod(method.toUpperCase());
        if ("POST".equals(method.toUpperCase()) || "PUT".equals(method.toUpperCase())) {
            if (datas != null) {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
                writer.write(gson.toJson(datas));
                writer.flush();
                writer.close();
            }
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            result.append(line);
        }
        in.close();
        return result.toString();
    }

}
