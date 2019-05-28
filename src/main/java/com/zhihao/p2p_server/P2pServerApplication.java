package com.zhihao.p2p_server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;

@SpringBootApplication
@MapperScan("com.zhihao.p2p_server.mapper")
@ComponentScan(basePackages = "com.zhihao")
public class P2pServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(P2pServerApplication.class, args);
    }
    @Bean
    public ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        return new ProtobufHttpMessageConverter();
    }
}
