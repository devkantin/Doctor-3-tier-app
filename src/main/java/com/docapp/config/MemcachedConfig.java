package com.docapp.config;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MemcachedConfig {

    private static final Logger log = LoggerFactory.getLogger(MemcachedConfig.class);

    @Value("${memcached.host:localhost}")
    private String host;

    @Value("${memcached.port:11211}")
    private int port;

    @Bean
    public MemcachedClient memcachedClient() {
        try {
            log.info("Connecting to Memcached at {}:{}", host, port);
            return new XMemcachedClient(host, port);
        } catch (IOException e) {
            log.warn("Could not connect to Memcached ({}:{}). Caching disabled.", host, port);
            return null;
        }
    }
}
