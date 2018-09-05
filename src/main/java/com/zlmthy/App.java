package com.zlmthy;

import com.zlmthy.action.HelloAction;
import com.zlmthy.router.RouterUtil;
import com.zlmthy.router.entity.Router;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Hello world!
 *
 */
public class App {

    public static void main( String[] args ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Router router = RouterUtil.router.get("/");
        System.out.println(new JSONObject(router));
        Class<?> controller = router.getController();
        Object invoke = router.getMethod().invoke(controller.newInstance());
        System.out.println(invoke);
    }
}
