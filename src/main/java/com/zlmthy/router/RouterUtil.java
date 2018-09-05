package com.zlmthy.router;

import com.zlmthy.action.HelloAction;
import com.zlmthy.annotations.RequestMapper;
import com.zlmthy.router.entity.Router;
import io.netty.handler.codec.http.HttpMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zengliming
 * @ClassName RouterUtil
 * @Description TODO
 * @date 2018/9/5 16:30
 */
public class RouterUtil {

    public static Map<String,Router> router = new HashMap<>();

    static {
        try {
            Router r = new Router();
            Class<HelloAction> aClass = HelloAction.class;
            r.setController(aClass);
            r.setMethod(aClass.getMethod("hello"));
            r.setPath("/");
            r.setHttpMethod(HttpMethod.POST);
            router.put("/",r);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public RouterUtil() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        // 初始化路由
    }

    /**
     * 根据路由获取路由配置
     * @param uri
     * @return
     */
    public static Router getRouter(String uri) {
        return RouterUtil.router.get(uri);
    }

    /**
     * 根据路由配置获取结果
     * @param router
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    public static Object getRouterResult(Router router) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        return router.getMethod().invoke(router.getController().newInstance());
    }
}
