package com.hua.sss.huahttp.tool;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class MapTool {

    public static String toUrlQueryString(Map<String, String> map) throws UnsupportedEncodingException {
        if (map == null) {
            return "";
        }
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().equals("")) {
                query.append(URLEncoder.encode(entry.getKey(), "UTF-8")).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
            }
        }
        String result = query.toString();
        if (result.endsWith("&")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public static String concatQueryString(String url, String queryString) {
        if (!url.contains("?")) {
            url += "?";
        } else if (url.contains("?") && !url.endsWith("&") && !url.endsWith("&")) {
            url += "&";
        }
        return url + queryString;
    }

}
