package com.zhihao.p2p_server.domain;

import java.util.List;

public class Group_Message {
    private String id;
    private String group_name;
    private String creat_username;
    private String creat_time;
    private List<Users> usersList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getCreat_username() {
        return creat_username;
    }

    public void setCreat_username(String creat_username) {
        this.creat_username = creat_username;
    }

    public String getCreat_time() {
        return creat_time;
    }

    public void setCreat_time(String creat_time) {
        this.creat_time = creat_time;
    }

    public List<Users> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<Users> usersList) {
        this.usersList = usersList;
    }
}
