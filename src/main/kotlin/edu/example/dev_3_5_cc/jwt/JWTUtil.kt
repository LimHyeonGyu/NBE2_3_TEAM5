package edu.example.dev_3_5_cc.jwt

import edu.example.dev_3_5_cc.exception.JWTException
import edu.example.dev_3_5_cc.log
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Component
class JWTUtil( @Value("\${spring.jwt.secret}") secret: String ) {

    private val secretKey: SecretKey = SecretKeySpec(
        secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256"
    )
    //migration: SignatureAlgorithm.HS256.jcaName 오류로 인해 "HmacSHA256" 로 대체

    // MemberId 확인 메서드
    fun getMemberId(token: String): String? {
        val claims = Jwts.parser()
            .verifyWith(secretKey).build()
            .parseSignedClaims(token)
            .body
        return claims.get("memberId", String::class.java)
    }

    // role 확인 메서드
    fun getRole(token: String): String? {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token).body["role", String::class.java]
    }

    // 토큰의 만료 여부 확인
    fun isExpired(token: String): Boolean {
        return try {
            Jwts.parser()  // parser() 사용
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
            false // 유효한 토큰
        } catch (e: ExpiredJwtException) {
            true // 만료된 토큰
        } catch (e: JwtException) {
            log.error("JWTException 발생: ${e.message}")
            throw JWTException("유효하지 않은 토큰: ${e.message}")
        }
    }

    // AccessToken 생성
    fun createAccessToken(memberId: String, role: String, expiredMs: Long): String {
        return Jwts.builder()
            .claim("memberId", memberId)
            .claim("role", role)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiredMs * 1000))
            .signWith(secretKey)
            .compact()
    }

    // Refresh Token 생성
    fun createRefreshToken(expiredMs: Long): String {
        return Jwts.builder()
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiredMs)) // Refresh Token 만료 시간
            .signWith(secretKey)
            .compact()
    }

    // Refresh Token 으로 Access Token 재발급
    fun regenerateAccessToken(refreshToken: String, memberId: String, role: String, accessTokenExpiredMs: Long): String {
        if (isExpired(refreshToken)) {
            throw RuntimeException("Refresh Token 이 만료되었습니다.")
        }
        return createAccessToken(memberId, role, accessTokenExpiredMs)
    }

    fun regenerateRefreshToken(refreshToken: String, memberId: String, role: String, refreshTokenExpiredMs: Long): String {
        if (isExpired(refreshToken)) {
            throw RuntimeException("Refresh Token 이 만료되었습니다.")
        }
        return createRefreshToken(refreshTokenExpiredMs)
    }

}