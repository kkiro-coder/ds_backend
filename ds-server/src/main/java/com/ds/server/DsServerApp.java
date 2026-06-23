package com.ds.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.ds")
@EnableJpaRepositories(basePackages = "com.ds")
@EntityScan(basePackages = "com.ds")
public class DsServerApp {

    public static void main(String[] args) {
        SpringApplication.run(DsServerApp.class, args);
    }
}
