package com.example.board.entity;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class FileAtch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String oName;
    String cName;

    @OneToOne
    Board board; // 게시글과의 관계 설정 (1:1 관계)
}
