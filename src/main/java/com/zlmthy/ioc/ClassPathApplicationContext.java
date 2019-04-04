package com.zlmthy.ioc;

import com.zlmthy.annotations.XxComponent;
import com.zlmthy.annotations.XxAutowired;
import com.zlmthy.annotations.XxController;
import com.zlmthy.annotations.XxServer;
import com.zlmthy.thread.XxThreadPoolExecutor;
import com.zlmthy.utils.ClassUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * ioc容器操作类
 *
 * @author zengliming
 * @date 2019-04-04
 */
@Log4j2
public class ClassPathApplicationContext {


    private ConcurrentHashMap<String, Object> beans;

    private List<Class<?>> classes;

    public ClassPathApplicationContext(String basePackageName) throws Exception {
        beans = new ConcurrentHashMap<>();
        classes = ClassUtil.getAllClassByPackageNameAndAnnotation(basePackageName, null);
        initBeans();
        initAttribute();
    }

    private void initComponents() {
        for (Class<?> clazz : classes) {
            Annotation annotation = clazz.getAnnotation(XxComponent.class);
            if (annotation != null) {
                String beanId = ((XxComponent) annotation).value();
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
            Annotation annotation = clazz.getAnnotation(XxServer.class);
            if (annotation != null) {
                String beanId = ((XxServer) annotation).value();
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
        log.info("初始化beans end，总计初始化{}个bean，花费{}ms", beans.size(), System.currentTimeMillis() - begin);
    }

    private void addBean(String beanName, Class clazz) {
        Object newInstance = null;
        try {
            newInstance = clazz.newInstance();
            if (beans.containsKey(beanName)) {
                throw new RuntimeException("bean" + clazz.getPackage() + clazz.getName() + "已存在");
            }
            beans.put(beanName, newInstance);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("反射生成对象失败" + e.getMessage());
        }
    }

    public Object getBean(String beanId) throws Exception {
        if (beanId == null || StringUtils.isEmpty(beanId)) {
            throw new RuntimeException("beanId不能为空");
        }
        Object clazz = beans.get(beanId);
        if (clazz == null) {
            throw new RuntimeException("该包下没有BeanId为" + beanId + "的类");
        }
        return clazz;
    }

    private String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        }
        return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    private void initAttribute() throws Exception {
        for (String key : beans.keySet()) {
            log.info("对bean：{}进行依赖注入", key);
            attributeAssign(beans.get(key));
        }
    }

    private void attributeAssign(Object object) throws Exception {
        Field[] fields = object.getClass().getDeclaredFields();
        log.debug("成员变量数量{}", fields.length);
        for (Field field : fields) {
            log.debug("对成员变量{}进行注入", field.getName());
            XxAutowired xxAutowired = field.getAnnotation(XxAutowired.class);
            if (xxAutowired != null) {
                log.debug("成员变量{}进行注入");
                Object bean = getBean(field.getName());
                field.setAccessible(true);
                field.set(object, bean);
            }

        }

    }
}
