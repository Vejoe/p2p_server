package com.zhihao.p2p_server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TCPServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;
    private ServerBootstrap serverBootstrap;

    private static class SingletionP2PServer{
        static final TCPServer instance = new TCPServer();
    }

    public static TCPServer getInstance(){
        return SingletionP2PServer.instance;
    }

    private TCPServer(){
        this.bossGroup = new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        this.serverBootstrap = new ServerBootstrap();
        this.serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new TCPServerInitializer());
    }
    /**
     * 用作netty服务端启动方法
     */
    public void start(){
        System.out.println("hello  hi");
        this.channelFuture=serverBootstrap.bind(8050);
        System.out.println("netty启动成功");
    }
}
