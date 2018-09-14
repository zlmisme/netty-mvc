package com.zlmthy;

import com.zlmthy.annotations.RequestMapper;
import com.zlmthy.router.entity.Router;
import com.zlmthy.utils.ClassUtil;
import io.netty.handler.codec.http.HttpMethod;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }


    @Test
    public void testLoadUtil(){
        List<Class<?>> allClassByPackageName = ClassUtil.getAllClassByPackageName("com.zlmthy.action");

        String controllerUrl = "";

        for (Class clazz : allClassByPackageName){
            Annotation annotation = clazz.getAnnotation(RequestMapper.class);
            RequestMapper requestMapper = (RequestMapper)annotation;

            controllerUrl = requestMapper.value();
            for (Method method : clazz.getMethods()){
                RequestMapper mapper = method.getAnnotation(RequestMapper.class);
                if (mapper!=null){
                    Router router = new Router();
                    router.setHttpMethod(HttpMethod.GET);
                    router.setPath(controllerUrl+mapper.value());
                    router.setMethod(method);
                    router.setController(clazz);
                    System.out.println(router.getHttpMethod());
                }
            }
        }
    }
}
