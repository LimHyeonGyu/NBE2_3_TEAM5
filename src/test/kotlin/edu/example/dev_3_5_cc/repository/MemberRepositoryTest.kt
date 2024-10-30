package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.MemberImage
import edu.example.dev_3_5_cc.log
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@TestPropertySource(locations = ["classpath:application-test.properties"])
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MemberRepositoryTest {

    @Autowired
    lateinit var memberRepository: MemberRepository

    @BeforeEach
    fun init() {
        for (i in 1L..100L) {
            val member = Member().apply {
                memberId = "user$i"
                email = "user$i@user$i.com"
                phoneNumber = "010-1111-2222"
                name = "user$i"
                password = "user$i"
                sex = if (i % 2 == 0L) "M" else "F"
                address = "user$i"
                role = if (i % 10 == 0L) "ADMIN" else "USER"
                image = image ?: MemberImage("default_avatar.png")
            }

            memberRepository.save(member)
        }
    }

    @Test
    @Order(1)
    @Transactional
    fun testInsert() {
        // GIVEN -> 테스트를 위한 Member 객체 생성
        val member = Member().apply {
            memberId = "user101"
            email = "user101@user101.com"
            phoneNumber = "010-1111-2222"
            name = "user101"
            password = "user101"
            sex = "F"
            address = "user101"
            role = "USER"
            image = image ?: MemberImage("default_avatar.png")
        }

        // WHEN -> Member 엔티티 저장
        memberRepository.save(member).run {
            // THEN -> 저장 후 ID가 null이 아닌지 확인
            assertNotNull(this)
            log.info("", this)
        }
    }

    @Test
    @Order(2)
    fun testGetMember() {
        // GIVEN -> 조회할 memberId 설정
        val memberId = "user10"

        // WHEN -> memberId를 이용하여 Member 엔티티 조회
        val member = memberRepository.findByMemberId(memberId) ?: throw NoSuchElementException()
        member.run {
            // THEN -> 조회된 Member의 ID가 기대한 값과 일치하는지 확인
            assertEquals(memberId, this.memberId)
            log.info(this)
        }
    }

    @Test
    @Order(4)
    fun testFindByIdFailure() {
        // GIVEN -> 존재하지 않는 memberId 설정
        val memberId = "userNoExist"

        // WHEN & THEN -> 조회 실패 시 예외가 발생하는지 확인
        assertThrows<NoSuchElementException> {
            memberRepository.findByIdOrNull(memberId) ?: throw NoSuchElementException()
        }
    }

    @Test
    @Order(5)
    @Transactional
    fun testUpdateMember() {
        // GIVEN -> 업데이트할 memberId 설정 및 해당 Member 조회
        val memberId = "user10"
        val member = memberRepository.findByMemberId(memberId) ?: throw NoSuchElementException()

        // WHEN -> Member의 email과 address 정보 업데이트
        member.apply {
            email = "updated_user10@user10.com"
            address = "updated_user10"
        }
        memberRepository.save(member)

        // THEN -> 업데이트된 정보가 올바른지 검증
        val updatedMember = memberRepository.findByMemberId(memberId) ?: throw NoSuchElementException()
        updatedMember.run {
            assertEquals("updated_user10@user10.com", email)
            assertEquals("updated_user10", address)
        }
    }

    @Test
    @Transactional
    @Order(6)
    fun testDelete() {
        // GIVEN -> 삭제할 memberId 설정
        val memberId = "user10"

        // WHEN -> Member 삭제
        memberRepository.deleteById(memberId)

        // THEN -> 삭제된 Member를 조회할 때 예외가 발생하는지 확인
        assertThrows<NoSuchElementException> {
            memberRepository.findByIdOrNull(memberId) ?: throw NoSuchElementException()
        }
    }

    @Test
    @Order(7)
    fun testFindAll() {
        // WHEN -> memberRepository에서 모든 멤버를 조회
        val memberList = memberRepository.findAll()

        // THEN -> memberList가 비어있지 않고, 첫 번째 멤버의 memberId가 예상값과 일치하는지 검증
        memberList.run {
            assertNotNull(this)
            assertEquals("user1", this[0]?.memberId, "첫 번째 memberID가 예상값과 다름")
        }

        memberList.forEach { println(it) }
    }

}
