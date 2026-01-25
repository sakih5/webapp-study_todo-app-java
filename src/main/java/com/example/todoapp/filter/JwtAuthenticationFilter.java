package com.example.todoapp.filter;

import java.io.IOException;
import java.util.Collections;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.todoapp.util.JwtUtil;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        System.out.println("=== JwtAuthenticationFilter ===");
        System.out.println("Authorization header: " + request.getHeader("Authorization"));

        // ステップ1: Authorizationヘッダーを取得
        String authHeader = request.getHeader("Authorization");

        //  ステップ2: ヘッダーの確認とトークン抽出
        // Authorizationヘッダーがない、または"Bearer "で始まらない場合はスキップ
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer "の後ろのトークン部分を抽出（7文字目以降）
        String token = authHeader.substring(7);

        System.out.println("Token: " + token);
        boolean isValid = jwtUtil.validateToken(token);
        System.out.println("Token valid: " + isValid);

        // ステップ3: トークン検証と認証情報の設定
        // トークンが有効かチェック
        if (jwtUtil.validateToken(token)) {
              // トークンからメールアドレスを取得
              String email = jwtUtil.getEmailFromToken(token);
              System.out.println("Email: " + email);

              // 認証情報を作成してSecurityContextに設定
              UsernamePasswordAuthenticationToken authentication =
                  new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
              SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 次のフィルターへ
        filterChain.doFilter(request, response);
    }
}
