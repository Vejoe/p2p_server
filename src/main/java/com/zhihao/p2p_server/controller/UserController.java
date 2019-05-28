package com.zhihao.p2p_server.controller;

import com.zhihao.p2p_server.Utils.UUIDUtils;
import com.zhihao.p2p_server.domain.Users;
import com.zhihao.p2p_server.protobuf.Message;
import com.zhihao.p2p_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sun.misc.Request;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(value = "/register",consumes = "application/x-protobuf")
    public int userRegister(RequestEntity<Message.Register_Message> requestEntity){
        Message.Register_Message message = requestEntity.getBody();
        System.out.println(message);
        Users user = new Users();
        user.setId(UUIDUtils.create().toString().replaceAll("-",""));
        user.setUsername(message.getUsername());
        user.setPassword(message.getPassword());
        user.setFace_image(null);
        user.setFace_image_big(message.getFaceImageBig());
        user.setNickname(message.getNickname());
        return userService.registerUser(user);
    }

    @PostMapping(value = "/login",consumes = "application/x-protobuf",produces = "application/x-protobuf")
    public ResponseEntity<Message.Login_User_Message> userLogin(RequestEntity<Message.Login_Message> requestEntity){
        Message.Login_Message message = requestEntity.getBody();
        System.out.println(message);
        Users user = null;
        user =userService.getCorrentUser(message.getUsername(),message.getPassword());
        if(user == null){
            return null;
        }
        Message.Login_User_Message login_user_message = Message.Login_User_Message.newBuilder()
                .setUserName(user.getUsername())
                .setFaceImage(user.getFace_image())
                .setNickname(user.getNickname())
                .build();
        return ResponseEntity.ok(login_user_message);
    }

    @PostMapping(value = "/updateMyMessage",consumes = "application/x-protobuf",produces = "application/x-protobuf")
    public ResponseEntity<Message.MyMessage> updateMyMessage(RequestEntity<Message.MyMessage> requestEntity){
        Message.MyMessage myMessage = requestEntity.getBody();
        return ResponseEntity.ok(userService.updateMyMessage(myMessage));
    }

    @PostMapping(value = "/updatePassword",consumes = "application/x-protobuf")
    public int updatePassword(RequestEntity<Message.MyMessage> requestEntity){
        Message.MyMessage myMessage = requestEntity.getBody();
        return userService.updatePassword(myMessage);
    }
}
