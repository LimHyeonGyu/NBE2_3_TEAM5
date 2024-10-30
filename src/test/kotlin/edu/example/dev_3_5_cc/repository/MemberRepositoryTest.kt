package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Member
import jakarta.persistence.EntityListeners
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.test.annotation.Commit
import kotlin.test.DefaultAsserter.assertEquals

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Test
    @Transactional
    @Commit
    fun testInsert(){
        for (i in 1..50){
            val member = Member().apply {
                memberId = "$i"
                email = "test$i@example.com"
                phoneNumber = "010-$i$i$i$i-$i$i$i$i"
                name = "user$i"
                password = "password$i"
                sex = "F"
                address = "address$i"
                role = "ADMIN$i"
            }

            memberRepository.save(member).run {
                assertNotNull(this)
            }
        }
    }
}