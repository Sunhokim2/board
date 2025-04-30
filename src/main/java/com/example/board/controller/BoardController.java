package com.example.board.controller;

import com.example.board.entity.Board;
import com.example.board.entity.User;
import com.example.board.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.board.repository.BoardRepository;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;
import java.util.Optional;

@Controller
public class BoardController {
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    UserRepository urp;

    @GetMapping("/board/delete/{id}")
    public String boardDelete(
            @PathVariable Long id,
            HttpSession session
    ) {
        Optional<Board> board = boardRepository.findById(id);
        User user = board.get().getUser();

        Object userInfoObject = session.getAttribute("user_info");
        if (userInfoObject != null) {
            User userInfo = (User) userInfoObject;
            if (userInfo.getId() == user.getId()) {
                boardRepository.delete(board.get());
            }
        }


        return "redirect:/board";
    }

    @GetMapping("/board/update")
    public String boardUpdate(
            @RequestParam Long id,
            Model model
    ) {
        Optional<Board> board = boardRepository.findById(id);
        Board boardData = board.get();
        model.addAttribute("board", boardData);

        return "board/update";
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdatePost(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content
    ) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (optionalBoard.isPresent()) {
            Board board = optionalBoard.get();
            board.setTitle(title);
            board.setContent(content);
            boardRepository.save(board);
        }
        return "redirect:/board";
    }


    //클릭시 내용보기

    /**
     * board의 ID를 받아온다.
     *
     * @param id
     * @return
     */
    @GetMapping("/board/view")
    public String boardView(
            @RequestParam Long id,
            Model model
    ) {

        Optional<Board> board = boardRepository.findById(id);
        Board boardData = board.get();
        model.addAttribute("board", boardData);

        return "board/view";
    }

    @GetMapping("/board")
    public String board() {

        return "redirect:/board/list";
    }

    @GetMapping("/board/list")
    public String boardList(
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


        model.addAttribute("list", boards.getContent());
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("page", page);

        return "board/list";
    }

    @GetMapping("/board/write")
    public String boardWrite() {

        return "board/write";
    }

    //받아온건 title, content
    //userId는 따로 넣어줘야함
    @PostMapping("/board/write")
    public String boardWritePost(
            @ModelAttribute Board board,
            @SessionAttribute(value = "user_info", required = false) User user) {
        User newUser = new User();
        if (user == null) {
            newUser = urp.getById(2L); //익명 계좌
        } else {
            newUser = user;
        }
        board.setUser(newUser);
        boardRepository.save(board);
        return "redirect:/board";
    }
}