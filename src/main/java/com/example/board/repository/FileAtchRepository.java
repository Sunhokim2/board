package com.example.board.repository;

import com.example.board.entity.Board;
import com.example.board.entity.FileAtch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileAtchRepository extends JpaRepository<FileAtch, Long> {
    FileAtch findByBoard(Board board);
}
