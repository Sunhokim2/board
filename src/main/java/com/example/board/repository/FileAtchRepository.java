package com.example.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.board.entity.FileAtch;

@Repository
public interface FileAtchRepository
        extends JpaRepository<FileAtch, Long> {
    // Custom query methods can be added here if needed

}
