package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.reply.ReplyListDTO
import edu.example.dev_3_5_cc.dto.reply.ReplyRequestDTO
import edu.example.dev_3_5_cc.dto.reply.ReplyResponseDTO
import edu.example.dev_3_5_cc.dto.reply.ReplyUpdateDTO
import edu.example.dev_3_5_cc.entity.QReply.reply
import edu.example.dev_3_5_cc.entity.Reply
import edu.example.dev_3_5_cc.exception.BoardException
import edu.example.dev_3_5_cc.exception.MemberException
import edu.example.dev_3_5_cc.exception.ReplyException
import edu.example.dev_3_5_cc.repository.BoardRepository
import edu.example.dev_3_5_cc.repository.MemberRepository
import edu.example.dev_3_5_cc.repository.ReplyRepository
import jakarta.transaction.Transactional
import org.modelmapper.ModelMapper
import org.modelmapper.PropertyMap
import org.modelmapper.builder.ConfigurableConditionExpression
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
@Transactional
class ReplyService(
    private val replyRepository: ReplyRepository,
    private val memberRepository: MemberRepository, // 얘네는 필요없는 것 같긴 한데 나중에 수정하면서 필요할 수도 있을 것 같아 냅뒀습니다.
    private val boardRepository: BoardRepository,   // 끝까지 필요없으면 나중에 지울 예정입니다
    private val modelMapper: ModelMapper
) {

    fun createReply(replyRequestDTO: ReplyRequestDTO) : ReplyResponseDTO{
        val member = memberRepository.findByIdOrNull(replyRequestDTO.memberId) ?: throw MemberException.NOT_FOUND.get()
        val board = boardRepository.findByIdOrNull(replyRequestDTO.boardId) ?: throw BoardException.NOT_FOUND.get()

        val reply = replyRequestDTO.toEntity(member, board)

        val savedReply = replyRepository.save(reply)
        return ReplyResponseDTO(savedReply)
    }

    // 이거 2차 프로젝트에도 통째로 주석처리 돼있길래 해뒀습니다
//    fun readReply(replyId : Long) : ReplyResponseDTO{
//        val reply = replyRepository.findByIdOrNull(replyId) ?: throw ReplyException.NOT_FOUND.get()
//
//        return ReplyResponseDTO(reply)
//    }

    fun updateReply(replyUpdateDTO: ReplyUpdateDTO) : ReplyResponseDTO{
        val reply = replyRepository.findByIdOrNull(replyUpdateDTO.replyId) ?: throw ReplyException.NOT_FOUND.get()

        // 이곳 언저리에 updateDTO에 넣은 memberId랑 로그인한 사용자가 똑같은지 확인하는 로직 넣어야 할 것 같아요
        // security 하면서 추가하면 됩니다

        try {
            with(reply){
                content = replyUpdateDTO.content
            }
        }catch(e: Exception){
            throw ReplyException.NOT_UPDATED.get()
        }

        return ReplyResponseDTO(reply)
    }

    fun deleteReply(replyId : Long){
        val reply = replyRepository.findByIdOrNull(replyId) ?: throw ReplyException.NOT_FOUND.get()

        try {
            replyRepository.delete(reply)
        }catch(e: Exception){
            throw ReplyException.NOT_DELETED.get()
        }
    }

    fun listByMemberId(memberId: String): List<ReplyListDTO>{
        val replies : List<Reply> = replyRepository.findAllByMember(memberId)

        if(replies.isEmpty()) throw ReplyException.NOT_FOUND.get()

        return replies.map { ReplyListDTO(it) }
    }

    // security 관련이라 일단 여기 있어야 한다고 명시만 해두겠습니다.
    // fun checkDeleteReplyAuthorization(replyId : Long)

    // listByBoard는 왜 통째로 주석처리 했었을까요? 기억이 안나네요


}