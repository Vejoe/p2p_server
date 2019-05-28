package com.zhihao.p2p_server.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class UDPServer {
    private Bootstrap bootstrap;
    private EventLoopGroup myClientUdpGroup;
    private Channel channel;
    private ChannelFuture channelFuture;
    private int port = 7011;
    private UDPServer(){
        bootstrap = new Bootstrap();
        myClientUdpGroup = new NioEventLoopGroup();
        bootstrap.group(myClientUdpGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535))//加上这个，里面是最大接收、发送的长度
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new UDPServerHandler());
    }
    private static class SingletionMyClientUdp{
        static final UDPServer instance = new UDPServer();
    }

    public static UDPServer getInstance(){
        return UDPServer.SingletionMyClientUdp.instance;
    }

    public void start(){

        try {
            channelFuture = bootstrap .bind("localhost", port).sync();
            channel = channelFuture.channel();
            System.out.println("UDPServer启动！");
            channel.closeFuture().await();
        }catch (InterruptedException in){
            in.printStackTrace();
        } finally {
            myClientUdpGroup.shutdownGracefully();
        }

    }

    public void close(){
        if(channelFuture != null){
            channelFuture.channel().closeFuture();
            myClientUdpGroup.shutdownGracefully();
            System.out.println("netty Udp关闭成功！");
        }
    }

    public Channel getChannel() {
        return channel;
    }
}
