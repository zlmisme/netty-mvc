package com.zlmthy.handle;

import com.zlmthy.enums.RequestMethod;
import com.zlmthy.ioc.ClassPathApplicationContext;
import com.zlmthy.router.RouterUtil;
import com.zlmthy.router.entity.Router;
import com.zlmthy.utils.ParameterNameUtil;
import com.zlmthy.utils.log.LogType;
import com.zlmthy.utils.log.LogUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.util.AsciiString;
import lombok.extern.log4j.Log4j2;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.rtsp.RtspResponseStatuses.OK;

/**
 * @author zengliming
 * @ClassName HealthServerHandle
 * @Description TODO
 * @date 2018/9/5 14:05
 */
@Log4j2
public class HealthServerHandle extends ChannelInboundHandlerAdapter {

    private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
    private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");
    private static final AsciiString CONNECTION = new AsciiString("Connection");
    private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");

    private static HttpRequest httpRequest;

    private static HttpResponse httpResponse;

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof FullHttpRequest) {
            //客户端的请求对象
            FullHttpRequest req = (FullHttpRequest) msg;
            //新建一个返回消息的Json对象
            Map<String,Object> responseJson = new HashMap<>();


            //获取客户端的URL
            String uri = req.uri();

            System.out.println("请求:"+req.method().name());

            Map<String, String> params = new HashMap<>();

            if (HttpMethod.GET.equals(req.method())){
                QueryStringDecoder decoder = new QueryStringDecoder(req.uri());

                uri = decoder.path();
                Map<String, List<String>> temp = decoder.parameters();

                Iterator<String> iterator = temp.keySet().iterator();

                while (iterator.hasNext()){
                    String next = iterator.next();
                    params.put(next,temp.get(next).get(0));
                }
            }else if (HttpMethod.POST.equals(req.method())){
                HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(req);

            }


            Router router = RouterUtil.getRouter(uri);
            boolean existUri = false;
            if (router != null){
                for (RequestMethod method: router.getHttpMethods()){
                    if (method.getValue().equals(req.method().name())){
                        existUri = true;
                    }
                }
                if (existUri){
                    if (router.getHttpMethods()[0] == RequestMethod.POST){

                        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(req);
                        existUri = true;

                    }else if (router.getHttpMethods()[0] == RequestMethod.GET){

                        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
                        log.info(decoder.path());
                        existUri = true;
                    }else {
                        log.info("request uri not exist");
                        responseJson.put("error", "404 Not Find");
                    }
                }else {
                    log.info("request uri not exist");
                    responseJson.put("error", "404 Not Find");
                }
            }else {
                log.info("request uri not exist");
                responseJson.put("error", "404 Not Find");
            }

            if (existUri){
                httpRequest = (HttpRequest)req;
                Object result = getRouterResult(router,params);
                responseJson.put("success",result);
            }
            //向客户端发送结果
            responseJson(ctx, req, responseJson.toString());
        }
    }

    /**
     * 响应HTTP的请求
     *
     * @param ctx
     * @param req
     * @param jsonStr
     */
    private void responseJson(ChannelHandlerContext ctx, FullHttpRequest req, String jsonStr) {

        boolean keepAlive = HttpUtil.isKeepAlive(req);
        byte[] jsonByteByte = new byte[0];
        try {
            jsonByteByte = jsonStr.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(jsonByteByte));
        response.headers().set(CONTENT_TYPE, "text/json");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        if (!keepAlive) {
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 根据路由配置获取结果
     * @param router
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    private static Object getRouterResult(Router router,Map params) throws Exception {
        ClassPathApplicationContext context = ClassPathApplicationContext.getInstance();
        //得到方法中的所有参数信息
        Class<?>[] parameterClazz = router.getMethod().getParameterTypes();
        Parameter[] parameters = router.getMethod().getParameters();
        List<Object> listValue = new ArrayList<Object>();
        log.debug("路由信息为：{}", router);
        String[] parameterNameByAsm = ParameterNameUtil.getMethodParameterNameByAsm(context.getBean(router.getController()).getClass(), router.getMethod());

        if (parameterNameByAsm!=null){
            for (int i=0;i<parameterNameByAsm.length;i++){
                if (!parameterClazz[i].getTypeName().equals("HttpRequest")){
                    ParameterNameUtil.fillList(listValue, parameterClazz[i],params.get(parameterNameByAsm[i]));
                }else {
                    listValue.add(httpRequest);
                }
            }
        }

        return router.getMethod().invoke(context.getBean(router.getController()),listValue.toArray());
    }
}
