package com.lyj.zhihumanage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ZhihuManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhihuManageApplication.class, args);
    }

}
