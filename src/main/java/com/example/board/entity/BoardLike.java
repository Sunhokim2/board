package com.example.board.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor // Needed for JPA
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"user_id", "board_id"}) // 유저한명단 게시물 하나에 좋아요할수있는설정
})
public class BoardLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    // Constructor for convenience
    public BoardLike(User user, Board board) {
        this.user = user;
        this.board = board;
    }
}