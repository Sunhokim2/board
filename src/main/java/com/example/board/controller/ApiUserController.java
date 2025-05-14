package com.example.board.controller;

import com.example.board.entity.User;
import com.example.board.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class ApiUserController {
	@Autowired
	UserRepository urp;

	@PostMapping("/api/user/signin")
	public String signinPost(@ModelAttribute User user, HttpSession session) {
		Optional<User> optionalUser = urp.findByEmail(user.getEmail());
		if (optionalUser.isPresent()) {
			// 2. 사용자가 존재하면 비밀번호 비교
			User dbUser = optionalUser.get(); // DB에서 조회한 완전한 User 객체

			if (user.getPwd().equals( dbUser.getPwd())){
				user.setName(dbUser.getName());
				user.setId(dbUser.getId());
				System.out.println(user);
				session.setAttribute("user_info", user);
				session.setAttribute("date", new Date());

				return "redirect:/";
			}
		}
		System.out.println("로그인실패");

		return "redirect:/signin";

	}
	

	@CrossOrigin
	@PostMapping("/api/user/signup")
	public ResponseEntity<?> signupPost(
			@RequestBody User user) {
		Map<String, Object> response = new HashMap<>();

		try {
			User savedUser = urp.save(user); // 사용자 저장
			// 성공 응답
			response.put("message", "User registered successfully!");
			response.put("userId", savedUser.getId()); // 예시로 사용자 ID 포함 (필요에 따라)
			// HTTP 201 Created 상태 코드와 함께 응답 본문 전송
			return ResponseEntity.status(HttpStatus.CREATED).body(response);

		} catch (org.springframework.dao.DataIntegrityViolationException e) {
			// 예를 들어, 사용자 이름이나 이메일이 고유해야 하는데 중복된 경우 발생 가능
			response.put("message", "User already exists or data integrity violation.");
			// HTTP 409 Conflict 또는 400 Bad Request
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		} catch (Exception e) {
			// 기타 예상치 못한 오류
			e.printStackTrace(); // 서버 로그에 오류 상세 기록
			response.put("message", "An unexpected error occurred during registration.");
			// HTTP 500 Internal Server Error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}


}