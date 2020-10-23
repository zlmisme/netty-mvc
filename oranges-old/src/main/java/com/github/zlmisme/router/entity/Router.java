package com.github.zlmisme.router.entity;

import com.github.zlmisme.enums.RequestMethod;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author zengliming
 * @ClassName Router
 * @Description TODO
 * @date 2018/9/5 17:07
 */
@Data
public class Router {

    /**
     * 路由的路径
     */
    private String path;

    /**
     * 路由所对应的方法
     */
    private Method method;

    /**
     * 路由的路径
     */
    private String controller;

    /**
     * 请求方式
     */
    private RequestMethod[] httpMethods;

}
