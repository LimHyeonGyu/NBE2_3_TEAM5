package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.entity.ReplyLike
import edu.example.dev_3_5_cc.exception.ReplyException
import edu.example.dev_3_5_cc.repository.MemberRepository
import edu.example.dev_3_5_cc.repository.ReplyLikeRepository
import edu.example.dev_3_5_cc.repository.ReplyRepository
import edu.example.dev_3_5_cc.util.SecurityUtil
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ReplyLikeService(
    private val replyRepository: ReplyRepository,
    private val memberRepository: MemberRepository,
    private val replyLikeRepository: ReplyLikeRepository,
    private val securityUtil: SecurityUtil
) {
    // 좋아요 추가 기능
    fun addLike(replyId: Long) {
        val member = securityUtil.getCurrentUser()
        val reply = replyRepository.findByIdOrNull(replyId) ?: throw ReplyException.NOT_FOUND.get()

        // 이미 좋아요를 눌렀는지 확인
        if (replyLikeRepository.existsByReplyAndMember(reply, member)) {
            throw IllegalArgumentException("이미 좋아요를 눌렀습니다.")
        }

        val replyLike = ReplyLike(reply = reply, member = member)
        replyLikeRepository.save(replyLike)
    }

    // 좋아요 취소 기능
    fun removeLike(replyId: Long) {
        val member = securityUtil.getCurrentUser()
        val reply = replyRepository.findByIdOrNull(replyId) ?: throw ReplyException.NOT_FOUND.get()

        val replyLike = replyLikeRepository.findByReplyAndMember(reply, member)
            ?: throw IllegalArgumentException("좋아요가 등록되어 있지 않습니다.")

        replyLikeRepository.delete(replyLike)
    }

    // 특정 댓글에 대한 좋아요 수 조회
    fun getLikeCount(replyId: Long): Long {
        val reply = replyRepository.findByIdOrNull(replyId) ?: throw ReplyException.NOT_FOUND.get()
        return replyLikeRepository.countByReply(reply)
    }
}