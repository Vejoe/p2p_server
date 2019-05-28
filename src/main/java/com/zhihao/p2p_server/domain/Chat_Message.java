package com.zhihao.p2p_server.domain;

public class Chat_Message {
    private String id;
    private String send_user_id;
    private String accept_user_id;
    private String msg;
    private int msg_type;
    private int sign_flag;
    private String create_time;
    private  Users send_users;

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSend_user_id() {
        return send_user_id;
    }

    public void setSend_user_id(String send_user_id) {
        this.send_user_id = send_user_id;
    }

    public String getAccept_user_id() {
        return accept_user_id;
    }

    public void setAccept_user_id(String accept_user_id) {
        this.accept_user_id = accept_user_id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getSign_flag() {
        return sign_flag;
    }

    public void setSign_flag(int sign_flag) {
        this.sign_flag = sign_flag;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public Users getSend_users() {
        return send_users;
    }

    public void setSend_users(Users send_users) {
        this.send_users = send_users;
    }
}
