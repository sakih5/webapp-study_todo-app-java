package com.example.todoapp.util;

import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // 秘密鍵
    @Value("${jwt.secret}")
    private String secretKey;

    // トークン有効期限（ミリ秒）
    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    //
    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // トークン生成
    public String generateToken(String email) {
        SecretKey key = getSigningKey();
        return Jwts.builder()
                .subject(email)           // ペイロードにメールアドレス
                .issuedAt(new Date())     // 発行日時
                .expiration(new Date(System.currentTimeMillis() + expirationMs))  // 有効期限
                .signWith(key)            // 署名
                .compact();               // 文字列に変換
    }

    // メールアドレス取得
    public String getEmailFromToken(String token) {
        SecretKey key = getSigningKey();

        Claims claims = Jwts.parser() // JWTを 解析するための設定を組み立てるビルダー
                .verifyWith(key)      // この鍵で署名を検証しなさい
                .build()              // ここまでに設定した内容を固定化
                .parseSignedClaims(token) // JWTトークンを解析して正当か期限切れ/改ざん/不正形式か判断
                .getPayload();        // JWTの ペイロード部分（Claims）を取得

        return claims.getSubject(); // ペイロード部分のsubに登録してあるメールアドレスを取得
    }

    // トークン検証
    public boolean validateToken(String token) {
        try {
            SecretKey key = getSigningKey();
            Jwts.parser() // JWTを 解析するための設定を組み立てるビルダー
                    .verifyWith(key)      // この鍵で署名を検証しなさい
                    .build()              // ここまでに設定した内容を固定化
                    .parseSignedClaims(token); // JWTトークンを解析して正当か期限切れ/改ざん/不正形式か判断
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
