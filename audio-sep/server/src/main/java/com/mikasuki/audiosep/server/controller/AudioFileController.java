package com.mikasuki.audiosep.server.controller;

import com.mikasuki.audiosep.server.entity.AudioFile;
import com.mikasuki.audiosep.server.server.AudioFileService;
import com.mikasuki.audiosep.server.utils.FileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class AudioFileController {

    @Autowired
    AudioFileService fileService;

    @PostMapping("/files")
    public ResponseEntity<Map<String, List<String>>> uploadAudioFiles(
            @RequestPart("files") MultipartFile[] files) {
        if (files == null || files.length == 0)
            throw new FileException("未接受到文件");

        Map<String, List<String>> retMap = new ConcurrentHashMap<>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            retMap.put(fileName.substring(0, fileName.indexOf('.')), fileService.save(file));
        }

        return ResponseEntity.ok(retMap);
    }

    @GetMapping("/files/{fileDir}/{fileName}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable("fileDir") String fileDir,
            @PathVariable("fileName") String fileName,
            HttpServletRequest request) {

        Resource resource = fileService.loadFileAsResource(fileDir, fileName);
        String contentType = null;

        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
       } catch (IOException e) {
            e.printStackTrace();
            throw new FileException("不能确定文件类型");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/records")
    public ResponseEntity<List<AudioFile>> getHistoryUploadRecords() {
        List<AudioFile> audioFiles = fileService.getHistoryUploadRecord();
        return ResponseEntity.ok(audioFiles);
    }

    @DeleteMapping("/records/{id}")
    public ResponseEntity<String> deleteOneRecord(@PathVariable("id") int id) {
        fileService.deleteOneRecord(id);
        return ResponseEntity.ok("成功移除一条记录");
    }
}
