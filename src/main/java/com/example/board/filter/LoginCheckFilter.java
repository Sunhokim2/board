package com.example.board.filter;

import com.example.board.entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;

import java.io.IOException;


/**
 * 필터를 이용해서 로그인상태에서 signin접속시 자동으로 board/list로 이동,
 * 비로그인시 글작성하려하면 로그인창으로 이동 (단, 현재는 자동으로 user가 ㅇㅇ가 되므로 작동하지 않습니다.
 */
public class LoginCheckFilter implements Filter {


    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user_info");

        String url = req.getRequestURI();
        System.out.println(url);

        if(user == null && url.equals("/board/write")){
            res.sendRedirect("/signin");
            return;
        }

        if(user != null && url.equals("/signin")){
            res.sendRedirect("/board/list");
            return;
        }

        chain.doFilter(request,response);

    }
}
