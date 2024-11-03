package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.userDetails.CustomUserDetails
import edu.example.dev_3_5_cc.repository.MemberRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailService(
    private val memberRepository: MemberRepository
) : UserDetailsService {
    // loadUserByUsername 은 Username 이 아닌 memberId 로 DB 에서 조회
    override fun loadUserByUsername(memberId: String): UserDetails {
        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw UsernameNotFoundException("User not found with memberId: $memberId")

        return CustomUserDetails(member)
    }
}