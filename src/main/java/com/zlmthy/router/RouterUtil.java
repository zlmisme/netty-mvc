package com.zlmthy.router;

import com.zlmthy.annotations.XxController;
import com.zlmthy.annotations.RequestMapper;
import com.zlmthy.config.LoadConfig;
import com.zlmthy.router.entity.Router;
import com.zlmthy.utils.ClassUtil;
import com.zlmthy.utils.log.LogType;
import com.zlmthy.utils.log.LogUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    /**
     * 存放路由
     */
    private static Map<String,Router> routerMap = new ConcurrentHashMap<>();

    private static LogUtil log = LogUtil.getLog(LogType.FRAMEWORK);


    /**
     * 初始化路由表
     */
    public static void initRouter(String basePackage){
        try {
            new RouterUtil(basePackage);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            log.error("初始化路由异常, 异常信息{0}",e);
        }
    }


    /**
     * 扫描所有包下的class
     * @return 所有包下的class
     */
    private List<Class<?>> scanPackageClass(){
        // 初始化路由
       return ClassUtil.getAllClassByPackageName("");
    }

    /**
     * 扫描特定包下的class
     * @param packageName 包路径
     * @return 特定包下的class
     */
    private List<Class<?>> scanPackageClass(String packageName){
        if ("".equals(packageName)){
            return scanPackageClass();
        }
        return ClassUtil.getAllClassByPackageName(packageName);
    }

    /**
     * 路由构造方法
     * @throws NoSuchMethodException 没有找到
     * @throws InvocationTargetException 当被调用的方法的内部抛出了异常而没有被捕获时，将由此异常接收
     * @throws IllegalAccessException 非法访问异常
     * @throws InstantiationException newInstance()实例化异常
     */
    private RouterUtil(String basePackage) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        // 初始化路由
        List<Class<?>> allClassByPackageName = scanPackageClass(basePackage);
        String controllerUrl = "";
        for (Class clazz : allClassByPackageName){
            // 判断类是否注解controller
            Annotation controllerAnnotation = clazz.getAnnotation(XxController.class);
            XxController controller = (XxController)controllerAnnotation;
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
                        router.setHttpMethods(mapper.method());
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
     * @param uri 请求路径
     * @return 路由配置
     */
    public static Router getRouter(String uri) {
        return RouterUtil.routerMap.get(uri);
    }

    /**
     * 根据路由配置获取结果
     * @param router 路由信息
     * @return 路由结果
     * @throws IllegalAccessException 非法访问异常
     * @throws InstantiationException newInstance()实例化异常
     * @throws InvocationTargetException 当被调用的方法的内部抛出了异常而没有被捕获时，将由此异常接收
     */
    public static Object getRouterResult(Router router) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        return router.getMethod().invoke(router.getController().newInstance());
    }
}
