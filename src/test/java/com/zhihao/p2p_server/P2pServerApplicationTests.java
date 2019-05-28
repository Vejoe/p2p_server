package com.zhihao.p2p_server;

import com.zhihao.p2p_server.domain.*;
import com.zhihao.p2p_server.mapper.*;
import com.zhihao.p2p_server.netty.UDPServer;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetSocketAddress;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class P2pServerApplicationTests {

    @Autowired
    private Friends_RequestMapper friends_RequestMapper;
    @Autowired
    private My_FriendsMapper my_friendsMapper;
    @Autowired
    private Chat_MessageMapper chat_MessageMapper;
    @Autowired
    private GroupsMapper groupsMapper;
    @Autowired
    private Group_UserMapper group_UserMapper;
    @Test
    public void contextLoads() {

        List<My_Friends> my_friendslist = my_friendsMapper.getMyFriendsByMyID("weizhihao1");
        System.out.println(my_friendslist.size());
        for(int i =0 ; i<my_friendslist.size();i++){
            System.out.println(my_friendslist.get(i).getMyFriend_user().getUsername());
        }

    }
    @Test
    public void contextLoads1() {
        List<Chat_Message> chat_messages = chat_MessageMapper.getChat_MessageByAccept_user_id("weizhihao1");
        for(int i =0 ; i<chat_messages.size();i++){
            System.out.println(chat_messages.get(i).getSend_users().getNickname());
        }
    }
    @Test
    public void TreadTest(){
        Group_Message group_messageList = groupsMapper.getGroupMessageByGroupID("weizhihao2");
//        System.out.println(group_messageList.get(0).getUsersList().size()+","+group_messageList.get(1).getUsersList().size());
    }
    @Test
    public void udpTest(){

        UDPServer.getInstance().getChannel().writeAndFlush(new DatagramPacket(
                Unpooled.copiedBuffer(("ServerTop2p 11111 33323").getBytes()),
                new InetSocketAddress("localhost", 8888)));
    }
    @Test
    public void udpTest111(){
        try{
            Class<?> cla =  Class.forName("java.lang.String");

        }catch (ClassNotFoundException cnfe){
            cnfe.printStackTrace();
        }

    }

}
