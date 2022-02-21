package com.mikasuki.audiosep.server.mapper;

import com.mikasuki.audiosep.server.entity.AudioFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AudioFileMapper {
    public List<AudioFile> findAll();
    public int deleteById(int id);
    public int addOne(AudioFile file);
    public int addUrl(@Param("file_id") int fileId, @Param("url") String url);
}
