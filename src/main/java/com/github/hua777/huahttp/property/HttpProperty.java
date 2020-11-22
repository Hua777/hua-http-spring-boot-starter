package com.github.hua777.huahttp.property;

public class HttpProperty {

    String scanPackages;

    Integer httpTimeoutSeconds = 60;

    Boolean httpRedirects = true;

    String paramDateFormat = "yyyy-MM-dd HH:mm:ss";

    public String getScanPackages() {
        return scanPackages;
    }

    public void setScanPackages(String scanPackages) {
        this.scanPackages = scanPackages;
    }

    public Integer getHttpTimeoutSeconds() {
        return httpTimeoutSeconds;
    }

    public void setHttpTimeoutSeconds(Integer httpTimeoutSeconds) {
        this.httpTimeoutSeconds = httpTimeoutSeconds;
    }

    public Boolean getHttpRedirects() {
        return httpRedirects;
    }

    public void setHttpRedirects(Boolean httpRedirects) {
        this.httpRedirects = httpRedirects;
    }

    public String getParamDateFormat() {
        return paramDateFormat;
    }

    public void setParamDateFormat(String paramDateFormat) {
        this.paramDateFormat = paramDateFormat;
    }
}
