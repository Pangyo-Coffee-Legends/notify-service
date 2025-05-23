package com.nhnacademy.notifyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication
public class NotifyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotifyServiceApplication.class, args);
    }

}
