package com.zhihao.p2p_server.netty;

import io.netty.channel.Channel;

import java.util.HashMap;

public class UserTCPChannelMap {
    public static HashMap<String, Channel> manager = new HashMap<>();



    public static void put(String senderId, Channel channel) {
        manager.put(senderId, channel);
    }

    public static Channel get(String senderId) {
        return manager.get(senderId);
    }

    public static void remover(String senderId){
        manager.remove(senderId);
    }

    public static int getCount() {
        return manager.size();
    }

    public static void output() {
        for (HashMap.Entry<String, Channel> entry : manager.entrySet()) {
            System.out.println("UserId: " + entry.getKey()
                    + ", ChannelId: " + entry.getValue().id().asLongText());
        }
    }
}
