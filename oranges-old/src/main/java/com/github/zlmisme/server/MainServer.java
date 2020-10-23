package com.github.zlmisme.server;

import com.github.zlmisme.ioc.ClassPathApplicationContext;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author zengliming
 * @ClassName MainServer
 * @Description TODO
 * @date 2018/9/5 13:39
 */
public class MainServer {


    public static void main(String[] args) throws Exception {
        ClassPathApplicationContext context = ClassPathApplicationContext.getInstance();

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            // 配置最大连接数
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            //
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ServerInitializer());

            ChannelFuture future = bootstrap.bind(8009).sync();
            System.out.println("Open your web browser and navigate to " + "http://127.0.0.1:8009/");
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }
}
