package com.zhihao.p2p_server.service;

import com.zhihao.p2p_server.domain.Friends_Request;
import com.zhihao.p2p_server.domain.Users;
import com.zhihao.p2p_server.protobuf.Message;

public interface UserService {
    public int registerUser(Users users);
    public int loginUser(String userName, String passWord);
    public Users getUsers(String userName);
    public Users getCorrentUser(String userName,String passWord);
    public void insertFriends_Request(String send_user_id , String accept_user_id , String remarks);
    public Message.MyMessage getFriendsRequestMessage(String accept_user_id);
    public void updateFriendsRequestMessage(Message.MyMessage myMessage);
    public Message.MyMessage getMyFriendsList(String my_userName);
    public Message.MyMessage forwardingMessage(String accept_user_id);
    public void updateChatMessageDatabase(Message.MyMessage myMessage,int sign_flag);
    public Message.MyMessage forwardingAndUpdateDatabase(Message.MyMessage myMessage);
    public Message.MyMessage  updateGroupsMessage(Message.MyMessage myMessage);
    public Message.MyMessage forwardingGroupMessage(String userName );
    public Message.MyMessage updateMyMessage(Message.MyMessage myMessage);
    public int updatePassword(Message.MyMessage myMessage);

    public void handleGroupMessageSendOrAccept(Message.MyMessage msg);
    public void handleforwardingGroupMessageOnline(String userName);

    public void handleDeleteFriendAction(Message.MyMessage msg);

    public Message.MyMessage  handleChangeGroupAction(Message.MyMessage msg);

    public Message.MyMessage handleQuitGroupAction(Message.MyMessage msg);
}
