package com.zhihao.p2p_server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

public class UDPServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        System.out.println("UDP接受到消息");
        ByteBuf buf = (ByteBuf) msg.copy().content();
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);

        String[] str = new String(req, "UTF-8").split(" ");
        InetSocketAddress sender = msg.sender();
        UserUDPIPMap.put(str[1],sender);

        for( int i = 0;i<str.length ; i++){
            System.out.println("str"+i+":"+str[i]);
        }
        System.out.println(sender.getAddress()+"---"+sender.getPort());
        //str[0]消息类型，str[1]发送者,str[2]:接收者
        if(str[0].equals("ServerTop2p")){
            //p2p打洞。
            if(UserUDPIPMap.get(str[2]) != null){
                String remot = "ServerTop2p "+str[1]+" " + (sender.getAddress().toString().replace("/", ""))+" "+sender.getPort();
                ctx.writeAndFlush(new DatagramPacket(
                        Unpooled.copiedBuffer(remot.getBytes()) ,UserUDPIPMap.get(str[2])));
                remot = "ServerTop2p "+str[2]+" " + (UserUDPIPMap.get(str[2]).getAddress().toString().replace("/", ""))+" "+sender.getPort();
                ctx.writeAndFlush(new DatagramPacket(
                        Unpooled.copiedBuffer(remot.getBytes()) ,UserUDPIPMap.get(str[1])));
            }
        }

    }
}
