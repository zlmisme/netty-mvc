package com.zlmthy.example;

import com.zlmthy.annotations.RequestMapper;
import com.zlmthy.annotations.XxAutowired;
import com.zlmthy.annotations.XxController;
import com.zlmthy.enums.RequestMethod;

@XxController
@RequestMapper(value = "/say")
public class SsController {

    @XxAutowired
    private SsServer ssServer;

    @RequestMapper(value = "/hello",method = {RequestMethod.GET,RequestMethod.POST})
    public String hello() {
        if (ssServer == null){
            System.out.println("null");
        }else {
            ssServer.say();
        }
        return "say hello world! my age is";
    }
}
