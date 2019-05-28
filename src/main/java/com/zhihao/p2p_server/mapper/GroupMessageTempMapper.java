package com.zhihao.p2p_server.mapper;

import org.springframework.stereotype.Repository;

@Repository
public interface GroupMessageTempMapper {
    public int insertByChatIDOrAccept(String id ,String chatMessageId,String acceptUser);
    public int deleteByChatMessageId(String chatMessageId);
}
