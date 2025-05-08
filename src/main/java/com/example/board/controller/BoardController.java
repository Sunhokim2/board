package com.example.board.controller;

import com.example.board.entity.Board;
import com.example.board.entity.BoardLike;
import com.example.board.entity.Comment;
import com.example.board.entity.FileAtch;
import com.example.board.entity.User;
import com.example.board.repository.BoardLikeRepository;
import com.example.board.repository.CommentRepository;
import com.example.board.repository.FileAtchRepository;
import com.example.board.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.board.repository.BoardRepository;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class BoardController {
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    UserRepository urp;
    @Autowired
    CommentRepository cmr;
    @Autowired
    BoardLikeRepository blr;
    @Autowired
    FileAtchRepository far;

    @GetMapping("/board/delete/{id}")
    public String boardDelete(
            @PathVariable Long id,
            HttpSession session) {
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
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        // 로그인이 필요합니다.
        User user = (User) session.getAttribute("user_info");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다!");
            return "redirect:/board/view?id=" + id;
        }
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 작성자만 수정가능하게 하기
        if (!board.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "수정 권한이 없습니다.");
            return "redirect:/board/view?id=" + id;
        }
        model.addAttribute("board", board);

        return "board/update";
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdatePost(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        // 이미 get으로 수정페이지 접근할때 했었지만 그 페이지에서 로그아웃하고 요청될수있으니 또 작성한다.
        User currentUser = (User) session.getAttribute("user_info");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/board/signin";
        }

        // 이것도 마찬가지로 2중으로 검사를 한다 생각하면 됨
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
        if (!board.getUser().getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("error", "수정 권한이 없습니다.");
            return "redirect:/board/view?id=" + id;
        }

        board.setTitle(title);
        board.setContent(content);
        boardRepository.save(board);
        redirectAttributes.addFlashAttribute("success", "게시글이 수정되었습니다.");
        return "redirect:/board/view?id=" + id; // Redirect back to the view page
    }

    // 클릭시 내용보기

    /**
     * board의 ID를 받아온다.
     * 
     * @Transactional쓰는 이유는 oneToMany인 좋아요와 댓글의 존재 때문
     * @param id
     * @return
     */
    @Transactional
    @GetMapping("/board/view")
    public String boardView(
            @RequestParam Long id,
            Model model,
            HttpSession session) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        // Increment view count
        board.setViewCount(board.getViewCount() + 1);
        boardRepository.save(board); // Save the updated count

        // Fetch comments
        List<Comment> comments = cmr.findByBoardIdOrderByCreatedAtAsc(id);

        // Fetch like count
        int likeCount = blr.countByBoardId(id);

        // Check if current user liked this post
        boolean userHasLiked = false;
        User currentUser = (User) session.getAttribute("user_info");
        if (currentUser != null) {
            userHasLiked = blr.findByUserIdAndBoardId(currentUser.getId(), id).isPresent();
        }

        model.addAttribute("board", board);
        model.addAttribute("comments", comments);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("userHasLiked", userHasLiked);
        model.addAttribute("currentUser", currentUser); // Pass current user for comment deletion check

        return "board/view";
    }

    @GetMapping("/board")
    public String board() {

        return "redirect:/board/list";
    }

    @GetMapping("/board/list")
    public String boardList(
            Model model,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String searchKeyword) {
        int pageSize = 3; // 페이지 당 아이템 수
        Pageable pageable = PageRequest.of(page - 1, pageSize); // page는 1부터 시작, Pageable은 0부터 시작

        Page<Board> boardsPage = boardRepository.findByTitleContainingOrContentContaining(
                searchKeyword, searchKeyword, pageable);
        int totalPages = boardsPage.getTotalPages(); // 전체 페이지 수

        // 페이지네이션 블록 계산 (예: 1-10, 11-20)
        int pageBlockSize = 10;
        int startPage = ((page - 1) / pageBlockSize) * pageBlockSize + 1;
        int endPage = startPage + pageBlockSize - 1;
        // endPage가 실제 totalPages보다 크면 totalPages로 조정
        endPage = Math.min(endPage, totalPages);

        Map<Long, Integer> commentCounts = new HashMap<>();
        Map<Long, Integer> likeCounts = new HashMap<>();
        for (Board board : boardsPage.getContent()) {
            commentCounts.put(board.getId(), cmr.countByBoardId(board.getId()));
            likeCounts.put(board.getId(), blr.countByBoardId(board.getId()));
        }

        model.addAttribute("list", boardsPage.getContent());
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page); // Pass current page number
        model.addAttribute("searchKeyword", searchKeyword); // Pass search keyword back
        model.addAttribute("commentCounts", commentCounts); // Pass comment counts map
        model.addAttribute("likeCounts", likeCounts);

        return "board/list";
    }

    @GetMapping("/board/write")
    public String boardWrite() {

        return "board/write";
    }

    // 받아온건 title, content
    // userId는 따로 넣어줘야함
    @PostMapping("/board/write")
    public String boardWritePost(
            @ModelAttribute Board board,
            @RequestParam MultipartFile file,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user_info");
        User author;

        if (user == null) {
            author = urp.findById(2L)
                    .orElseThrow(() -> new EntityNotFoundException("익명 사용자(ID: 2)를 찾을 수 없습니다."));
        } else {
            author = user;
        }
        board.setUser(author);
        board.setViewCount(0); // 조회수 0으로 시작
        Board savedBoard = boardRepository.save(board);

        // 파일 업로드 처리(DB에 저장)
        // FileAtch를 하는 이유는 여러 개를 첨부할 수 있기 때문
        // 하지만 지금은 하나만 첨부할거니까 그냥 Board에 넣어도 됨(하지만 여기선 FileAtch로 처리)
        FileAtch fileAtch = new FileAtch(); // 빈객체생성
        fileAtch.setBoard(savedBoard); // board와 연결
        String oName = file.getOriginalFilename();
        String ext = oName.substring(oName.lastIndexOf(".")); // 확장자
        String cName = UUID.randomUUID().toString() + ext; // 랜덤한 이름으로 변경
        fileAtch.setOName(oName);
        fileAtch.setCName(cName); // 랜덤

        far.save(fileAtch);

        // 실제 파일을 저장 (transferTo) (로컬컴퓨터에저장)
        try {
            file.transferTo(new File("C:/upload/" + cName)); // 실제 파일 저장
        } catch (IOException e) {
            return "redirect:/board/write"; // Redirect back to the write page on error
        }

        redirectAttributes.addFlashAttribute("success", "게시글이 작성되었습니다.");
        return "redirect:/board/list";
    }

    // --- Comment Handling ---

    @PostMapping("/board/{boardId}/comments")
    public String addComment(@PathVariable Long boardId,
            @RequestParam String content,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user_info");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "댓글을 작성하려면 로그인이 필요합니다.");
            return "redirect:/board/view?id=" + boardId;
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setBoard(board);
        comment.setUser(currentUser); // Set the logged-in user

        cmr.save(comment);
        redirectAttributes.addFlashAttribute("success", "댓글이 등록되었습니다.");

        return "redirect:/board/view?id=" + boardId;
    }

    /**
     * 댓글삭제
     * 
     * @param commentId
     * @param session
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/board/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user_info");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "댓글을 삭제하려면 로그인이 필요합니다.");
            return "redirect:/login"; // Or redirect back to the board if possible
        }

        Comment comment = cmr.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."));

        // Check if the current user is the author of the comment
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("error", "댓글 삭제 권한이 없습니다.");
            return "redirect:/board/view?id=" + comment.getBoard().getId(); // Redirect back to the board
        }

        Long boardId = comment.getBoard().getId(); // Get board ID before deleting comment
        cmr.delete(comment);
        redirectAttributes.addFlashAttribute("success", "댓글이 삭제되었습니다.");

        return "redirect:/board/view?id=" + boardId; // Redirect back to the board view page
    }

    // --- Like Handling ---

    // Use @Transactional as it involves check-then-act (delete or save)
    @Transactional
    @PostMapping("/board/{boardId}/like")
    public String toggleLike(@PathVariable Long boardId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user_info");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "좋아요를 하려면 로그인이 필요합니다.");
            return "redirect:/board/view?id=" + boardId;
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        Optional<BoardLike> existingLike = blr.findByUserIdAndBoardId(currentUser.getId(), boardId);

        if (existingLike.isPresent()) {
            // User already liked, so unlike (delete the like)
            blr.deleteById(existingLike.get().getId()); // Use the ID from the found like
            // Or use the custom delete method:
            // boardLikeRepository.deleteByUserIdAndBoardId(currentUser.getId(), boardId);
            redirectAttributes.addFlashAttribute("info", "좋아요를 취소했습니다.");
        } else {
            // User hasn't liked yet, so like (create a new like)
            BoardLike newLike = new BoardLike(currentUser, board);
            blr.save(newLike);
            redirectAttributes.addFlashAttribute("success", "좋아요를 눌렀습니다.");
        }

        return "redirect:/board/view?id=" + boardId;
    }
}