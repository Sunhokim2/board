package com.example.board.entity;



import jakarta.persistence.*;
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
