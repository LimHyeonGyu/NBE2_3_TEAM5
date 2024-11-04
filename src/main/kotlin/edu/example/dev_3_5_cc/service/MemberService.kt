package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.member.MemberRequestDTO
import edu.example.dev_3_5_cc.dto.member.MemberResponseDTO
import edu.example.dev_3_5_cc.dto.member.MemberUpdateDTO
import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.MemberImage
import edu.example.dev_3_5_cc.exception.MemberException
import edu.example.dev_3_5_cc.log
import edu.example.dev_3_5_cc.repository.MemberRepository
import org.hibernate.query.sqm.tree.SqmNode.log
import org.modelmapper.ModelMapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberService (
    private val memberRepository : MemberRepository,
    private val modelMapper: ModelMapper,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) {

    fun register(memberRequestDTO: MemberRequestDTO): MemberResponseDTO {
        if (memberRepository.findByIdOrNull(memberRequestDTO.memberId) != null) {
            throw MemberException.DUPLICATE.get()
        }

        val member = memberRequestDTO.toEntity(bCryptPasswordEncoder)
        log.info("날짜, role, image  값 없는 회원 정보 = $member")
        if (member.role == null) { member.role = "ROLE_USER" }
        if (member.image == null) { member.image = MemberImage("default_avatar.png") }

        val savedMember = memberRepository.save(member)
        log.info("날짜, role, image 값 있는 회원 정보 = $savedMember")

        return MemberResponseDTO(savedMember).also { log.info("image 값 변환된 회원 정보: $it") }
    }

    fun read(memberId: String): MemberResponseDTO {
        return memberRepository.getMemberResponseDTO(memberId) ?: throw MemberException.NOT_FOUND.get()
    }

    // 📌둘 중🔼🔽 어느것이 성능이 더 좋은지?? -> 🔼 성능 우수
    fun read2(memberId: String): MemberResponseDTO {
        val member = memberRepository.findById(memberId).orElseThrow { MemberException.NOT_FOUND.get() }
        return MemberResponseDTO(member!!)
    }

    fun modify(memberId: String, memberUpdateDTO: MemberUpdateDTO): MemberResponseDTO {
        val member = memberRepository.findByIdOrNull(memberId) ?: throw MemberException.NOT_FOUND.get()

        with(member) {
            // null이 아닌 필드만 수정
            memberUpdateDTO.email?.let { email = it }
            memberUpdateDTO.phoneNumber?.let { phoneNumber = it }
            memberUpdateDTO.name?.let { name = it }
            memberUpdateDTO.password?.let { password = it }
            memberUpdateDTO.sex?.let { sex = it }
            memberUpdateDTO.address?.let { address = it }

            image = if (memberUpdateDTO.image == null) MemberImage("default_avatar.png")
            else MemberImage(memberUpdateDTO.image)
        }

        val updatedMember = memberRepository.save(member)
        return MemberResponseDTO(updatedMember).also { log.info("최종 수정된 회원 정보: $it") }
    }

    fun adminModify(memberId: String, memberUpdateDTO: MemberUpdateDTO): MemberResponseDTO {
        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw MemberException.NOT_FOUND.get()

        with(member) {
            // null이 아닌 필드만 수정
            memberUpdateDTO.email?.let { email = it }
            memberUpdateDTO.phoneNumber?.let { phoneNumber = it }
            memberUpdateDTO.name?.let { name = it }
            memberUpdateDTO.password?.let { password = it }
            memberUpdateDTO.sex?.let { sex = it }
            memberUpdateDTO.address?.let { address = it }
            memberUpdateDTO.role?.let { role = it } // 관리자만 수정 가능한 필드

            image = if (memberUpdateDTO.image == null) MemberImage("default_avatar.png")
            else MemberImage(memberUpdateDTO.image)
        }

        val updatedMember = memberRepository.save(member)
        return MemberResponseDTO(updatedMember).also { log.info("관리자가 수정한 회원 정보: $it") }
    }


    fun remove(memberId: String) {
        if (!memberRepository.existsById(memberId)) {
            throw MemberException.NOT_FOUND.get()
        }
        memberRepository.deleteById(memberId)
        log.info("유저에 의해 삭제 완료: $memberId")
    }

    // 📌둘 중🔼🔽 어느것이 성능이 더 좋은지?? -> 테스트 해보기
    fun adminRemove(memberId: String) {
        val member = memberRepository.findByIdOrNull(memberId)
            ?: throw MemberException.NOT_FOUND.get()

        memberRepository.delete(member)
        log.info("관리자에 의해 회원 삭제 완료: $memberId")
    }


    fun getList(): List<MemberResponseDTO> {
        // JPA 기본 메서드로 모든 Member 데이터 조회
        val memberList: List<Member?> = memberRepository.findAll()

        // Member -> MemberResponseDTO 매핑 후 리스트로 반환
        return memberList.map { member -> modelMapper.map(member, MemberResponseDTO::class.java) }
    }

}



//// 에러 나는 코드들 ////

//        // MemberRequestDTO -> Member 엔티티로 매핑
//        val member = modelMapper.map(memberRequestDTO, Member::class.java).apply {
//            // 이미지와 역할 기본값 설정
//            if (this.image == null) this.image = MemberImage("default_avatar.png")
//            if (this.role == null) this.role = "USER"
//        }

//        return modelMapper.map(savedMember, MemberResponseDTO::class.java)