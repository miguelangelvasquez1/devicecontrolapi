package com.devicecontrolapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DevicecontrolapiApplication {

    public static void main(String[] args) {

        SpringApplication.run(DevicecontrolapiApplication.class, args);

    }

}