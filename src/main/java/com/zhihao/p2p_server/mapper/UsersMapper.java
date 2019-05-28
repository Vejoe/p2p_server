package com.zhihao.p2p_server.mapper;

import com.zhihao.p2p_server.domain.Users;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersMapper {
    public int insert(Users users);
    public int getUserCountByUserName(String userName);
    public int getUserCountByUserNameAndPassWord(String userName, String passWord);
    public Users getUsersByUserName(String userName);
    public int updateUsersByUser(Users users);
    public int updateUserPassword(String userName ,String newPassword);
}
