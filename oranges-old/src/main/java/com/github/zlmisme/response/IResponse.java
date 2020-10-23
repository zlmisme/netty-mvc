package com.github.zlmisme.response;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zengliming
 * @date 2020/3/30
 * @since 1.0.0
 */
@Data
public final class IResponse {

    private Map<String, String> headers = new HashMap<>(8);

    private String contentType;

    private String httpContent;

    private IResponse() {
    }

    public static IResponse init() {
        IResponse response = new IResponse();
        response.contentType = "application/json;charset=utf-8";
        return response;
    }
}
