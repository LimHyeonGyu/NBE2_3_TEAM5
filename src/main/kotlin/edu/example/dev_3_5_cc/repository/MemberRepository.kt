package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member?, String?> {

//    fun findByMemberId(memberId: String?): Member?

}
