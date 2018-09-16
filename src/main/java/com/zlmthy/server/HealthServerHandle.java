package com.zlmthy.server;

import com.alibaba.fastjson.JSON;
import com.zlmthy.router.RouterUtil;
import com.zlmthy.router.entity.Router;
import com.zlmthy.utils.log.LogType;
import com.zlmthy.utils.log.LogUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.util.AsciiString;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.rtsp.RtspResponseStatuses.OK;

/**
 * @author zengliming
 * @ClassName HealthServerHandle
 * @Description TODO
 * @date 2018/9/5 14:05
 */
public class HealthServerHandle extends ChannelInboundHandlerAdapter {

    private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
    private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");
    private static final AsciiString CONNECTION = new AsciiString("Connection");
    private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");

    private static HttpRequest httpRequest;

    private static HttpResponse httpResponse;

    private static LogUtil LOG = LogUtil.getLog(LogType.NETTY_HANDLE);
//    private static Logger LOG = LogManager.getLogger(LogType.NETTY_HANDLE);

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        if (msg instanceof FullHttpRequest) {
            //客户端的请求对象
            FullHttpRequest req = (FullHttpRequest) msg;
            //新建一个返回消息的Json对象
            Map<String,Object> responseJson = new HashMap<>();


            //获取客户端的URL
            String uri = req.uri();

            if (HttpMethod.GET.equals(req.method())){
                QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
                uri = decoder.path();
            }else if (HttpMethod.POST.equals(req.method())){
                HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(req);
            }


            LOG.info("路径=》{0}",uri);
            Router router = RouterUtil.getRouter(uri);
            LOG.info("路由{0}",JSON.toJSONString(router));
            boolean existUri = false;
            if (router != null){
                if (router.getHttpMethod() == HttpMethod.POST){

                    HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(req);
                    existUri = true;

                }else if (router.getHttpMethod() == HttpMethod.GET){

                    QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
                    LOG.info(decoder.path());
                    existUri = true;
                }else {
                    LOG.info("request uri not exist");
                    responseJson.put("error", "404 Not Find");
                }
            }else {
                LOG.info("request uri not exist");
                responseJson.put("error", "404 Not Find");
            }

            if (existUri){
                httpRequest = (HttpRequest)req;
                Object result = getRouterResult(router);
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
    private static Object getRouterResult(Router router) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        //得到方法中的所有参数信息
        Class<?>[] parameterClazz = router.getMethod().getParameterTypes();
        List<Object> listValue = new ArrayList<Object>();
        //循环参数类型
        for(int i=0; i<parameterClazz.length; i++){
            fillList(listValue, parameterClazz[i],"zlm");
        }
        LOG.info("param{0}",JSON.toJSONString(listValue));
        return router.getMethod().invoke(router.getController().newInstance(),listValue.toArray());
    }


    private static void fillList(List<Object> list, Class<?> parameter,Object value) {
        System.out.println(parameter.getTypeName());
        if("java.lang.String".equals(parameter.getTypeName())){
            list.add(value);
        }else if("java.lang.Character".equals(parameter.getTypeName())){
            char[] ch = ((String)value).toCharArray();
            list.add(ch[0]);
        }else if("char".equals(parameter.getTypeName())){
            char[] ch = ((String)value).toCharArray();
            list.add(ch[0]);
        }else if("java.lang.Double".equals(parameter.getTypeName())){
            list.add(Double.parseDouble((String) value));
        }else if("double".equals(parameter.getTypeName())){
            list.add(Double.parseDouble((String) value));
        }else if("java.lang.Integer".equals(parameter.getTypeName())){
            list.add(Integer.parseInt((String) value));
        }else if("int".equals(parameter.getTypeName())){
            list.add(Integer.parseInt((String) value));
        }else if("java.lang.Long".equals(parameter.getTypeName())){
            list.add(Long.parseLong((String) value));
        }else if("long".equals(parameter.getTypeName())){
            list.add(Long.parseLong((String) value));
        }else if("java.lang.Float".equals(parameter.getTypeName())){
            list.add(Float.parseFloat((String) value));
        }else if("float".equals(parameter.getTypeName())){
            list.add(Float.parseFloat((String) value));
        }else if("java.lang.Short".equals(parameter.getTypeName())){
            list.add(Short.parseShort((String) value));
        }else if("shrot".equals(parameter.getTypeName())){
            list.add(Short.parseShort((String) value));
        }else if("java.lang.Byte".equals(parameter.getTypeName())){
            list.add(Byte.parseByte((String) value));
        }else if("byte".equals(parameter.getTypeName())){
            list.add(Byte.parseByte((String) value));
        }else if("java.lang.Boolean".equals(parameter.getTypeName())){
            if("false".equals(value) || "0".equals(value)){
                list.add(false);
            }else if("true".equals(value) || "1".equals(value)){
                list.add(true);
            }
        }else if("boolean".equals(parameter.getTypeName())){
            if("false".equals(value) || "0".equals(value)){
                list.add(false);
            }else if("true".equals(value) || "1".equals(value)){
                list.add(true);
            }
        }else if ("io.netty.handler.codec.http.HttpRequest".equals(parameter.getTypeName())){
            list.add(httpRequest);
        }
    }


}
