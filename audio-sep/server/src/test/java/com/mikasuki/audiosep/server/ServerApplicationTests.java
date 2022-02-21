package com.mikasuki.audiosep.server;

import com.mikasuki.audiosep.server.entity.AudioFile;
import com.mikasuki.audiosep.server.mapper.AudioFileMapper;

import jdk.internal.cmm.SystemResourcePressureImpl;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
class ServerApplicationTests {

    @Autowired
    AudioFileMapper fileMapper;

    @Test
    void test1() {
        AudioFile audioFile = new AudioFile();
        audioFile.setName("test");
        audioFile.setLastDate("2020");
        System.out.println(fileMapper.addOne(audioFile));
        System.out.println(audioFile.getId());

    }

    @Test
    void test2() {
        fileMapper.addUrl(2, "pppp");
        fileMapper.addUrl(2, "ssss");
        fileMapper.addUrl(2, "aaa");
    }

    @Test
    void test3() {
        fileMapper.findAll().forEach(System.out::println);
    }

    @Test
    void test4() {
        fileMapper.deleteById(2);
    }

}
