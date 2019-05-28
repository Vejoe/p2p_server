package com.zhihao.p2p_server.service.serviceImpl;

import com.zhihao.p2p_server.Utils.ImageUtils;
import com.zhihao.p2p_server.Utils.UUIDUtils;
import com.zhihao.p2p_server.domain.*;
import com.zhihao.p2p_server.mapper.*;
import com.zhihao.p2p_server.netty.TCPServer;
import com.zhihao.p2p_server.netty.UserTCPChannelMap;
import com.zhihao.p2p_server.protobuf.Message;
import com.zhihao.p2p_server.service.UserService;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.zhihao.p2p_server.protobuf.Message.MyMessage.ActionType.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    Chat_MessageMapper chat_messageMapper;
    @Autowired
    UsersMapper usersMapper;
    @Autowired
    Friends_RequestMapper friends_requestMapper;
    @Autowired
    My_FriendsMapper my_friendsMapper;
    @Autowired
    GroupsMapper groupsMapper;
    @Autowired
    Group_UserMapper group_UserMapper;
    @Autowired
    GroupMessageTempMapper groupMessageTempMapper;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public int registerUser(Users users){
        if(usersMapper.getUserCountByUserName(users.getUsername())!=0){
            return 0;
        }
        try{
            users.setFace_image(ImageUtils.BigImageToSmallImage(users.getFace_image_big()));
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return usersMapper.insert(users);
    }

    public  int loginUser(String userName, String passWord){
        if(usersMapper.getUserCountByUserNameAndPassWord(userName,passWord)==0){
            return 0;
        }
        return 1;
    }

    /**
     * 查找用户
     * @param userName
     * @return
     */
    @Override
    public Users getUsers(String userName) {
       return usersMapper.getUsersByUserName(userName);
    }

    /**
     * 登陆时候返回一个当前登录用户对象
     * @param userName
     * @return
     */
    public Users getCorrentUser(String userName,String passWord){
        if(usersMapper.getUserCountByUserNameAndPassWord(userName, passWord)!=0){
            return usersMapper.getUsersByUserName(userName);
        }
        return null;
    }

    /**
     * 用户进行添加好友操作
     * @param send_user_id
     * @param accept_user_id
     * @param remarks
     */
    @Override
    public void insertFriends_Request(String send_user_id, String accept_user_id, String remarks) {

        Friends_Request friends_request = new Friends_Request();
        friends_request.setId(UUIDUtils.create().toString().replaceAll("-",""));
        friends_request.setSend_user_id(send_user_id);
        friends_request.setAccept_user_id(accept_user_id);
        friends_request.setRemarks(remarks);
        friends_request.setAccept_status(0);
        friends_request.setRequest_date_time(df.format(new Date()));
        if(friends_requestMapper.getFriends_RequestMatch(send_user_id,accept_user_id)!=0){
            friends_requestMapper.updateFriends_Request(friends_request);
        }else {
            friends_requestMapper.insertFriends_Request(friends_request);
        }

    }

    /**
     * 获取好友列表并封装好，用户上线时候推送过去和有人添加的时候推送
     * @param accept_user_id
     * @return
     */
    @Override
    public Message.MyMessage getFriendsRequestMessage(String accept_user_id) {
        if(friends_requestMapper.getCountByAccept_user_id(accept_user_id)!=0){
            List<Friends_Request> newFriendsList = friends_requestMapper.getFriends_Request(accept_user_id);
            if(!(newFriendsList!=null && newFriendsList.size()>0)){
                return null;
            }
            Message.Make_Friend_Response.Builder make_friend_response = Message.Make_Friend_Response.newBuilder();
            for(int i = 0; i<newFriendsList.size();i++){
                make_friend_response.addSendFriendRequestUserMessage(
                        Message.Make_Friend_Response.Send_FriendRequest_User_Message.newBuilder()
                                .setSendUserName(newFriendsList.get(i).getSend_user_message().getUsername())
                                .setFaceImage(newFriendsList.get(i).getSend_user_message().getFace_image())
                                .setNickname(newFriendsList.get(i).getSend_user_message().getNickname())
                                .setRemarks(newFriendsList.get(i).getRemarks())
                                .setRequestDateTime(newFriendsList.get(i).getRequest_date_time()).build()
                );
            }
            Message.MyMessage myMessage =  Message.MyMessage.newBuilder()
                    .setActionType(MakeFriendsAction).setResponse(
                            Message.Response.newBuilder().setMakeFriendResponse(
                                    make_friend_response.build()
                            ).build()
                    ).build();
            return myMessage;
        }
        return null;
    }

    /**
     * Author:weizhihao
     * 把用户对新好友拒绝或者接受的信息进行处理，如果接受则添加好友表。
     * @param myMessage
     */
    public void updateFriendsRequestMessage(Message.MyMessage myMessage){
        System.out.println("updateFriendsRequestMessage!!!!!!");
        Friends_Request friends_request =  new Friends_Request();
        friends_request.setAccept_user_id(myMessage.getRequest().getAcceptOrRefuseFriends().getAcceptUserName());
        friends_request.setSend_user_id(myMessage.getRequest().getAcceptOrRefuseFriends().getSendUserName());
        friends_request.setRemarks(myMessage.getRequest().getAcceptOrRefuseFriends().getRemarks());
        friends_request.setRequest_date_time(myMessage.getRequest().getAcceptOrRefuseFriends().getRequestDateTime());
        if(Message.AcceptOrRefuseFriends.AcceptOrRefuseType.Accept == myMessage.getRequest().getAcceptOrRefuseFriends().getAcceptOrRefuseType()){
            friends_request.setAccept_status(1);
            System.out.println("Accept!");
            my_friendsMapper.insertMy_Friends(UUIDUtils.create().toString().replaceAll("-",""),friends_request.getSend_user_id(),friends_request.getAccept_user_id());
            my_friendsMapper.insertMy_Friends(UUIDUtils.create().toString().replaceAll("-",""),friends_request.getAccept_user_id(),friends_request.getSend_user_id());
        }else{
            friends_request.setAccept_status(-1);
            System.out.println("Refuse!");
        }
        friends_requestMapper.updateFriends_Request(friends_request);
    }

    /**
     * 用户上线时候推送好友信息
     *  @param my_userName
     * @return
     */
    public Message.MyMessage getMyFriendsList(String my_userName){
        List<My_Friends> my_friendsList = my_friendsMapper.getMyFriendsByMyID(my_userName);
        if(my_friendsList != null &&my_friendsList.size()>0){
            Message.GetMyFriendsMessage_Response.Builder getMyFriendsMessage_Response = Message.GetMyFriendsMessage_Response.newBuilder();
            for (int i = 0 ; i < my_friendsList.size();i++){
                getMyFriendsMessage_Response.addMyFriendsMessage(
                        Message.GetMyFriendsMessage_Response.MyFriendsMessage.newBuilder()
                        .setUsername(my_friendsList.get(i).getMyFriend_user().getUsername())
                        .setFaceImage(my_friendsList.get(i).getMyFriend_user().getFace_image())
                        .setNickname(my_friendsList.get(i).getMyFriend_user().getNickname()).build()
                );
            }

            Message.MyMessage myMessage = Message.MyMessage.newBuilder()
                    .setActionType(GetMyFriendsListAction)
                    .setResponse(
                            Message.Response.newBuilder().setGetMyFriendsMessageResponse(
                                    getMyFriendsMessage_Response.build()
                            )
                    ).build();
            return myMessage;
        }
        return  null;
    }

    /**
     * 用户刚上线时候转发聊天信息
     */
    public Message.MyMessage forwardingMessage(String accept_user_id){
        List<Chat_Message> chat_messagesList = chat_messageMapper.getChat_MessageByAccept_user_id(accept_user_id);
        if(chat_messagesList == null || chat_messagesList.size() == 0){
            return null;
        }
        Message.Accept_ChatMessage_Response.Builder messageResponse =  Message.Accept_ChatMessage_Response.newBuilder();
        for(int i=0;i<chat_messagesList.size();i++){
            Chat_Message chat_message = new Chat_Message();
            messageResponse.addAcceptChatMessageBody(Message.Accept_ChatMessage_Response.Accept_ChatMessage_Body.newBuilder()
                    .setSendUserId(chat_messagesList.get(i).getSend_user_id())
                    .setAcceptUserId(chat_messagesList.get(i).getAccept_user_id())
                    .setMsg(chat_messagesList.get(i).getMsg())
                    .setMsgType(chat_messagesList.get(i).getMsg_type()+"")
                    .setCreateTime(chat_messagesList.get(i).getCreate_time())
                    .setSendUserNickname(chat_messagesList.get(i).getSend_users().getNickname())
                    .setSendUserImage(chat_messagesList.get(i).getSend_users().getFace_image()).build());
        }
        Message.MyMessage newMessage = Message.MyMessage.newBuilder()
                .setActionType(ChatMessageSendOrAccept)
                .setResponse(Message.Response.newBuilder().setAcceptChatMessageResponse(messageResponse.build()))
                .build();
        System.out.println("用户刚上线时候转发聊天信息:/n"+newMessage);
        chat_messageMapper.updateChat_MessageByAccept_user_id(accept_user_id);
        return newMessage;
    }



    /**
     *  用户在线时候转发聊天消息并保存数据库
     *  @param myMessage
     *  @return Message.MyMessage
     */
    public Message.MyMessage forwardingAndUpdateDatabase(Message.MyMessage myMessage){
        updateChatMessageDatabase(myMessage,1);
        Message.Accept_ChatMessage_Response.Builder messageResponse =  Message.Accept_ChatMessage_Response.newBuilder();
        messageResponse.addAcceptChatMessageBody(Message.Accept_ChatMessage_Response.Accept_ChatMessage_Body.newBuilder()
                .setSendUserId(myMessage.getRequest().getSendChatMessageRequest().getSendUserId())
                .setAcceptUserId(myMessage.getRequest().getSendChatMessageRequest().getAcceptUserId())
                .setMsg(myMessage.getRequest().getSendChatMessageRequest().getMsg())
                .setMsgType(myMessage.getRequest().getSendChatMessageRequest().getMsgType())
                .setCreateTime(myMessage.getRequest().getSendChatMessageRequest().getCreateTime()).build());
        Message.MyMessage newMessage = Message.MyMessage.newBuilder()
                .setActionType(ChatMessageSendOrAccept)
                .setResponse(Message.Response.newBuilder().setAcceptChatMessageResponse(messageResponse.build()))
                .build();
        System.out.println("用户在线时候转发聊天消息并保存数据库:/n"+newMessage);
        return newMessage;
    }

    /**
     * 更新未接受信息状态
     * @param myMessage
     * @param sign_flag
     */
    public void updateChatMessageDatabase(Message.MyMessage myMessage,int sign_flag){
        Chat_Message chat_message = new Chat_Message();
        chat_message.setId(UUIDUtils.create().toString().replaceAll("-",""));
        chat_message.setSend_user_id(myMessage.getRequest().getSendChatMessageRequest().getSendUserId());
        chat_message.setAccept_user_id(myMessage.getRequest().getSendChatMessageRequest().getAcceptUserId());
        chat_message.setMsg(myMessage.getRequest().getSendChatMessageRequest().getMsg());
        chat_message.setMsg_type(Integer.parseInt(myMessage.getRequest().getSendChatMessageRequest().getMsgType()));
        chat_message.setSign_flag(sign_flag);
        chat_message.setCreate_time(myMessage.getRequest().getSendChatMessageRequest().getCreateTime());
        chat_messageMapper.insertChat_Message(chat_message);
    }

    /**
     * 用户创建群组后保存到数据库,并组装好。
     * @param myMessage
     */
    public Message.MyMessage updateGroupsMessage(Message.MyMessage myMessage){
        //保存Groups表
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String GroupId = UUIDUtils.create().toString().replaceAll("-","");
        Group_Message groupMessage = new Group_Message();
        groupMessage.setId(GroupId);
        groupMessage.setGroup_name(myMessage.getRequest().getCreateGroupRequest().getGroupName());
        groupMessage.setCreat_username(myMessage.getRequest().getCreateGroupRequest().getGroupCreator());
        groupMessage.setCreat_time(df.format(new Date()));
        System.out.println(df.format(new Date()));
        groupsMapper.insertGroups(groupMessage);
        //保存Group_User表
        int message_size = myMessage.getRequest().getCreateGroupRequest().getUserNameList().size();
        for(int i=0 ; i<message_size ; i++){
            Group_User group_user = new Group_User();
            group_user.setId(UUIDUtils.create().toString().replaceAll("-",""));
            group_user.setGroupid(GroupId);
            group_user.setUsername(myMessage.getRequest().getCreateGroupRequest().getUserNameList().get(i));
            group_UserMapper.insertGroup_User(group_user);
        }
        //组装信息返回给在线的用户。
        return getOneGroupsMessage(GroupId);
    }

    /**
     * 获取单个群组信息,组装信息返回给在线的用户。
     */
    public Message.MyMessage getOneGroupsMessage(String GroupId){
        Group_Message group_message = groupsMapper.getGroupMessageByGroupID(GroupId);
        Message.Create_Group_Response.All_Group.Builder one_group = Message.Create_Group_Response.All_Group.newBuilder();
        one_group.setGroupid(group_message.getId());
        one_group.setGroupCreator(group_message.getCreat_username());
        one_group.setGroupName(group_message.getGroup_name());
        for(int i=0;i<group_message.getUsersList().size();i++){
            Message.Create_Group_Response.All_Group.Group_User.Builder group_user = Message.Create_Group_Response.All_Group.Group_User.newBuilder();
            group_user.setNickname(group_message.getUsersList().get(i).getNickname())
                    .setFaceImage(group_message.getUsersList().get(i).getFace_image())
                    .setUsername(group_message.getUsersList().get(i).getUsername()).build();
            one_group.addGroupUser(group_user);
        }

        Message.MyMessage  msg = Message.MyMessage.newBuilder().setActionType(CreateGroupAction)
                .setResponse(Message.Response.newBuilder().setCreateGroupResponse(
                        Message.Create_Group_Response.newBuilder().addAllGroup(one_group.build()).build()
                ).build()).build();
        return msg;
    }

    /**
     * 用户上线后发送过去给用户的群组信息
     */
    public Message.MyMessage forwardingGroupMessage(String userName ) {
        List<Group_Message> group_messageList = groupsMapper.getGroupMessageByUserName(userName);
        if( group_messageList == null || group_messageList.size() == 0){
            return null;
        }
        Message.Create_Group_Response.Builder create_Group_Response = Message.Create_Group_Response.newBuilder();
        for (int i = 0 ; i < group_messageList.size() ;i++) {
            Message.Create_Group_Response.All_Group.Builder all_group = Message.Create_Group_Response.All_Group.newBuilder();
            all_group.setGroupid(group_messageList.get(i).getId());
            all_group.setGroupCreator(group_messageList.get(i).getCreat_username());
            all_group.setGroupName(group_messageList.get(i).getGroup_name());
            for(int j = 0;j<group_messageList.get(i).getUsersList().size();j++){
                Message.Create_Group_Response.All_Group.Group_User.Builder group_user = Message.Create_Group_Response.All_Group.Group_User.newBuilder();
                group_user.setUsername(group_messageList.get(i).getUsersList().get(j).getUsername());
                group_user.setFaceImage(group_messageList.get(i).getUsersList().get(j).getFace_image());
                group_user.setNickname(group_messageList.get(i).getUsersList().get(j).getNickname());
                all_group.addGroupUser(group_user.build());
            }
            create_Group_Response.addAllGroup(all_group).build();
        }

        Message.MyMessage myMessage = Message.MyMessage.newBuilder()
                .setActionType(CreateGroupAction).setResponse(
                        Message.Response.newBuilder().setCreateGroupResponse(
                                create_Group_Response.build()
                        ).build()
                ).build();
        return myMessage;
    }

    public Message.MyMessage updateMyMessage(Message.MyMessage myMessage){
        Users users = new Users();
        users.setNickname(myMessage.getRequest().getChangeMyMessageAction().getNickName());
        users.setFace_image(myMessage.getRequest().getChangeMyMessageAction().getMyImage());
        users.setUsername(myMessage.getRequest().getChangeMyMessageAction().getUserName());
        if(usersMapper.updateUsersByUser(users)>0){
            return myMessage;
        }
        return null;
    }
    public int updatePassword(Message.MyMessage myMessage){
        if(usersMapper.getUserCountByUserNameAndPassWord(myMessage.getRequest().getChangeMyPassword().getUserName(),myMessage.getRequest().getChangeMyPassword().getOldPassword())>0){
            return usersMapper.updateUserPassword(myMessage.getRequest().getChangeMyPassword().getUserName(),myMessage.getRequest().getChangeMyPassword().getNewpassword());
        }
        return 0;
    }

    /**
     * 群组的聊天信息处理
     * @param msg
     */
    @Override
    public void handleGroupMessageSendOrAccept(Message.MyMessage msg) {
        List<String> listUsername = group_UserMapper.getUsersByGroupID(msg.getRequest().getSendChatMessageRequest().getAcceptUserId());
        if(listUsername!=null||listUsername.size()!=0){
            //插入Chat_Message信息表记录所有消息。
            String chatMessageID = UUIDUtils.create().toString().replace("-","");
            Date time = new  Date();
            Chat_Message chat_message = new Chat_Message();
            chat_message.setId(chatMessageID);
            chat_message.setCreate_time(df.format(time));
            chat_message.setSign_flag(2);
            chat_message.setMsg_type(Integer.parseInt(msg.getRequest().getSendChatMessageRequest().getMsgType()));
            chat_message.setAccept_user_id(msg.getRequest().getSendChatMessageRequest().getAcceptUserId());
            chat_message.setSend_user_id(msg.getRequest().getSendChatMessageRequest().getSendUserId());
            chat_message.setMsg(msg.getRequest().getSendChatMessageRequest().getMsg());
            chat_messageMapper.insertChat_Message(chat_message);

            for(int i = 0;i<listUsername.size();i++){
                //该用户在线则转发，否则加进数据库进行上线拉取。
                if(UserTCPChannelMap.get(listUsername.get(i)) != null && !UserTCPChannelMap.get(listUsername.get(i)).equals("")){
                    if(listUsername.get(i).equals(msg.getRequest().getSendChatMessageRequest().getSendUserId())){
                        continue;
                    }

                    Users users = usersMapper.getUsersByUserName(msg.getRequest().getSendChatMessageRequest().getSendUserId());
                    Message.MyMessage myMessage = Message.MyMessage.newBuilder()
                            .setActionType(ChatMessageSendOrAccept).setResponse(
                                    Message.Response.newBuilder().setAcceptChatMessageResponse(
                                        Message.Accept_ChatMessage_Response.newBuilder().addAcceptChatMessageBody(
                                                Message.Accept_ChatMessage_Response.Accept_ChatMessage_Body.newBuilder().setSendUserId(msg.getRequest().getSendChatMessageRequest().getSendUserId())
                                                .setAcceptUserId(msg.getRequest().getSendChatMessageRequest().getAcceptUserId())
                                                .setMsg(msg.getRequest().getSendChatMessageRequest().getMsg())
                                                .setMsgType(msg.getRequest().getSendChatMessageRequest().getMsgType())
                                                .setCreateTime(df.format(time))
                                                .setSendUserNickname(users.getNickname())
                                                .setSendUserImage(users.getFace_image()).build()
                                        ).build()
                                    ).build()
                            ).build();
                    UserTCPChannelMap.get(listUsername.get(i)).writeAndFlush(myMessage);

                }else {//用户不在线，保存数据库
                    String groupMessageTempID = UUIDUtils.create().toString().replaceAll("-","");
                    groupMessageTempMapper.insertByChatIDOrAccept(groupMessageTempID,chatMessageID,listUsername.get(i));
                }
            }
        }
    }

    /**
     * 用户上线后进行发送群组的信息
     * @param userName
     */
    @Override
    public void handleforwardingGroupMessageOnline(String userName) {
        List<Chat_Message> chat_messages = chat_messageMapper.getGroupChatMessageByAccept_user_id(userName);
        if(chat_messages != null && chat_messages.size() != 0){
            for(int i =0;i<chat_messages.size();i++){
                Users users = usersMapper.getUsersByUserName(chat_messages.get(i).getSend_user_id());
                Message.MyMessage myMessage = Message.MyMessage.newBuilder()
                        .setActionType(ChatMessageSendOrAccept).setResponse(
                                Message.Response.newBuilder().setAcceptChatMessageResponse(
                                        Message.Accept_ChatMessage_Response.newBuilder().addAcceptChatMessageBody(
                                                Message.Accept_ChatMessage_Response.Accept_ChatMessage_Body.newBuilder().setSendUserId(chat_messages.get(i).getSend_user_id())
                                                        .setAcceptUserId(chat_messages.get(i).getAccept_user_id())
                                                        .setMsg(chat_messages.get(i).getMsg())
                                                        .setMsgType(chat_messages.get(i).getMsg_type()+"")
                                                        .setCreateTime(chat_messages.get(i).getCreate_time())
                                                        .setSendUserNickname(users.getNickname())
                                                        .setSendUserImage(users.getFace_image()).build()
                                        ).build()
                                ).build()
                        ).build();
                UserTCPChannelMap.get(userName).writeAndFlush(myMessage);
                groupMessageTempMapper.deleteByChatMessageId(chat_messages.get(i).getId());
            }
        }
    }

    @Override
    public void handleDeleteFriendAction(Message.MyMessage msg) {
        my_friendsMapper.deleteByFriendIdandMyId(msg.getRequest().getDeleteFriendRequest().getDeletedUserName(),msg.getRequest().getDeleteFriendRequest().getSenderUserName());
        my_friendsMapper.deleteByFriendIdandMyId(msg.getRequest().getDeleteFriendRequest().getSenderUserName(),msg.getRequest().getDeleteFriendRequest().getDeletedUserName());
    }

    @Override
    public Message.MyMessage  handleChangeGroupAction(Message.MyMessage msg) {
        groupsMapper.updateGroupMessageByGorupid(msg.getRequest().getChangeGroupMessage().getGroupName(),msg.getRequest().getChangeGroupMessage().getGroupUserName());
        Message.MyMessage myMessage =  getOneGroupsMessage(msg.getRequest().getChangeGroupMessage().getGroupUserName());
        return myMessage;
    }

    @Override
    public Message.MyMessage handleQuitGroupAction(Message.MyMessage msg) {
        System.out.println(msg);
        group_UserMapper.deleteByUserName(msg.getRequest().getQuitGroupRequest().getGroupId(),msg.getRequest().getQuitGroupRequest().getQuitUserName());
        Message.MyMessage myMessage;
        List<String > usernameList = group_UserMapper.getUsersByGroupID(msg.getRequest().getQuitGroupRequest().getGroupId());
        if(usernameList == null || usernameList.size() == 0){
            groupMessageTempMapper.deleteByChatMessageId(msg.getRequest().getQuitGroupRequest().getGroupId());
            return null;
        }else{
            myMessage =  getOneGroupsMessage(msg.getRequest().getQuitGroupRequest().getGroupId());
        }
        return myMessage;
    }
}
