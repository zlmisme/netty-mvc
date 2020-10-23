package com.github.zlmisme.enums;

/**
 * @author zengliming
 */
public enum RequestMethod {
    // 请求方式
    GET("GET"), POST("POST");

    private final String value;

    RequestMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
