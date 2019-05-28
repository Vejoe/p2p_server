package com.zhihao.p2p_server.mapper;

import com.zhihao.p2p_server.domain.Chat_Message;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Chat_MessageMapper {
    public int insertChat_Message(Chat_Message chat_message);
    public List<Chat_Message>  getChat_MessageByAccept_user_id(String accept_user_id);
    public int updateChat_MessageByAccept_user_id(String accept_user_id);
    public List<Chat_Message> getGroupChatMessageByAccept_user_id(String accept_user_id);
}
