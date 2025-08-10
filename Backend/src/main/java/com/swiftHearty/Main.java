package com.swiftHearty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Main.class, args);

        System.out.println("Registered Beans:");
        String[] beanNames = ctx.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            if (beanName.toLowerCase().contains("mail")) {
                System.out.println(beanName);
            }
        }
    }

}