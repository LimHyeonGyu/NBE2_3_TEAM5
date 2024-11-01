package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.Orders
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Orders, Long> {

    fun findByMember(member: Member?): List<Orders?>?

}
