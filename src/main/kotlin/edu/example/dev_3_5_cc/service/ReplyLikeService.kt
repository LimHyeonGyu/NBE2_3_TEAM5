package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.entity.ReplyLike
import edu.example.dev_3_5_cc.exception.ReplyException
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
    private val replyLikeRepository: ReplyLikeRepository,
    private val securityUtil: SecurityUtil
) {
    /**
     * 좋아요 추가 기능
     *
     * @param replyId 좋아요를 추가할 댓글 ID
     */
    fun addLike(replyId: Long) {
        val member = securityUtil.getCurrentUser()
        val reply = replyRepository.findByIdOrNull(replyId) ?: throw ReplyException.NOT_FOUND.get()

        // 이미 좋아요를 눌렀는지 확인
        if (replyLikeRepository.existsByReplyAndMember(reply, member)) {
            throw IllegalArgumentException("이미 좋아요를 눌렀습니다.")
        }

        val replyLike = ReplyLike(reply = reply, member = member)
        reply.addLike(replyLike) // likeCount 업데이트
        replyLikeRepository.save(replyLike)
    }

    /**
     * 좋아요 취소 기능
     *
     * @param replyId 좋아요를 취소할 댓글 ID
     */
    fun removeLike(replyId: Long) {
        val member = securityUtil.getCurrentUser()
        val reply = replyRepository.findByIdOrNull(replyId) ?: throw ReplyException.NOT_FOUND.get()

        val replyLike = replyLikeRepository.findByReplyAndMember(reply, member)
            ?: throw IllegalArgumentException("좋아요가 등록되어 있지 않습니다.")

        reply.removeLike(replyLike) // likeCount 업데이트
        replyLikeRepository.delete(replyLike)
    }

    /**
     * 특정 댓글의 좋아요 수 조회
     *
     * @param replyId 좋아요 수를 조회할 댓글 ID
     * @return 좋아요 수
     */
    fun getLikeCount(replyId: Long): Long {
        val reply = replyRepository.findByIdOrNull(replyId) ?: throw ReplyException.NOT_FOUND.get()
        return replyLikeRepository.countByReply(reply)
    }
}
