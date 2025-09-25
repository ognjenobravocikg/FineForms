package com.djokic.responseserviceff;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ResponseServiceFfApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        System.setProperty("RESPONSE_DB_URL", dotenv.get("RESPONSE_DB_URL"));
        System.setProperty("RESPONSE_DB_USERNAME", dotenv.get("RESPONSE_DB_USERNAME"));
        System.setProperty("RESPONSE_DB_PASSWORD", dotenv.get("RESPONSE_DB_PASSWORD"));

        SpringApplication.run(ResponseServiceFfApplication.class, args);
    }

}
