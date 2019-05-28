package com.zhihao.p2p_server.netty;

import com.zhihao.p2p_server.domain.Users;
import com.zhihao.p2p_server.protobuf.Message;
import com.zhihao.p2p_server.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.zhihao.p2p_server.protobuf.Message.AcceptOrRefuseFriends.AcceptOrRefuseType.Accept;
import static com.zhihao.p2p_server.protobuf.Message.CameraOrVoiceRequest.CameraOrVoiceType.Voice;
import static com.zhihao.p2p_server.protobuf.Message.CameraOrVoiceResponse.AcceptOrNo.Refuse;
import static com.zhihao.p2p_server.protobuf.Message.MyMessage.ActionType.*;

@Component
public class TCPServerHandler extends SimpleChannelInboundHandler<Message.MyMessage> {

    // 用于记录和管理所有客户端的channle
    public static ChannelGroup users =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Autowired
    private UserService userService;

    public static TCPServerHandler p2PServerHandler ;

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public TCPServerHandler(){}

    @PostConstruct
    public void init(){
        p2PServerHandler = this;
        p2PServerHandler.userService = this.userService;
    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message.MyMessage msg)  {
        System.out.println(msg.getLoginMessage().getUsername()+"!!!!!!!!!!!!!!!!");
        System.out.println(msg);
        //用户上线先验证是否存在该用户且记录该用户和channel，并转发未处理的信息给该用户。
        if(msg.getActionType() == Message.MyMessage.ActionType.LoginAction){
            Users users = p2PServerHandler.userService.getCorrentUser(msg.getLoginMessage().getUsername(),msg.getLoginMessage().getPassword());
            if(users!=null){
                UserTCPChannelMap.put(users.getUsername(),ctx.channel());
                System.out.println("当前在线人数："+ UserTCPChannelMap.getCount());
                //发送未处理的新好友信息。
                Message.MyMessage myMessage = p2PServerHandler.userService.getFriendsRequestMessage(users.getUsername());
                if (myMessage != null) {
                    System.out.println("开始发送未处理的新好友信息！");
                    ctx.channel().writeAndFlush(myMessage);
                }
                //发送好友信息
                myMessage = p2PServerHandler.userService.getMyFriendsList(users.getUsername());
                System.out.println("发送好友消息："+myMessage);
                if(myMessage != null){
                    System.out.println("开始发送好友信息！");
                    ctx.channel().writeAndFlush(myMessage);
                }
                //发送未接受到的聊天消息。
                System.out.println("上线用户："+users.getUsername());
                myMessage = p2PServerHandler.userService.forwardingMessage(users.getUsername());
                if(myMessage != null){
                    System.out.println("开始发送未接受到的聊天消息！:/n"+myMessage);
                    ctx.channel().writeAndFlush(myMessage);
                }
                //发送该用户所在的群组
                myMessage = p2PServerHandler.userService.forwardingGroupMessage(users.getUsername());
                if(myMessage != null){
                    System.out.println("开始发送群组信息！");
                    ctx.channel().writeAndFlush(myMessage);
                }
                //发送群组的消息
                p2PServerHandler.userService.handleforwardingGroupMessageOnline(users.getUsername());
            }
        }else if (msg.getActionType() == CloseTCPConnection){
            if(UserTCPChannelMap.get(msg.getCloseTCPConnection().getCloseUserName())!=null ){
                System.out.println("用户tcp"+msg.getCloseTCPConnection().getCloseUserName()+"退出!");
                UserTCPChannelMap.remover(msg.getCloseTCPConnection().getCloseUserName());
            }
            if(UserUDPIPMap.get(msg.getCloseTCPConnection().getCloseUserName())!=null){
                System.out.println("用户udp"+msg.getCloseTCPConnection().getCloseUserName()+"退出!");
                UserUDPIPMap.remove(msg.getCloseTCPConnection().getCloseUserName());
            }
        }else if(msg.getActionType() == Message.MyMessage.ActionType.QueryFriends){
            System.out.println(msg.getRequest().getQueryFriendsRequest().getUserName()+",,22222222222222222??????");
            Users users =  p2PServerHandler.userService.getUsers(msg.getRequest().getQueryFriendsRequest().getUserName());
            if(users == null){
                return ;
            }
            if(users.getFace_image()==null){
                users.setFace_image("");
            }
            if(users.getFace_image_big()==null){
                users.setFace_image_big("");
            }
            Message.MyMessage myMessage = Message.MyMessage.newBuilder()
                    .setActionType(QueryFriends)
                    .setResponse(Message.Response.newBuilder().setQueryFriendsResponse(
                            Message.Query_Friends_Response.newBuilder().
                                    setFaceImage(users.getFace_image()).
                                    setNickname(users.getNickname()).
                                    setUserName(users.getUsername()).build()
                    ).build()).build();
            ctx.channel().writeAndFlush(myMessage);
        }else if(msg.getActionType() == MakeFriendsAction){
            p2PServerHandler.userService.insertFriends_Request(msg.getRequest().getMakeFriendRequest().getSendUserName(),msg.getRequest().getMakeFriendRequest().getAcceptUserName(),msg.getRequest().getMakeFriendRequest().getRemarks());
            //判断用户是否在线，在线则推送过去。
            if(UserTCPChannelMap.get(msg.getRequest().getMakeFriendRequest().getAcceptUserName())!=null && !UserTCPChannelMap.get(msg.getRequest().getMakeFriendRequest().getAcceptUserName()).equals("")){
                Message.MyMessage myMessage = p2PServerHandler.userService.getFriendsRequestMessage(msg.getRequest().getMakeFriendRequest().getAcceptUserName());
                if (myMessage != null) {
                    UserTCPChannelMap.get(msg.getRequest().getMakeFriendRequest().getAcceptUserName()).writeAndFlush(myMessage);
                }
            }
        }else if(msg.getActionType() == AcceptOrRefuseFriendsAction){
            //更新客户对自己新好友信息的处理，如果是接受和之前发送请求好友的客户在线，则推送信息给请求的客户
            p2PServerHandler.userService.updateFriendsRequestMessage(msg);
            if(UserTCPChannelMap.get(msg.getRequest().getAcceptOrRefuseFriends().getSendUserName())!=null
                    && !UserTCPChannelMap.get(msg.getRequest().getAcceptOrRefuseFriends().getSendUserName()).equals("")
                    && msg.getRequest().getAcceptOrRefuseFriends().getAcceptOrRefuseType() == Accept){
                Message.MyMessage myMessage = p2PServerHandler.userService.getMyFriendsList(msg.getRequest().getAcceptOrRefuseFriends().getAcceptUserName());
                if (myMessage != null) {
                    UserTCPChannelMap.get(msg.getRequest().getAcceptOrRefuseFriends().getAcceptUserName()).writeAndFlush(myMessage);
                }
                myMessage = p2PServerHandler.userService.getMyFriendsList(msg.getRequest().getAcceptOrRefuseFriends().getSendUserName());
                if (myMessage != null) {
                    UserTCPChannelMap.get(msg.getRequest().getAcceptOrRefuseFriends().getSendUserName()).writeAndFlush(myMessage);
                }
            }
        }else if(msg.getActionType() == ChatMessageSendOrAccept){
            //服务器接受客户的聊天信息并转发。
            System.out.println(msg);
            //群组消息处理
            if(msg.getRequest().getSendChatMessageRequest().getAcceptUserId().length()>20){
                p2PServerHandler.userService.handleGroupMessageSendOrAccept(msg);
            }else{//个人用户消息处理
                if(UserTCPChannelMap.get(msg.getRequest().getSendChatMessageRequest().getAcceptUserId()) != null
                        && !UserTCPChannelMap.get(msg.getRequest().getSendChatMessageRequest().getAcceptUserId()).equals("")){
                    System.out.println("在线转发!");
                    Message.MyMessage myMessage = p2PServerHandler.userService.forwardingAndUpdateDatabase(msg);
                    UserTCPChannelMap.get(msg.getRequest().getSendChatMessageRequest().getAcceptUserId()).writeAndFlush(myMessage);
                }else{
                    p2PServerHandler.userService.updateChatMessageDatabase(msg,0);
                }
            }

        }else if(msg.getActionType() == CreateGroupAction){
            Message.MyMessage myMessage = p2PServerHandler.userService.updateGroupsMessage(msg);
            for(int i=0;i<msg.getRequest().getCreateGroupRequest().getUserNameCount();i++){
                if(UserTCPChannelMap.get(msg.getRequest().getCreateGroupRequest().getUserName(i)) != null
                        && !UserTCPChannelMap.get(msg.getRequest().getCreateGroupRequest().getUserName(i)).equals("")){
                    UserTCPChannelMap.get(msg.getRequest().getCreateGroupRequest().getUserName(i)).writeAndFlush(myMessage);
                }
            }
        }else if(msg.getActionType() == ChangeGroupAction){
            Message.MyMessage myMessage =  p2PServerHandler.userService.handleChangeGroupAction(msg);
            System.out.println("修改后返回的group信息："+myMessage);
            for(int i=0;i<myMessage.getResponse().getCreateGroupResponse().getAllGroup(0).getGroupUserCount();i++){
                if(UserTCPChannelMap.get(myMessage.getResponse().getCreateGroupResponse().getAllGroup(0).getGroupUser(i).getUsername()) != null
                        && !UserTCPChannelMap.get(myMessage.getResponse().getCreateGroupResponse().getAllGroup(0).getGroupUser(i).getUsername()).equals("")){
                    System.out.println("进来了？");
                    UserTCPChannelMap.get(myMessage.getResponse().getCreateGroupResponse().getAllGroup(0).getGroupUser(i).getUsername()).writeAndFlush(myMessage);
                }
            }
        }else if (msg.getActionType() == QuitGroupAction){
            System.out.println("进来？" );
            Message.MyMessage myMessage =  p2PServerHandler.userService.handleQuitGroupAction(msg);
            System.out.println(myMessage);

            if(myMessage == null){
                return ;
            }
            for(int i=0;i<myMessage.getResponse().getCreateGroupResponse().getAllGroup(0).getGroupUserCount();i++){
                if(UserTCPChannelMap.get(myMessage.getResponse().getCreateGroupResponse().getAllGroup(0).getGroupUser(i).getUsername()) != null
                        && !UserTCPChannelMap.get(myMessage.getResponse().getCreateGroupResponse().getAllGroup(0).getGroupUser(i).getUsername()).equals("")){

                    UserTCPChannelMap.get(myMessage.getResponse().getCreateGroupResponse().getAllGroup(0).getGroupUser(i).getUsername()).writeAndFlush(myMessage);
                }
            }
        }else if(msg.getActionType() == CameraOrVoiceRequestAction){
            //在线转发请求，否则返回用户不在线的拒绝
            if(UserTCPChannelMap.get(msg.getRequest().getCameraOrVoiceRequest().getAccepter()) != null
                    && !UserTCPChannelMap.get(msg.getRequest().getCameraOrVoiceRequest().getAccepter()).equals("")){
                UserTCPChannelMap.get(msg.getRequest().getCameraOrVoiceRequest().getAccepter()).writeAndFlush(msg);
                System.out.println("发送给："+msg.getRequest().getCameraOrVoiceRequest().getAccepter());
            }else {
                Message.CameraOrVoiceResponse.Builder cameraOrVoiceResponse = Message.CameraOrVoiceResponse.newBuilder();
                cameraOrVoiceResponse.setAccepter(msg.getRequest().getCameraOrVoiceRequest().getAccepter());
                cameraOrVoiceResponse.setSender(msg.getRequest().getCameraOrVoiceRequest().getSender());
                cameraOrVoiceResponse.setRefuseRemark("用户不在线！");
                cameraOrVoiceResponse.setAcceptOrNo(Refuse);
                cameraOrVoiceResponse.setCameraOrVoiceType(Message.CameraOrVoiceResponse.CameraOrVoiceType.Camera);
                if(msg.getRequest().getCameraOrVoiceRequest().getCameraOrVoiceType()==Voice){
                    cameraOrVoiceResponse.setCameraOrVoiceType(Message.CameraOrVoiceResponse.CameraOrVoiceType.Voice);
                }
                Message.MyMessage myMessage =  Message.MyMessage.newBuilder().setActionType(CameraOrVoiceResponseAction)
                        .setResponse(Message.Response.newBuilder().setCameraOrVoiceResponse(
                                cameraOrVoiceResponse.build()
                        ).build()).build();
                UserTCPChannelMap.get(msg.getRequest().getCameraOrVoiceRequest().getSender()).writeAndFlush(myMessage);
            }
        }else if(msg.getActionType() == CameraOrVoiceResponseAction){
            if(UserTCPChannelMap.get(msg.getResponse().getCameraOrVoiceResponse().getAccepter()) != null
                    && !UserTCPChannelMap.get(msg.getResponse().getCameraOrVoiceResponse().getAccepter()).equals("")){
                UserTCPChannelMap.get(msg.getResponse().getCameraOrVoiceResponse().getAccepter()).writeAndFlush(msg);
            }
        }else if(msg.getActionType() == CloseCameraOrVoiceAction){
            if(UserTCPChannelMap.get(msg.getCloseCameraOrVoice().getAcceptUserName()) != null
                    && !UserTCPChannelMap.get(msg.getCloseCameraOrVoice().getAcceptUserName()).equals("")){
                UserTCPChannelMap.get(msg.getCloseCameraOrVoice().getAcceptUserName()).writeAndFlush(msg);
            }
        }else if(msg.getActionType() == DeleteFriendAction){
            p2PServerHandler.userService.handleDeleteFriendAction(msg);
            if(UserTCPChannelMap.get(msg.getRequest().getDeleteFriendRequest().getDeletedUserName()) != null
                    && !UserTCPChannelMap.get(msg.getRequest().getDeleteFriendRequest().getDeletedUserName()).equals("")){
                UserTCPChannelMap.get(msg.getRequest().getDeleteFriendRequest().getDeletedUserName()).writeAndFlush(msg);
            }
        }


    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("添加成功！");
        channelGroup.add(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        System.out.println("客户端出错终端，调用exceptionCaught！");
    }
}
