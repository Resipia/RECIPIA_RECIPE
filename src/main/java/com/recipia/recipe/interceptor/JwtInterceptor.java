package com.recipia.recipe.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        if (token == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Authorization token is missing");
            return false;
        }

        if (!TokenUtils.isValidToken(token, TokenUtils.ACCESS_TOKEN_TYPE)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token");
            return false;
        }

        // todo: 유효한 token일때 username, nickname 가져와져서 실제 api CONTROLLER나 SERVICE에서 username, nickname 사용하기



        return true;
    }

}
