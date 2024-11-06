package edu.example.dev_3_5_cc

import edu.example.dev_3_5_cc.entity.*
import edu.example.dev_3_5_cc.repository.BoardRepository
import edu.example.dev_3_5_cc.repository.MemberRepository
import edu.example.dev_3_5_cc.repository.ReplyRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TestTaskExcuter(
    private val memberRepository: MemberRepository,
    private val boardRepository: BoardRepository,
    private val replyRepository: ReplyRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) : ApplicationRunner {

    @Transactional
    override fun run(args: ApplicationArguments?) {
        // 멤버 추가
        for (i in 1..10) {
            val member = Member().apply {
                memberId = "user$i"
                password = bCryptPasswordEncoder.encode("1111")
                email = "user$i@user.com"
                phoneNumber = "010-1111-2222"
                name = "User$i"
                sex = if (i % 2 == 1) "MALE" else "FEMALE"
                address = "Address $i"
                role = if (i % 2 == 1) "ROLE_USER" else "ROLE_ADMIN"
                image = MemberImage("default_avatar.png")
                println("$i 번째 멤버 작성")
            }
            memberRepository.save(member)
        }

        // 게시글 추가
        for (i in 1..3) {
            val member = memberRepository.findByIdOrNull("user$i") // 각 게시글에 연결할 멤버
            val board = Board().apply {
                this.member = member
                title = "테스트 게시글 제목 $i"
                description = "이것은 테스트 게시글 내용 $i 입니다."
                category = Category.GENERAL
                println("$i 번째 게시글 작성")
            }
            boardRepository.save(board)
        }

        // 댓글 추가
        for (i in 1..3) {
            val member = memberRepository.findByIdOrNull("user${i + 1}") // 각 댓글에 연결할 멤버
            val board = boardRepository.findByIdOrNull(i.toLong()) // 각 댓글에 연결할 게시글
            val reply = Reply(content = "이것은 게시글 ${board?.boardId}에 대한 사용자 ${member?.memberId}의 댓글입니다.").apply {
                this.member = member
                this.board = board
                println("$i 번째 댓글 작성")
            }
            board?.replies?.add(reply) // 양방향 관계 설정
            replyRepository.save(reply) // 댓글 저장
        }
    }

}