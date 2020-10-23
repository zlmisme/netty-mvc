package com.github.zlmisme.context;

import com.alibaba.fastjson.JSON;
import com.github.zlmisme.request.IRequest;
import com.github.zlmisme.response.IResponse;

/**
 * @author zengliming
 * @date 2020/3/30
 * @since 1.0.0
 */
public final class IContext {

    private IRequest iRequest;

    private IResponse iResponse;

    public IContext(IRequest iRequest, IResponse iResponse) {
        this.iRequest = iRequest;
        this.iResponse = iResponse;
    }

    public static void json(Object o) {
        ThreadLocalHolder.getContext().iResponse.setContentType("application/json;charset=utf-8");
        ThreadLocalHolder.getContext().iResponse.setHttpContent(JSON.toJSONString(o));
    }

    public static void setContext(IContext context) {
        ThreadLocalHolder.setContext(context);
    }


    public static void removeContext() {
        ThreadLocalHolder.removeContext();
    }

    public static IRequest getRequest() {
        return IContext.getContext().iRequest;
    }

    public static IContext getContext() {
        return ThreadLocalHolder.getContext();
    }

    public static IResponse getResponse() {
        return IContext.getContext().iResponse;
    }
}
