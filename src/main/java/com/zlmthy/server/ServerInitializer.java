package com.zlmthy.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;

/**
 * @author zengliming
 * @ClassName ServerInitializer
 * @Description TODO
 * @date 2018/9/5 13:53
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline p = channel.pipeline();
        /*HTTP 服务的解码器*/
        p.addLast(new HttpServerCodec());
        /*HTTP 消息的合并处理*/
        p.addLast(new HttpObjectAggregator(2048));
        /* http-request解码器
         * http服务器端对request解码
         */
        p.addLast("decoder", new HttpRequestDecoder());
        /*
         * http-response解码器
         * http服务器端对response编码
         */
        p.addLast(new HttpResponseDecoder());
        p.addLast("aggregator", new HttpObjectAggregator(1048576));
        /*
         * 压缩
         */
        p.addLast("deflater", new HttpContentCompressor());
        /*自己写的服务器逻辑处理*/
        p.addLast(new HealthServerHandle());
    }
}
