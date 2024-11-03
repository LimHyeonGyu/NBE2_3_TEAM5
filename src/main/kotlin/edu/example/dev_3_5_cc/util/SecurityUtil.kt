package edu.example.dev_3_5_cc.util

import edu.example.dev_3_5_cc.dto.userDetails.CustomUserDetails
import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.exception.AuthorizationException
import edu.example.dev_3_5_cc.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityUtil(
    private val memberRepository: MemberRepository
) {

    // Authentication 객체를 가져오는 메서드
    private fun getAuthentication(): Authentication {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null || !authentication.isAuthenticated) {
            throw AuthorizationException("인증 정보가 없습니다.")
        }
        return authentication
    }

    // 현재 인증된 사용자 정보 가져오기
    fun getCurrentUser(): Member {
        val authentication = getAuthentication()
        val principal = authentication.principal

        val memberId = when (principal) {
            is CustomUserDetails -> principal.getMemberId()
            is String -> principal
            else -> throw AuthorizationException("인증된 사용자 정보를 가져올 수 없습니다.")
        }

        return memberRepository.findByIdOrNull(memberId)
            ?: throw AuthorizationException("사용자를 찾을 수 없습니다.")
    }

    // 현재 사용자의 역할(Role) 가져오기
    fun getCurrentUserRole(): String {
        val authentication = getAuthentication()
        return authentication.authorities.firstOrNull()?.authority
            ?: throw AuthorizationException("권한 정보를 가져올 수 없습니다.")
    }

    // 현재 사용자가 작성자거나 관리자 인지 확인
    fun checkUserAuthorization(member: Member) {
        val currentUser = getCurrentUser()
        val isAuthorized = currentUser.memberId == member.memberId || getCurrentUserRole() == "ROLE_ADMIN"
        if (!isAuthorized) {
            throw AuthorizationException("작성자나 관리자만 해당 작업을 수행할 수 있습니다.")
        }
    }
}