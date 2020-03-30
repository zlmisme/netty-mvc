package com.zlmthy.example;

import com.alibaba.fastjson.JSON;
import com.zlmthy.annotations.RequestMapper;
import com.zlmthy.annotations.XxAutowired;
import com.zlmthy.annotations.XxController;
import com.zlmthy.context.IContext;
import com.zlmthy.enums.RequestMethod;

@XxController
@RequestMapper(value = "/say")
public class SsController {

    @XxAutowired
    private SsServer ssServer;

    @RequestMapper(value = "/hello",method = {RequestMethod.GET,RequestMethod.POST})
    public void hello() {
        System.out.println(JSON.toJSONString(IContext.getRequest()));
        if (ssServer == null){
            System.out.println("null");
        }else {
            ssServer.say();
        }
        IContext.json("say hello world! my age is");
    }
}
