package com.zlmthy.action;

import com.zlmthy.annotations.Controller;
import com.zlmthy.annotations.RequestMapper;
import com.zlmthy.enums.RequestMethod;
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

    @RequestMapper(value = "/hello",method = {RequestMethod.GET,RequestMethod.POST})
    public String hello(HttpRequest request,String name, int age) {
        return name+" say hello world! my age is "+age;
    }
}
