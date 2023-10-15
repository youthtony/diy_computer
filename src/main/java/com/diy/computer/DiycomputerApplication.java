package com.diy.computer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//日志注解
@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
public class DiycomputerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiycomputerApplication.class,args);
        log.info("项目启动成功");
    }
}
