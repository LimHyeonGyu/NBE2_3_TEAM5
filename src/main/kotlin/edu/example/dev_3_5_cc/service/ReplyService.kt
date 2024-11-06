package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.reply.ReplyListDTO
import edu.example.dev_3_5_cc.dto.reply.ReplyRequestDTO
import edu.example.dev_3_5_cc.dto.reply.ReplyResponseDTO
import edu.example.dev_3_5_cc.dto.reply.ReplyUpdateDTO
import edu.example.dev_3_5_cc.entity.Reply
import edu.example.dev_3_5_cc.exception.BoardException
import edu.example.dev_3_5_cc.exception.JWTException
import edu.example.dev_3_5_cc.exception.MemberException
import edu.example.dev_3_5_cc.exception.ReplyException
import edu.example.dev_3_5_cc.repository.BoardRepository
import edu.example.dev_3_5_cc.repository.ReplyRepository
import edu.example.dev_3_5_cc.util.SecurityUtil
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
@Transactional
class ReplyService(
    private val replyRepository: ReplyRepository,
    private val boardRepository: BoardRepository,
    private val securityUtil: SecurityUtil
) {

    /**
     * 댓글 생성 메서드
     *
     * @param replyRequestDTO 댓글 생성 요청 DTO
     * @return 생성된 댓글의 응답 DTO
     */
    fun createReply(replyRequestDTO: ReplyRequestDTO): ReplyResponseDTO {
        // 현재 인증된 사용자 가져오기
        val member = securityUtil.getCurrentUser()

        // 게시판 존재 여부 확인
        val board = boardRepository.findByIdOrNull(replyRequestDTO.boardId)
            ?: throw BoardException.NOT_FOUND.get()

        // 부모 댓글이 지정된 경우, 부모 댓글 존재 여부 확인
        val parentReply = replyRequestDTO.parentReplyId?.let { parentId ->
            replyRepository.findByIdOrNull(parentId) ?: throw ReplyException.NOT_FOUND.get()
        }

        // 새로운 댓글 생성
        val reply = Reply(
            content = replyRequestDTO.content,
            member = member,
            board = board,
            parent = parentReply
        )

        // 댓글 저장
        val savedReply = replyRepository.save(reply)
        return ReplyResponseDTO(savedReply)
    }

    /**
     * 댓글 업데이트 메서드
     *
     * @param replyUpdateDTO 댓글 업데이트 요청 DTO
     * @return 업데이트된 댓글의 응답 DTO
     */
    fun updateReply(replyId: Long, content: String): ReplyResponseDTO {
        val reply = replyRepository.findByIdOrNull(replyId)
            ?: throw ReplyException.NOT_FOUND.get()

        // 권한 확인
        val currentUser = securityUtil.getCurrentUser()
        if (currentUser.memberId != reply.member?.memberId && securityUtil.getCurrentUserRole() != "ROLE_ADMIN") {
            throw JWTException("권한이 없습니다")
        }

        // 댓글 내용 업데이트
        reply.content = content

        return ReplyResponseDTO(reply)
    }

    /**
     * 댓글 삭제 메서드
     *
     * @param replyId 삭제할 댓글 ID
     */
    fun deleteReply(replyId: Long) {
        val reply = replyRepository.findByIdOrNull(replyId)
            ?: throw ReplyException.NOT_FOUND.get()

        // 권한 확인
        val currentUser = securityUtil.getCurrentUser()
        if (currentUser.memberId != reply.member?.memberId &&
            currentUser.memberId != reply.board?.member?.memberId &&
            securityUtil.getCurrentUserRole() != "ROLE_ADMIN"
        ) {
            throw JWTException("권한이 없습니다")
        }

        // 댓글 삭제
        replyRepository.delete(reply)
    }

    /**
     * 특정 사용자가 작성한 모든 댓글 조회
     *
     * @param memberId 작성자의 회원 ID
     * @return 작성자가 작성한 댓글 리스트
     */
    fun listByMemberId(memberId: String): List<ReplyListDTO> {
        val replies: List<Reply> = replyRepository.findAllByMember_MemberId(memberId)

        if (replies.isEmpty()) throw ReplyException.NOT_FOUND.get()

        return replies.map { ReplyListDTO(it) }
    }

    /**
     * 특정 게시판에 속한 모든 댓글 조회
     *
     * @param boardId 게시판 ID
     * @return 해당 게시판의 댓글 리스트
     */
    fun listByBoardId(boardId: Long): List<ReplyListDTO> {
        val replies: List<Reply> = replyRepository.findAllByBoard_BoardId(boardId)

        if (replies.isEmpty()) throw ReplyException.NOT_FOUND.get()

        return replies.map { ReplyListDTO(it) }
    }

    /**
     * 특정 부모 댓글에 속한 모든 자식 댓글(대댓글) 조회
     *
     * @param parentReplyId 부모 댓글 ID
     * @return 해당 부모 댓글의 자식 댓글 리스트
     */
    fun listByParentReplyId(parentReplyId: Long): List<ReplyListDTO> {
        val childReplies: List<Reply> = replyRepository.findAllByParent_ReplyId(parentReplyId)

        if (childReplies.isEmpty()) throw ReplyException.NOT_FOUND.get()

        return childReplies.map { ReplyListDTO(it) }
    }

    /**
     * 댓글 삭제 권한 확인 메서드
     *
     * @param replyId 삭제할 댓글 ID
     * @return 권한이 있는 경우 true
     */
    fun checkDeleteReplyAuthorization(replyId: Long): Boolean {
        val reply = replyRepository.findByIdOrNull(replyId) ?: throw ReplyException.NOT_FOUND.get()
        val currentUser = securityUtil.getCurrentUser()

        // 관리자이거나, 댓글 작성자이거나, 해당 댓글이 달린 게시판의 작성자인지 확인
        return if (currentUser.memberId == reply.member?.memberId ||
            currentUser.memberId == reply.board?.member?.memberId ||
            securityUtil.getCurrentUserRole() == "ROLE_ADMIN"
        ) {
            true
        } else {
            throw JWTException("권한이 없습니다")
        }
    }
}
