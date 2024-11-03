package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.member.MemberRequestDTO
import edu.example.dev_3_5_cc.dto.member.MemberUpdateDTO
import edu.example.dev_3_5_cc.exception.MemberTaskException
import edu.example.dev_3_5_cc.log
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestPropertySource(locations = ["classpath:application-test.properties"])
class MemberServiceTest {

    @Autowired
    lateinit var memberService: MemberService

//    @BeforeEach
//    fun init() {
//        for (i in 1L..10L) {
//            val member = MemberRequestDTO().apply {
//                memberId = "user$i"
//                email = "user$i@user$i.com"
//                phoneNumber = "010-1111-2222"
//                name = "user$i"
//                password = "user$i"
//                sex = if (i % 2 == 0L) "M" else "F"
//                address = "user$i"
//                image = null
//            }
//            memberService.register(member)
//        }
//    }

    @Test
    @Transactional
    @Order(1)
    fun register() {
        // GIVEN -> 테스트용 MemberRequestDTO 생성
        val member = MemberRequestDTO().apply {
            memberId = "user101"
            email = "user101@user101.com"
            phoneNumber = "010-1111-2222"
            name = "user101"
            password = "user101"
            sex = "F"
            address = "user101"
            image = null
        }
        log.info("image 확인용 회원 정보: $member")

        // WHEN -> memberService의 register 메서드 호출
        val memberResponseDTO = memberService.register(member)
        log.info("등록 후, 디폴트 값 지정된 회원 정보: $memberResponseDTO")

        // THEN -> 반환된 memberResponseDTO 값 검증
        memberResponseDTO.run {
            assertEquals("user101", memberId)
            assertEquals("default_avatar.png", image)
            assertEquals("USER", role)
            log.info("최종 등록된 회원 정보: $this")
        }
    }

    @Test
    @Transactional
    @Order(2)
    fun read() {
        val memberId = "user10"

        memberService.read(memberId).run {
            assertEquals("user10", this.memberId)
            log.info("조회된 회원 정보: $this")
        }
    }

    @Test
    @Transactional
    @Order(3)
    fun readFail() {
        // GIVEN -> 존재하지 않는 memberId
        val memberId = "user9999"

        // WHEN & THEN -> 예외 발생 여부 검증
        val exception = assertThrows<MemberTaskException> {
            memberService.read(memberId)
        }
        log.info("던지는 예외 확인: $exception")
    }

    @Test
    @Transactional
    @Order(4)
    fun remove() {
        try {
            val memberId = "user10"
            memberService.remove(memberId)
            memberService.read(memberId)
        } catch (e: MemberTaskException) {
            assertEquals(404, e.code)
            log.info("에러 코드: ${e.code}")
            assertEquals("NOT_FOUND(존재하지 않는 회원입니다)", e.message)
            log.info("에러 메시지: ${e.message}")
        }
    }

    @Test
    @Transactional
    @Order(5)
    fun removeFail() {
        // GIVEN -> 존재하지 않는 memberId
        val memberId = "user9999"

        // WHEN & THEN -> 삭제 시도 시 예외 발생 확인
        val exception = assertThrows<MemberTaskException> {
            memberService.remove(memberId)
        }
        log.info("예외: $exception")
    }

    @Test
    @Transactional
    @Order(6)
    fun modify() {
        val updatedMemberId = "user10"
        val updatedMember = MemberUpdateDTO(updatedMemberId).apply {
            email = "user101@user101.com"
            phoneNumber = "010-3333-4444"
            name = "user1111"
            password = "user1111"
            sex = "F"
            address = "user1111"
            image = null
        }

        // null로 지정된 image -> modify를 통해 default_avatar.png 지정됨
        log.info("아직 image = null 회원 정보: $updatedMember")

        val memberId = "user10"

        memberService.modify(memberId, updatedMember).run {
            assertEquals("user10", memberId)
            assertEquals("user1111", name)
            assertEquals("ROLE_ADMIN", role)
            assertEquals("default_avatar.png", image)
            log.info("수정된 회원 정보: $this")
        }
    }

    @Test
    @Transactional
    @Order(7)
    fun testGetList() {
        val memberList = memberService.getList().run {
            assertEquals("user1", this[0].memberId, "첫 번째 회원의 ID가 예상과 다름")
            assertEquals(10, this.size, "회원 리스트 크기가 예상과 다름")
            log.info("회원 정보 전체 조회")

            // 인자가 1개라서(MemberResponseDTO) "it" 사용 가능
            this.forEach { log.info("$it") }
        }
    }

}