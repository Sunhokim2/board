package com.example.board.repository;

import com.example.board.entity.BoardLike;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    Optional<BoardLike> findByUserIdAndBoardId(Long userId, Long boardId);

    // 좋아요 수 체크하기
    Integer countByBoardId(Long boardId);

    // oneTomany때문에 트랜잭셔널 사용(삭제용도)
    @Transactional
    void deleteByUserIdAndBoardId(Long userId, Long boardId);
}
