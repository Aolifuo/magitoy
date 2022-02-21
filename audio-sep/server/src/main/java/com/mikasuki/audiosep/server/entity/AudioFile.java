package com.mikasuki.audiosep.server.entity;

import lombok.Data;

import java.util.List;

@Data
public class AudioFile {
    private int id;
    private String name;
    private String status;
    private String lastDate;
    private List<String> urls;
}
