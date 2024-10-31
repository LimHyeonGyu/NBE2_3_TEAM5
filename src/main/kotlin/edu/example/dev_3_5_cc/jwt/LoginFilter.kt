package edu.example.dev_3_5_cc.jwt

import edu.example.dev_3_5_cc.dto.userDetails.CustomUserDetails
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class LoginFilter(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JWTUtil
) : UsernamePasswordAuthenticationFilter() {

    // 입력받은 로그인 정보를 토큰으로 만들어 검증단계로 전달
    override fun attemptAuthentication(
        request: HttpServletRequest, response: HttpServletResponse
    ): Authentication {
        val memberId = request.getParameter("memberId")
        val password = obtainPassword(request)

        // 스프링 시큐리티에서 검증을 위한 토큰 생성
        val authToken = UsernamePasswordAuthenticationToken(memberId, password, null)

        return authenticationManager.authenticate(authToken)
    }

    // 로그인 성공 시 JWT 발급
    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authentication: Authentication
    ) {
        val customUserDetails = authentication.principal as CustomUserDetails
        val memberId = customUserDetails.getMemberId()

        // 권한 정보 가져오기 (단일 권한만 가정)
        val role = authentication.authorities.firstOrNull()?.authority ?: ""

        // Access Token 과 Refresh Token 생성
        val accessToken = jwtUtil.createAccessToken(memberId, role, 1000 * 60 * 60 * 10L) // 10시간 유효
        val refreshToken = jwtUtil.createRefreshToken(1000 * 60 * 60 * 24 * 7L) // 7일 유효

        // 응답 헤더에 토큰 추가
        response.addHeader("Authorization", "Bearer $accessToken")
        response.addHeader("RefreshToken", refreshToken)
    }

    // 로그인 실패 시 실행
    override fun unsuccessfulAuthentication(
        request: HttpServletRequest, response: HttpServletResponse, failed: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
    }
}