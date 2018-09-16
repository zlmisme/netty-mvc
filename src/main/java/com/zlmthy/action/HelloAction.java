package com.zlmthy.action;

import com.zlmthy.annotations.Controller;
import com.zlmthy.annotations.RequestMapper;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author zengliming
 * @ClassName HelloAction
 * @Description TODO
 * @date 2018/9/5 16:30
 */
@Controller
@RequestMapper(value = "/say")
public class HelloAction {

    @RequestMapper(value = "/hello")
    public String hello(HttpRequest request,String name) {
        return name+" say hello world!";
    }
}
