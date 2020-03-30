package com.zlmthy.request;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zengliming
 * @date 2020/3/30
 * @since 1.0.0
 */
public class IRequest {

    private String method ;

    private String url ;

    private IRequest(){}

    private Map<String,String> headers = new HashMap<>(8) ;

    public static IRequest init(FullHttpRequest httpRequest){
        IRequest request = new IRequest() ;
        request.method = httpRequest.method().name();
        request.url = httpRequest.uri();

        //build headers
        buildHeaders(httpRequest, request);


        return request ;
    }

    /**
     * build headers
     * @param httpRequest io.netty.httprequest
     * @param request cicada request
     */
    private static void buildHeaders(FullHttpRequest httpRequest, IRequest request) {
        for (Map.Entry<String, String> entry : httpRequest.headers().entries()) {
            request.headers.put(entry.getKey(),entry.getValue());
        }
    }


    public String getMethod() {
        return this.method;
    }

    public String getUrl() {
        return this.url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
