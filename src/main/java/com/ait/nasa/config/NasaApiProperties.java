package com.ait.nasa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nasa.api")
public record NasaApiProperties(String baseUrl, String key) {}