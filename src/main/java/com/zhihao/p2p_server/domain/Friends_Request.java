package com.zhihao.p2p_server.domain;

import java.util.List;

public class Friends_Request {
    private String id;
    private String send_user_id;
    private Users send_user_message;
    private String accept_user_id;
    private String remarks;
    private int accept_status;
    private String request_date_time;

    public Users getSend_user_message() {
        return send_user_message;
    }

    public void setSend_user_message(Users send_user_message) {
        this.send_user_message = send_user_message;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getAccept_status() {
        return accept_status;
    }

    public void setAccept_status(int accept_status) {
        this.accept_status = accept_status;
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

    public String getRequest_date_time() {
        return request_date_time;
    }

    public void setRequest_date_time(String request_date_time) {
        this.request_date_time = request_date_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
