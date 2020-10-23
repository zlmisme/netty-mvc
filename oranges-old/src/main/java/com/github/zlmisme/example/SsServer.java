package com.github.zlmisme.example;

import com.github.zlmisme.annotations.XxAutowired;
import com.github.zlmisme.annotations.XxServer;

@XxServer()
public class SsServer {

    @XxAutowired
    private SsComponent ssComponent;

    public void say() {
        System.out.println(ssComponent.say(" hello world"));
    }

}
