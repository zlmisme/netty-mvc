package com.zlmthy.router;

import com.zlmthy.ClassUtil;
import com.zlmthy.action.HelloAction;
import com.zlmthy.annotations.Controller;
import com.zlmthy.annotations.RequestMapper;
import com.zlmthy.router.entity.Router;
import io.netty.handler.codec.http.HttpMethod;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengliming
 * @ClassName RouterUtil
 * @Description TODO
 * @date 2018/9/5 16:30
 */
public class RouterUtil {

    private static Map<String,Router> routerMap = new ConcurrentHashMap<>();


    public static void initRouter(){
        try {
            new RouterUtil();
            System.out.println("路由表=》"+new JSONObject(routerMap));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private RouterUtil() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        // 初始化路由
        List<Class<?>> allClassByPackageName = ClassUtil.getAllClassByPackageName("com.zlmthy.action");

        String controllerUrl = "";

        for (Class clazz : allClassByPackageName){
            // 判断类是否注解controller
            Annotation controllerAnnotation = clazz.getAnnotation(Controller.class);
            Controller controller = (Controller)controllerAnnotation;
            if (controller!=null){
                // 如果此类存在controller 注解则进行如下操作
                // 1. 获取类级别的requestMapper注解 获得路径
                Annotation requestAnnotation = clazz.getAnnotation(RequestMapper.class);
                if (requestAnnotation!=null){
                    controllerUrl = ((RequestMapper)requestAnnotation).value();
                }

                for (Method method : clazz.getMethods()){
                    RequestMapper mapper = method.getAnnotation(RequestMapper.class);
                    if (mapper!=null){
                        String resultUrl = controllerUrl+mapper.value();
                        Router router = new Router();
                        router.setHttpMethod(HttpMethod.GET);
                        router.setPath(resultUrl);
                        router.setMethod(method);
                        router.setController(clazz);
                        routerMap.put(resultUrl,router);
                    }
                }
            }
        }
    }

    /**
     * 根据路由获取路由配置
     * @param uri
     * @return
     */
    public static Router getRouter(String uri) {
        return RouterUtil.routerMap.get(uri);
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
