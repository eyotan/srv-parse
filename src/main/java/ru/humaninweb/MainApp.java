package ru.humaninweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author vit
 */
@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
public class MainApp {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(MainApp.class);
        springApplication.addListeners(new ApplicationPidFileWriter());
        springApplication.run(args);
    }
}
