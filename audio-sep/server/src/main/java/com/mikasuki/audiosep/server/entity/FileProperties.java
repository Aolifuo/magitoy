package com.mikasuki.audiosep.server.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "file")
@Data
public class FileProperties {
    private String uploadDir;
    private String downloadDir;
}
