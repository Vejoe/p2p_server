package com.zhihao.p2p_server.domain;

public class My_Friends {
    private String id;
    private String my_user_id;
    private String myfriend_user_id;

    public Users getMyFriend_user() {
        return myFriend_user;
    }

    public void setMyFriend_user(Users myFriend_user) {
        this.myFriend_user = myFriend_user;
    }

    private Users myFriend_user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMy_user_id() {
        return my_user_id;
    }

    public void setMy_user_id(String my_user_id) {
        this.my_user_id = my_user_id;
    }

    public String getMyfriend_user_id() {
        return myfriend_user_id;
    }

    public void setMyfriend_user_id(String myfriend_user_id) {
        this.myfriend_user_id = myfriend_user_id;
    }
}
