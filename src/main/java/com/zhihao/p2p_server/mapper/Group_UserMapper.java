package com.zhihao.p2p_server.mapper;

import com.zhihao.p2p_server.domain.Group_User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Group_UserMapper {
    public int insertGroup_User(Group_User group_user);
    public List<String> getUsersByGroupID(String groupid);
    public int deleteByUserName(String groupId, String UserName);
}
