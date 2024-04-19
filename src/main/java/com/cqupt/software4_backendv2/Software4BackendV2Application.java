package com.cqupt.software4_backendv2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.cqupt.software4_backendv2.dao")
public class Software4BackendV2Application {

    public static void main(String[] args) {
        SpringApplication.run(Software4BackendV2Application.class, args);
    }

}
