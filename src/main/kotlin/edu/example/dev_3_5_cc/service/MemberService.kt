package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.member.MemberRequestDTO
import edu.example.dev_3_5_cc.dto.member.MemberResponseDTO
import edu.example.dev_3_5_cc.dto.member.MemberUpdateDTO
import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.MemberImage
import edu.example.dev_3_5_cc.exception.MemberException
import edu.example.dev_3_5_cc.log
import edu.example.dev_3_5_cc.repository.MemberRepository
import org.modelmapper.ModelMapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberService (
    private val memberRepository : MemberRepository,
    private val modelMapper: ModelMapper,
//  private val bCryptPasswordEncoder: BCryptPasswordEncoder 비밀번호 암호화 설정 -> 📌JWT할 때 구현하기
) {

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

}

// password = bCryptPasswordEncoder.encode(memberRequestDTO.password)