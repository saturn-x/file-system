package com.example.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt.config")
@Data
public class JwtConfig {
    private String key  ;

    private Long ttl  ;

    private Long remember;
}
