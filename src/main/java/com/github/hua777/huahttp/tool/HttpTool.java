package com.github.hua777.huahttp.tool;

import com.github.hua777.huahttp.annotation.enumrate.TransportType;
import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpTool {

    private static String req(
            String url,
            String method,
            HashMap<String, String> params,
            String bodies,
            HashMap<String, String> headers,
            TransportType contentType,
            TransportType acceptType
    ) throws IOException {
        StringBuilder result = new StringBuilder();
        URL realUrl = new URL(MapTool.concatQueryString(url, MapTool.toUrlQueryString(params)));
        HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("Content-Type", contentType.string);
        connection.setRequestProperty("Accept", acceptType.string);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        connection.setRequestMethod(method.toUpperCase());
        if ("POST".equals(method.toUpperCase()) || "PUT".equals(method.toUpperCase())) {
            if (bodies != null) {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
                writer.write(bodies);
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

    public static String req(
            String url,
            String method,
            HashMap<String, String> params,
            HashMap<String, Object> bodies,
            HashMap<String, String> headers
    ) throws IOException {
        Gson gson = new Gson();
        return req(
                url,
                method,
                params,
                (bodies == null ? null : gson.toJson(bodies)),
                headers,
                TransportType.AppJson,
                TransportType.AppJson
        );
    }

    public static String req(
            String url,
            String method,
            HashMap<String, String> params,
            String bodies,
            HashMap<String, String> headers
    ) throws IOException {
        return req(
                url,
                method,
                params,
                bodies,
                headers,
                TransportType.AppXWWWFormUrlencoded,
                TransportType.AppJson
        );
    }

}
