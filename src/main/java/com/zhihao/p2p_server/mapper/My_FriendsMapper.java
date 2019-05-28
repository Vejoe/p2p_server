package com.zhihao.p2p_server.mapper;

import com.zhihao.p2p_server.domain.My_Friends;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface My_FriendsMapper {
    public int insertMy_Friends(String id , String my_user_id , String my_friend_user_id);
    public List<My_Friends> getMyFriendsByMyID(String my_user_id);
    public int deleteByFriendIdandMyId(String MyId ,String FriendId);
}
