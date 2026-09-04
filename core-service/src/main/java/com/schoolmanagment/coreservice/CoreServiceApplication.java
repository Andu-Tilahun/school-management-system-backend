package com.schoolmanagment.coreservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.schoolmanagment")
@EntityScan(basePackages = "com.schoolmanagment")
@EnableJpaRepositories(basePackages = "com.schoolmanagment")
@EnableFeignClients(basePackages = {
        "com.schoolmanagment.commonsecurity.client"
})
public class CoreServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreServiceApplication.class, args);
    }

}
