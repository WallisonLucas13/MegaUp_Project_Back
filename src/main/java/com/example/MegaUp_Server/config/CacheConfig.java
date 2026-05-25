package com.example.MegaUp_Server.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Cache para o 2FA do ADMIN.
     * Chave = código gerado; valor = username do admin pendente.
     * TTL de 3 minutos — após expirar, getIfPresent() retorna null automaticamente.
     * maximumSize = 10 para suportar múltiplos admins simultâneos sem crescimento ilimitado.
     */
    @Bean
    public Cache<String, String> adminCodeCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.MINUTES)
                .maximumSize(10)
                .build();
    }
}
