package com.github.zlmisme.example;

import com.alibaba.fastjson.JSON;
import com.github.zlmisme.annotations.RequestMapper;
import com.github.zlmisme.annotations.XxAutowired;
import com.github.zlmisme.annotations.XxController;
import com.github.zlmisme.context.IContext;
import com.github.zlmisme.enums.RequestMethod;

@XxController
@RequestMapper(value = "/say")
public class SsController {

    @XxAutowired
    private SsServer ssServer;

    @RequestMapper(value = "/hello", method = {RequestMethod.GET, RequestMethod.POST})
    public void hello() {
        System.out.println(JSON.toJSONString(IContext.getRequest()));
        if (ssServer == null) {
            System.out.println("null");
        } else {
            ssServer.say();
        }
        IContext.json("say hello world! my age is");
    }
}
