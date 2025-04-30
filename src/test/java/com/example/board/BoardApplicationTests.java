package com.example.board;

import com.example.board.entity.Board;
import com.example.board.repository.BoardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

@SpringBootTest
class BoardApplicationTests {

    @Autowired
    BoardRepository boardRepository;
    @Test
    void contextLoads(
            Model model,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        int pageSize = 3; // 페이지 당 아이템 수
        Pageable pageable = PageRequest.of(page - 1, pageSize); // page는 1부터 시작, Pageable은 0부터 시작

        Page<Board> boards = boardRepository.findAll(pageable);
        int totalPages = boards.getTotalPages(); // 전체 페이지 수

        // 페이지네이션 블록 계산 (예: 1-10, 11-20)
        int pageBlockSize = 10;
        int startPage = ((page - 1) / pageBlockSize) * pageBlockSize + 1;
        int endPage = startPage + pageBlockSize - 1;
        // endPage가 실제 totalPages보다 크면 totalPages로 조정
        endPage = Math.min(endPage, totalPages);


        boards.forEach(board -> {
            System.out.println(board.getTitle());
            System.out.println(board.getUser().getName());
        });

    }

}
