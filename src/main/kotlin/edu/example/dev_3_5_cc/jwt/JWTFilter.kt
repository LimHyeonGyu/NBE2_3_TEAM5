package edu.example.dev_3_5_cc.jwt

import edu.example.dev_3_5_cc.dto.userDetails.CustomUserDetails
import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.log
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JWTFilter(
    private val jwtUtil: JWTUtil
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        var authorization = request.getHeader("Authorization")
        val refreshToken = request.getHeader("RefreshToken")

        // Authorization Header 가 null 이 아닐 경우에만 로그 출력
        authorization?.let { log.info("Access Token: $it") }
        refreshToken?.let { log.info("Refresh Token: $it") }


        // Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            val cookies = request.cookies
            cookies?.forEach { cookie ->
                when (cookie.name) {
                    "jwtToken" -> authorization = cookie.value
                }
            }
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                log.info("token null")
                filterChain.doFilter(request, response)
                return
            }
        }

        log.info("--authorization now--")
        val token = authorization.split(" ")[1].trim()

        // 토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {
            log.info("--token expired--")

            // refreshToken 유효 기간 검증 후 재생성
            if (refreshToken != null && !jwtUtil.isExpired(refreshToken)) {
                log.info("--refreshToken valid, regenerating accessToken--")

                val memberId = jwtUtil.getMemberId(refreshToken) ?: ""
                val role = jwtUtil.getRole(refreshToken) ?: ""
                val newAccessToken = jwtUtil.regenerateAccessToken(refreshToken, memberId, role, 1000 * 60 * 60 * 10L)
                val newRefreshToken = jwtUtil.regenerateRefreshToken(refreshToken, memberId, role, 1000 * 60 * 60 * 24 * 7L)

                response.addHeader("newAuthorization", "Bearer $newAccessToken")
                response.addHeader("newRefreshToken", newRefreshToken)

                request.setAttribute("newAccessToken", newAccessToken)
                request.setAttribute("newRefreshToken", newRefreshToken)
            } else {
                log.info("--no valid refreshToken, unauthorized--")
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                return
            }
        } else {
            val memberId = jwtUtil.getMemberId(token) ?: ""
            val role = jwtUtil.getRole(token) ?: ""

            val member = Member().apply {
                this.memberId = memberId
                this.password = "temppassword"
                this.role = role
            }

            val customUserDetails = CustomUserDetails(member)
            val userAuthentication: Authentication = UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.authorities
            )

            SecurityContextHolder.getContext().authentication = userAuthentication
        }

        filterChain.doFilter(request, response)
    }


}