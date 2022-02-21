package com.mikasuki.audiosep.server.server;

import com.mikasuki.audiosep.server.entity.AudioFile;
import com.mikasuki.audiosep.server.entity.FileProperties;
import com.mikasuki.audiosep.server.mapper.AudioFileMapper;
import com.mikasuki.audiosep.server.utils.FileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AudioFileService {

    private final Path uploadStorageLocation;
    private final Path downloadStorageLocation;

    @Autowired
    private AudioFileMapper audioFileMapper;

    @Autowired
    AudioFileService(FileProperties fileProperties) {

        uploadStorageLocation = Paths.get(fileProperties.getUploadDir()).toAbsolutePath().normalize();
        downloadStorageLocation = Paths.get(fileProperties.getDownloadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadStorageLocation);
            Files.createDirectories(downloadStorageLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> save(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        Path targetPath = uploadStorageLocation.resolve(fileName);

        try {
            file.transferTo(targetPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileException("文件保存失败");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String lastDate = dateFormat.format(new Date());
        AudioFile audioFile = new AudioFile();
        audioFile.setName(fileName);
        audioFile.setStatus("完成");
        audioFile.setLastDate(lastDate);
        audioFileMapper.addOne(audioFile);

        String uri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/files")
                .path("/" + fileName.substring(0, fileName.indexOf('.')))
                .toUriString();

        List<String> urls = Stream.of(uri + "/accompaniment.wav", uri + "/vocals.wav")
                .collect(Collectors.toList());

        urls.forEach(url -> audioFileMapper.addUrl(audioFile.getId(), url));

        return urls;
    }

    public Resource loadFileAsResource(String fileDir, String fileName) {
        Path filePath = downloadStorageLocation
                .resolve(fileDir)
                .resolve(fileName)
                .normalize();

        Resource resource = null;

        try {
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists())
                throw new FileException("文件未找到");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new FileException("文件未找到");
        }

        return resource;
    }

    public List<AudioFile> getHistoryUploadRecord() {
        return audioFileMapper.findAll();
    }

    public int deleteOneRecord(int id) {
        return audioFileMapper.deleteById(id);
    }

    public int addOneRecord(AudioFile file) {
        return audioFileMapper.addOne(file);
    }

    public Path getUploadStorageLocation() {
        return uploadStorageLocation;
    }

}
