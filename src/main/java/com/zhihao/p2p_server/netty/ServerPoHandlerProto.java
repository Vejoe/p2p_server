package com.zhihao.p2p_server.netty;

import com.zhihao.p2p_server.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

import static com.zhihao.p2p_server.protobuf.Message.MyMessage.ActionType.IdleStateAction;
import static io.netty.handler.timeout.IdleState.READER_IDLE;

public class ServerPoHandlerProto extends ChannelInboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state() == READER_IDLE) {
                System.out.println("client " + ctx.channel().remoteAddress() + " is inactive to close");
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
