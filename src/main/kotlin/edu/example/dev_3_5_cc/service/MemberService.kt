package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.member.MemberRequestDTO
import edu.example.dev_3_5_cc.dto.member.MemberResponseDTO
import edu.example.dev_3_5_cc.dto.member.MemberUpdateDTO
import edu.example.dev_3_5_cc.entity.KakaoProfile
import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.MemberImage
import edu.example.dev_3_5_cc.exception.MemberException
import edu.example.dev_3_5_cc.log
import edu.example.dev_3_5_cc.repository.MemberRepository
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class MemberService (
    private val memberRepository : MemberRepository,
    private val modelMapper: ModelMapper,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) {

    @Value("\${cos.key}")
    lateinit var cosKey: String

    fun register(memberRequestDTO: MemberRequestDTO): MemberResponseDTO {
        // MemberRequestDTO -> Member 엔티티로 매핑
        val member = modelMapper.map(memberRequestDTO, Member::class.java).apply {
            // 이미지와 역할 기본값 설정
            if (this.image == null) this.image = MemberImage("default_avatar.png")
            if (this.role == null) this.role = "USER"
        }


        // Member 엔티티를 DB에 저장
        val savedMember = memberRepository.save(member)

        // 저장된 엔티티를 MemberResponseDTO로 매핑하여 반환
        return modelMapper.map(savedMember, MemberResponseDTO::class.java)
    }

    fun read(memberId: String): MemberResponseDTO {
        return memberRepository.getMemberResponseDTO(memberId) ?: throw MemberException.NOT_FOUND.get()
    }

    // 📌둘 중🔼🔽 어느것이 성능이 더 좋은지??
    fun read2(memberId: String): MemberResponseDTO {
        val member = memberRepository.findById(memberId).orElseThrow { MemberException.NOT_FOUND.get() }
        return modelMapper.map(member, MemberResponseDTO::class.java)
    }

    fun modify(memberUpdateDTO: MemberUpdateDTO): MemberResponseDTO {
        val member = memberRepository.findByIdOrNull(memberUpdateDTO.memberId)
            ?: throw MemberException.NOT_FOUND.get()

        with(member) {
            // null이 아닌 필드만 수정
            memberUpdateDTO.email?.let { email = it }
            memberUpdateDTO.phoneNumber?.let { phoneNumber = it }
            memberUpdateDTO.name?.let { name = it }
            memberUpdateDTO.password?.let { password = it }
            memberUpdateDTO.sex?.let { sex = it }
            memberUpdateDTO.address?.let { address = it }

            // image가 null이면 기본 이미지 설정, 아니면 수정
            image = if (memberUpdateDTO.image == null) MemberImage("default_avatar.png")
            else MemberImage(memberUpdateDTO.image)
        }

        // 변경된 엔티티를 저장 후 MemberResponseDTO로 반환
        val updatedMember = memberRepository.save(member)
        return modelMapper.map(updatedMember, MemberResponseDTO::class.java)
    }

    fun remove(memberId: String) { // 📌강사님께 더 코틀린스럽게 바꿀 수 있는지 여쭈어보기
        if (!memberRepository.existsById(memberId)) {
            throw MemberException.NOT_FOUND.get()
        }
        memberRepository.deleteById(memberId)
        log.info("삭제 완료")
    }

    fun getList(): List<MemberResponseDTO> {
        // JPA 기본 메서드로 모든 Member 데이터 조회
        val memberList: List<Member?> = memberRepository.findAll()

        // Member -> MemberResponseDTO 매핑 후 리스트로 반환
        return memberList.map { member -> modelMapper.map(member, MemberResponseDTO::class.java) }
    }

    fun findOrRegisterKakaoMember(kakaoProfile: KakaoProfile): Member {
        val memberId = kakaoProfile.id.toString()
        // 가입자 체크
        val existingMemberResponse = memberRepository.findById(memberId)
        log.info("memberId 조회 결과: ${existingMemberResponse.isPresent}")
        return if (existingMemberResponse.isPresent) {
            // 이미 가입된 경우, 해당 멤버 반환
            log.info("기존 회원입니다--------------------")
            existingMemberResponse.get()
        } else {
            // 가입되지 않은 경우, 새로운 멤버 등록
            val encodedPassword = bCryptPasswordEncoder.encode(cosKey)
            val newMemberRequest = MemberRequestDTO().apply {
                this.memberId = memberId
                this.password = encodedPassword
            }
            // 새로운 회원 등록
            register(newMemberRequest)
            log.info("새로운 회원을 등록합니다--------------")
            // 새로 등록된 회원을 반환
            memberRepository.findById(memberId).get()
        }
    }

}

// password = bCryptPasswordEncoder.encode(memberRequestDTO.password)