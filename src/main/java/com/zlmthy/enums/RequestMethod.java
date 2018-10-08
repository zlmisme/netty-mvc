package com.zlmthy.enums;

/**
 * @author zengliming
 */
public enum RequestMethod {
    // 请求方式
    GET("GET"),POST("POST");

    public String value;

    private RequestMethod(String value){
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
