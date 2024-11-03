package edu.example.dev_3_5_cc.dto.userDetails

import edu.example.dev_3_5_cc.entity.Member
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(private val member: Member) : UserDetails {
    // 사용자의 권한 반환
    override fun getAuthorities(): Collection<GrantedAuthority> = listOf(GrantedAuthority { member.role })

    override fun getPassword(): String = member.password ?: ""

    override fun getUsername(): String = member.name ?: ""

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true

    // 회원 ID 반환
    fun getMemberId(): String = member.memberId ?: ""
}