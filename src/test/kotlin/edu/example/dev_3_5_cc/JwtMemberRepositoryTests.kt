package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Member
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootTest
class JwtMemberRepositoryTests {
    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder

    @Test
    fun create() { // JWT 테스트를 위해 패스워드가 암호화된 Member 객체 생성
        val member = Member(
            memberId = "tester1",
            password = bCryptPasswordEncoder.encode("1111"),
            email = "tester1@abc.com",
            phoneNumber = "12345678",
            name = "테스터1",
            sex = "MALE",
            address = "tester address",
            role = "ROLE_USER"
        )
        memberRepository.save(member)

        val member2 = Member(
            memberId = "tester2",
            password = bCryptPasswordEncoder.encode("1111"),
            email = "tester2@abc.com",
            phoneNumber = "12345678",
            name = "테스터2",
            sex = "FEMALE",
            address = "tester address",
            role = "ROLE_ADMIN"
        )
        memberRepository.save(member2)
    }
}