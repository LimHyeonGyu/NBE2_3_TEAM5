package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.dto.member.MemberResponseDTO
import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.exception.MemberException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MemberRepository : JpaRepository<Member?, String?> {
    fun findByMemberId(memberId: String?): Member?

    @Query("""
            SELECT new edu.example.dev_3_5_cc.dto.member.MemberResponseDTO(
            m.memberId, m.email, m.phoneNumber, m.name, m.sex, 
            m.address, m.image.filename, m.role, m.createdAt, m.updatedAt)
            FROM Member m WHERE m.memberId = :memberId
           """)
    fun getMemberResponseDTO(@Param("memberId") memberId: String): MemberResponseDTO?
}
