package com.zlmthy.server;

import com.zlmthy.router.RouterUtil;
import com.zlmthy.router.entity.Router;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.util.AsciiString;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

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
            JSONObject responseJson = new JSONObject();

            //获取客户端的URL
            String uri = req.uri();
            Router router = RouterUtil.getRouter(uri);

            boolean existUri = false;
            if (router != null){
                if (router.getHttpMethod() == HttpMethod.POST){

                    HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(req);
                    existUri = true;

                }else if (router.getHttpMethod() == HttpMethod.GET){

                    QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
                    existUri = true;
                }else {
                    responseJson.put("error", "404 Not Find");
                }
            }else {
                responseJson.put("error", "404 Not Find");
            }

            if (existUri){
                Object result = RouterUtil.getRouterResult(router);
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
        System.out.println("keepAlive=>" + keepAlive);
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

}
