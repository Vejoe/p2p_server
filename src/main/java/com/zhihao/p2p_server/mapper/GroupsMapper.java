package com.zhihao.p2p_server.mapper;

import com.zhihao.p2p_server.domain.Group_Message;
import com.zhihao.p2p_server.domain.Group_User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupsMapper {
    public int insertGroups(Group_Message groupMessage);
    public List<Group_Message> getGroupMessageByUserName(String username);
    public Group_Message getGroupMessageByGroupID(String id);
    public int updateGroupMessageByGorupid(String groupName,String groupId);
    public int deleteById(String groupId);
}
