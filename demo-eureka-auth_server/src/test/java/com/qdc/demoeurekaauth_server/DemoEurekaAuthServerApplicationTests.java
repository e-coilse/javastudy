package com.qdc.demoeurekaauth_server;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoEurekaAuthServerApplicationTests {

    @Autowired
    DruidDataSource druidDataSource;

    @Test
    void contextLoads() {
        System.out.println(druidDataSource);
    }

}
