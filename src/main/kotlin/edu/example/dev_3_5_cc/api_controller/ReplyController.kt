package edu.example.dev_3_5_cc.api_controller

import edu.example.dev_3_5_cc.dto.reply.ReplyListDTO
import edu.example.dev_3_5_cc.dto.reply.ReplyRequestDTO
import edu.example.dev_3_5_cc.dto.reply.ReplyResponseDTO
import edu.example.dev_3_5_cc.dto.reply.ReplyUpdateDTO
import edu.example.dev_3_5_cc.service.ReplyLikeService
import edu.example.dev_3_5_cc.service.ReplyService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cc/reply")
class ReplyController(
    val replyService: ReplyService,
    private val replyLikeService: ReplyLikeService
) {
    // Reply 등록 - 확인 완료
    @PostMapping("/")
    @PreAuthorize("isAuthenticated()")
    fun createReply(@RequestBody @Valid replyRequestDTO: ReplyRequestDTO): ResponseEntity<ReplyResponseDTO> {
        val replyResponse = replyService.createReply(replyRequestDTO)
        return ResponseEntity.ok(replyResponse)
    }

    @PutMapping("/{replyId}")
    @PreAuthorize("isAuthenticated()")
    fun updateReply(
        @PathVariable("replyId") replyId: Long,
        @RequestBody @Valid replyUpdateDTO: ReplyUpdateDTO
    ): ResponseEntity<ReplyResponseDTO> {
        val replyResponse = replyService.updateReply(replyId, replyUpdateDTO.content)
        return ResponseEntity.ok(replyResponse)
    }

    @DeleteMapping("/{replyId}")
    @PreAuthorize("isAuthenticated()")
    fun deleteReply(@PathVariable("replyId") replyId: Long): ResponseEntity<Map<String, String>> {
        replyService.deleteReply(replyId)
        return ResponseEntity.ok(mapOf("message" to "Reply deleted"))
    }

    @GetMapping("/listByMember/{memberId}")
    @PreAuthorize("@securityUtil.getCurrentUser().memberId == #memberId or hasRole('ADMIN')")
    fun listByMemberId(@PathVariable("memberId") memberId: String): ResponseEntity<List<ReplyListDTO>> {
        val replies: List<ReplyListDTO> = replyService.listByMemberId(memberId)
        return ResponseEntity.ok(replies)
    }

    @GetMapping("/listByParent/{parentReplyId}")
    fun listByParentReplyId(@PathVariable("parentReplyId") parentReplyId: Long): ResponseEntity<List<ReplyListDTO>> {
        val childReplies: List<ReplyListDTO> = replyService.listByParentReplyId(parentReplyId)
        return ResponseEntity.ok(childReplies)
    }

    // 댓글에 좋아요 추가
    @PostMapping("/{replyId}/like")
    @PreAuthorize("isAuthenticated()")
    fun addLike(@PathVariable("replyId") replyId: Long): ResponseEntity<Map<String, String>> {
        replyLikeService.addLike(replyId)
        return ResponseEntity.ok(mapOf("message" to "Reply liked"))
    }

    // 댓글에 좋아요 취소
    @DeleteMapping("/{replyId}/like")
    @PreAuthorize("isAuthenticated()")
    fun removeLike(@PathVariable("replyId") replyId: Long): ResponseEntity<Map<String, String>> {
        replyLikeService.removeLike(replyId)
        return ResponseEntity.ok(mapOf("message" to "Reply like removed"))
    }

    // 댓글의 좋아요 수 조회
    @GetMapping("/{replyId}/like/count")
    fun getLikeCount(@PathVariable("replyId") replyId: Long): ResponseEntity<Map<String, Long>> {
        val likeCount = replyLikeService.getLikeCount(replyId)
        return ResponseEntity.ok(mapOf("likeCount" to likeCount))
    }

}