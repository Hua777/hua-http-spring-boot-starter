package com.github.hua777.huahttp.annotation.enumrate;

public enum TransportType {

    /**
     * JSON
     */
    AppJson("application/json; charset=UTF-8"),

    /**
     * Form
     */
    AppXWWWFormUrlencoded("application/x-www-form-urlencoded; charset=UTF-8"),

    /**
     * XML
     */
    AppXml("application/xml; charset=UTF-8"),

    /**
     * 纯文本
     */
    TxtPlain("text/plain; charset=UTF-8"),

    /**
     * us-ascii 编码的 XML
     */
    TxtXml("text/xml; charset=UTF-8"),

    /**
     * HTML
     */
    TxtHtml("text/html; charset=UTF-8"),

    ;

    public String string;

    TransportType(String string) {
        this.string = string;
    }

}
