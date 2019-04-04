package com.zlmthy.example;

import com.zlmthy.annotations.XxAutowired;
import com.zlmthy.annotations.XxServer;

@XxServer()
public class SsServer {

    @XxAutowired
    private SsComponent ssComponent;

    public void say(){
        System.out.println(ssComponent.say(" hello world"));
    }

}
