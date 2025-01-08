package com.hong.test.user.controller;

import com.hong.test.user.model.dto.User;
import com.hong.test.user.model.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 로그임 폼
    @GetMapping("/login")
    public String loginForm() {
        return "/user/login";
    }

    // 회원가입 폼
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "/user/join";
    }

    // 회원가입 로직
    @PostMapping("/register")
    public String register(@Valid User user, BindingResult bindingResult, Model model, String userId, String userPw) {

        if (bindingResult.hasErrors()) {
            // 오류 메시지 처리
            model.addAttribute("errors", bindingResult.getFieldError().getDefaultMessage());
            return "/user/join";
        }

        // 이미 등록된 회원인지 검증하는 로직
        user = userService.findByUserId(user);

        if(user != null) {
            model.addAttribute("error", "이미 존재하는 회원입니다.");
            return "/user/join";
        }

        int result = userService.insertUser(userId, userPw);
        return "redirect:/";
    }

    // 로그인 로직
    @PostMapping("/login")
    public String login(String userPw, User user, Model model, HttpSession session, @RequestParam(required = false) String targetURL) {

        user = userService.findByUserId(user);

        // 아이디 검증
        if (user == null) {
            model.addAttribute("error", "아이디가 존재하지 않습니다.");
            return "/user/login"; // 다시 로그인 페이지로 이동 (에러 메시지 포함)
        }

        // 비밀번호 검증
        if (!user.getUserPw().equals(userPw)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "/user/login"; // 비밀번호 불일치 시 로그인 페이지로 이동
        }

        // 로그인 유지
        session.setAttribute("userId", user.getUserId());

        // 인터셉터를 통해 로그인 페이지로 리다이렉트 되었을 경우
        // 로그인 성공 시
        // 지정된 targetURL 리다이렉트 시킴
        if (targetURL != null && !targetURL.isEmpty()) {
            return "redirect:" + targetURL;
        }


        return "redirect:/";
    }

    // 로그아웃 처리
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("userId");
        return "redirect:/";
    }
}
