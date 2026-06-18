package com.ds.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.ds")
public class DsServerApp {

    public static void main(String[] args) {
        SpringApplication.run(DsServerApp.class, args);
    }
}
