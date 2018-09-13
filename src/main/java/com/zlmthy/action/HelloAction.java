package com.zlmthy.action;

import com.zlmthy.annotations.RequestMapper;

/**
 * @author zengliming
 * @ClassName HelloAction
 * @Description TODO
 * @date 2018/9/5 16:30
 */
@RequestMapper(value = "/say")
public class HelloAction {

    @RequestMapper(value = "/hello")
    public String hello(){
        return "hello world!";
    }
}
