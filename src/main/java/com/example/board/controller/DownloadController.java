package com.example.board.controller;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;

import com.example.board.entity.Board;
import com.example.board.entity.FileAtch;
import com.example.board.repository.FileAtchRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DownloadController {
    private final FileAtchRepository fileAtchRepository;

    public DownloadController(FileAtchRepository fileAtchRepository) {
        this.fileAtchRepository = fileAtchRepository;
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> download(
            @RequestParam Long boardId
    ) throws Exception {
//        FileAtch fileAtch = FileAtch레포지토리.findByBoard(Board board);
//        String cName = fileAtch.getCName();
        Board board = new Board();
        board.setId(boardId);
        FileAtch fileAtch = fileAtchRepository.findByBoard(board);
        String cName = fileAtch.getCName();



        File file = new File("c:/upload/" + cName);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok().
                header("content-disposition",
                        "attachment; filename=\"" + URLEncoder.encode(file.getName(),
                                "utf-8") + "\"").
                contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    }
}