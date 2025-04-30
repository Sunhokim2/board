package com.example.board.controller;

import com.example.board.entity.User;
import com.example.board.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class UserController {
	@Autowired
	UserRepository urp;


	@GetMapping("/signin")
	public String signin() {


		return "signin";
	}

	@PostMapping("/signin")
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

	@GetMapping("/signout")
	public String signout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
	
	@GetMapping("/signup") 
	public String signup() {
		return "signup";
	}

	@PostMapping("/signup")
	public String signupPost(@ModelAttribute User user) {
		urp.save(user);

		return "redirect:/";
	}

	//로그인 여부확인
	@GetMapping("/api/auth/status")
	public Map<String, Boolean> getAuthStatus(HttpSession session) {
		Map<String, Boolean> response = new HashMap<>();

		boolean isLoggedIn = (session != null && session.getAttribute("user") != null);
		response.put("loggedIn", isLoggedIn);
		return response;
	}
}