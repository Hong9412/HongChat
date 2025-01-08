package com.hong.test.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLEncoder;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userId = (String) request.getSession().getAttribute("userId");

        // 로그인 검증
        if (userId == null) {

            String errorMessage = "로그인 후 이용가능합니다.";

            // 요청된 URL 저장
            String targetURL = request.getRequestURI();
            String queryString = request.getQueryString();
            String fullURL = targetURL + (queryString != null ? "?" + queryString : "");

            response.sendRedirect("/login?error=" + URLEncoder.encode(errorMessage, "UTF-8") + "&targetURL=" + fullURL);
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 요청 후 마지막 작업
    }
}