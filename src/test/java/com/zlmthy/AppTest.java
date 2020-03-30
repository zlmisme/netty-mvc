package com.zlmthy;

import com.zlmthy.annotations.RequestMapper;
import com.zlmthy.annotations.XxComponent;
import com.zlmthy.annotations.XxServer;
import com.zlmthy.example.SsController;
import com.zlmthy.example.SsServer;
import com.zlmthy.ioc.ClassPathApplicationContext;
import com.zlmthy.router.entity.Router;
import com.zlmthy.server.MainServer;
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
    public void shouldAnswerWithTrue() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        SsServer ssServer = new SsServer();
        for (Annotation annotation : ssServer.getClass().getAnnotations()){
            System.out.println(annotation);
            XxServer xxServer = ((XxServer)annotation);
            for (Annotation a :xxServer.getClass().getAnnotations()){
                System.out.println(a);
            }
        }

    }


    @Test
    public void testLoadUtil() throws Exception {
        ClassPathApplicationContext context = ClassPathApplicationContext.getInstance();
        SsController ssController = (SsController) context.getBean("ssController");
        ssController.hello();
    }
}
