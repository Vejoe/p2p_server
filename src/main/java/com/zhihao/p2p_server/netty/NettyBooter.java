package com.zhihao.p2p_server.netty;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class NettyBooter  implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    //ContextRefreshedEvent为初始化完毕事件
    /**
     * 当一个ApplicationContext被初始化或刷新触发
     */
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(contextRefreshedEvent.getApplicationContext().getParent() == null){
            try{
                Thread thread = new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                UDPServer.getInstance().start();
                            }
                        }
                );
                thread.start();
                TCPServer.getInstance().start();
            }catch(Exception ex){
                ex.printStackTrace();
            }

        }
    }
}
