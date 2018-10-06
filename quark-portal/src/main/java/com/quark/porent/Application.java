package com.quark.porent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

/**
 * @Author LHR
 * Create By 2017/8/21
 */
@SpringBootApplication
public class Application {


    public static void main(String[] args) throws IOException {
        SpringApplication app = new SpringApplication(Application.class);
        app.run(args);
    }

}
