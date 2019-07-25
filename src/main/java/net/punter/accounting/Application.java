package net.punter.accountapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackages = {"net.punter.accountapp"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        }
    }

