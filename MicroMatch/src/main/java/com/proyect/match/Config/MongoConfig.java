package com.proyect.match.Config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {
    
    @Bean
    public MongoClient mongoClient() {
        String connectionString = System.getProperty("MATCH_URI", "mongodb://localhost:27017/db_fullstack");
        ConnectionString connString = new ConnectionString(connectionString);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .build();
        return MongoClients.create(settings);
    }
    
    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "db_fullstack");
    }
}