package com.zlmthy.router;

import com.zlmthy.action.HelloAction;
import com.zlmthy.router.entity.Router;

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
            router.put("/",r);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public RouterUtil() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Router router = RouterUtil.router.get("/");
        router.getMethod().invoke(router.getController());
    }

    public static String getRouter() throws NoSuchMethodException {

        return new HelloAction().hello();

    }
}
