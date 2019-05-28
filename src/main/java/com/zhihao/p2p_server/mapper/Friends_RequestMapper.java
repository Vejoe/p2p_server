package com.zhihao.p2p_server.mapper;

import com.zhihao.p2p_server.domain.Friends_Request;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Friends_RequestMapper {
    public int insertFriends_Request(Friends_Request friends_request);
    public int getFriends_RequestMatch(String send_user_id, String accept_user_id);
    public int updateFriends_Request(Friends_Request friends_request);
    public int getCountByAccept_user_id(String accept_user_id);
    public List<Friends_Request> getFriends_Request(String accept_user_id);
}
