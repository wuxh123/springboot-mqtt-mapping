package com.central;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 作者 wuxh 
 */
@SpringBootApplication
public class DevicesApp {
    public static void main(String[] args) {
        SpringApplication.run(DevicesApp.class, args);
    }
}
