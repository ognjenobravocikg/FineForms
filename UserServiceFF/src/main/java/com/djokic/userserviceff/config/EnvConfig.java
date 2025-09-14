package com.djokic.userserviceff.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class EnvConfig {
    @Bean
    public Dotenv dotenv(){
        return Dotenv.load();
    }

    @Bean
    public DataSource dataSource(Dotenv dotenv) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dotenv.get("USER_DB_URL"));
        config.setUsername(dotenv.get("USER_DB_USERNAME"));
        config.setPassword(dotenv.get("USER_DB_PASSWORD"));
        config.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(config);
    }
}
