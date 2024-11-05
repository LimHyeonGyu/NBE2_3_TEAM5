package edu.example.dev_3_5_cc.dto.board

import edu.example.dev_3_5_cc.dto.reply.ReplyResponseDTO
import edu.example.dev_3_5_cc.entity.Board
import edu.example.dev_3_5_cc.entity.Category
import java.io.Serializable
import java.time.LocalDateTime

data class BoardResponseDTO (
     var boardId: Long? = null,
     var title: String? = null,
     var description: String? = null,
     var category: Category = Category.GENERAL,
     var memberId: String? = null,
     var thumbnail: String? = null, // 썸네일 필드 추가
     var imageFilenames: List<String?>? = null, // 이미지 파일 이름들을 담을 리스트 추가
     var createdAt: LocalDateTime? = null,
     var updatedAt: LocalDateTime?= null,
     var replies: MutableList<ReplyResponseDTO>? = null,
     var viewCount: Int? = null
): Serializable {
    constructor(board: Board) :
            this(
                boardId = board.boardId,
                title = board.title,
                description = board.description,
                category = board.category ?: Category.GENERAL,
                memberId = board.member?.memberId,
                thumbnail = board.member?.image?.filename?.let { "s_$it" },  // 이미지가 있을 때만 s_붙여서 명시
                imageFilenames = board.images.map { it.filename }, // Map images to filenames
                createdAt = board.createdAt,
                updatedAt = board.updatedAt,
                replies = board.replies?.mapNotNull { it?.let{ReplyResponseDTO(it)} }?.toMutableList(),
                viewCount = board.viewCount
            )
}