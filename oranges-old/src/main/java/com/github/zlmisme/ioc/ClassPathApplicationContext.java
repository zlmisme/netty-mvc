package com.github.zlmisme.ioc;

import com.github.zlmisme.annotations.*;
import com.github.zlmisme.config.LoadConfig;
import com.github.zlmisme.router.RouterUtil;
import com.github.zlmisme.router.entity.Router;
import com.github.zlmisme.utils.ClassUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ioc容器操作类
 *
 * @author zengliming
 * @date 2019-04-04
 */
@Log4j2
public final class ClassPathApplicationContext {


    private static final ConcurrentHashMap<String, Object> BEANS = new ConcurrentHashMap<>();

    private final List<Class<?>> classes;

    public static ClassPathApplicationContext getInstance() {
        return Singleton.CLASS_PATH_APPLICATION_CONTEXT;
    }

    private static class Singleton {
        private static final ClassPathApplicationContext CLASS_PATH_APPLICATION_CONTEXT = new ClassPathApplicationContext();
    }


    private ClassPathApplicationContext() {
        classes = ClassUtil.getAllClassByPackageNameAndAnnotation(LoadConfig.getBasePackage(), null);
        initBeans();
        try {
            initAttribute();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private void initComponents() {
        for (Class<?> clazz : classes) {
            XxComponent annotation = clazz.getAnnotation(XxComponent.class);
            if (annotation != null) {
                String beanId = annotation.value();
                if (StringUtils.isEmpty(beanId)) {
                    // 获取当前类名
                    beanId = toLowerCaseFirstOne(clazz.getSimpleName());
                }
                addBean(beanId, clazz);
            }
        }

    }

    private void initServers() {
        for (Class<?> clazz : classes) {
            XxServer annotation = clazz.getAnnotation(XxServer.class);
            if (annotation != null) {
                String beanId = annotation.value();
                if (StringUtils.isEmpty(beanId)) {
                    // 获取当前类名
                    beanId = toLowerCaseFirstOne(clazz.getSimpleName());
                }
                addBean(beanId, clazz);
            }
        }
    }

    private void initController() {
        for (Class<?> clazz : classes) {
            Annotation annotation = clazz.getAnnotation(XxController.class);
            if (annotation != null) {
                // 获取当前类名
                String beanId = toLowerCaseFirstOne(clazz.getSimpleName());
                Method[] methods = clazz.getMethods();
                String controllerUrl = "";
                RequestMapper requestAnnotation = clazz.getAnnotation(RequestMapper.class);
                if (requestAnnotation != null) {
                    controllerUrl = requestAnnotation.value();
                }
                for (Method method : methods) {
                    RequestMapper mapper = method.getAnnotation(RequestMapper.class);
                    if (mapper != null) {
                        String resultUrl = controllerUrl + mapper.value();
                        Router router = new Router();
                        router.setHttpMethods(mapper.method());
                        router.setPath(resultUrl);
                        router.setMethod(method);
                        router.setController(toLowerCaseFirstOne(clazz.getSimpleName()));
                        log.info("添加路由{}", router);
                        RouterUtil.addRouter(resultUrl, router);
                    }
                }
                addBean(beanId, clazz);
            }
        }
    }

    private void initBeans() {
        long begin = System.currentTimeMillis();
        log.info("初始化beans begin");
        initComponents();
        initServers();
        initController();
        log.info("初始化beans end，总计初始化{}个bean，花费{}ms", BEANS.size(), System.currentTimeMillis() - begin);
        BEANS.forEach((k, v) -> log.debug("bean {}, object{}", k, v));
    }

    private void addBean(String beanName, Class clazz) {
        Object newInstance;
        try {
            newInstance = clazz.newInstance();
            if (BEANS.containsKey(beanName)) {
                throw new RuntimeException("bean" + clazz.getPackage() + clazz.getName() + "已存在");
            }
            BEANS.put(beanName, newInstance);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("反射生成对象失败" + e.getMessage());
        }
    }

    public Object getBean(String beanId) {
        if (beanId == null || StringUtils.isEmpty(beanId)) {
            throw new RuntimeException("beanId不能为空");
        }
        Object clazz = BEANS.get(beanId);
        if (clazz == null) {
            throw new RuntimeException("该包下没有BeanId为" + beanId + "的类");
        }
        return clazz;
    }

    private String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        }
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    private void initAttribute() throws Exception {
        for (String key : BEANS.keySet()) {
            log.info("对bean：{}进行依赖注入", key);
            attributeAssign(BEANS.get(key));
        }
    }

    private void attributeAssign(Object object) throws Exception {
        Field[] fields = object.getClass().getDeclaredFields();
        log.debug("成员变量数量{}", fields.length);
        for (Field field : fields) {
            XxAutowired xxAutowired = field.getAnnotation(XxAutowired.class);
            if (xxAutowired != null) {
                log.debug("成员变量{}进行注入", field.getName());
                Object bean = getBean(field.getName());
                log.debug("成员变量{}进行注入 bean {}", field.getName(), bean);
                field.setAccessible(true);
                field.set(object, bean);
            }

        }

    }
}
