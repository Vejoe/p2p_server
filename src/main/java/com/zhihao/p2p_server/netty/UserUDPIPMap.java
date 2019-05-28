package com.zhihao.p2p_server.netty;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class UserUDPIPMap {
    private static HashMap<String, InetSocketAddress> manager = new HashMap<>();

    public static void put(String senderId, InetSocketAddress ip) {
        manager.put(senderId, ip);
    }

    public static void remove(String senderId) {
        manager.remove(senderId);
    }

    public static InetSocketAddress get(String senderId) {
        return manager.get(senderId);
    }
    public static int getCount() {
        return manager.size();
    }

}
