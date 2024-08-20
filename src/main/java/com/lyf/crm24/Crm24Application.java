package com.lyf.crm24;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@MapperScan({"com.lyf.crm24.dao","com.lyf.crm24.base"})
public class Crm24Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Crm24Application.class, args);
    }

    /*设置web项目的启动入口*/
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Crm24Application.class);
    }
}
