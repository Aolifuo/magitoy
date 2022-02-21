package com.mikasuki.audiosep.server.controller;

import com.mikasuki.audiosep.server.utils.FileException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FileExceptionHandler {

    @ExceptionHandler(FileException.class)
    public ResponseEntity<String> handleFileException(FileException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessException(DataAccessException e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body("数据库查询出错");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body("服务器出现其他错误");
    }
}
