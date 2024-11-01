package edu.example.dev_3_5_cc.api_controller

import edu.example.dev_3_5_cc.dto.member.MemberResponseDTO
import edu.example.dev_3_5_cc.dto.member.MemberUpdateDTO
import edu.example.dev_3_5_cc.service.MemberService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cc/admin")
class AdminController(
    private val memberService: MemberService,
) {

    //-----------------------------------------회원 관리-----------------------------------------------------

    // 관리자의 회원 전체 조회
    @GetMapping("/memberList") // 📌l을 대문자 L로 바꿨다고 현규님께 말씀드리기
    fun getAllMembers(): ResponseEntity<List<MemberResponseDTO>> {
        val memberList = memberService.getList()
        return ResponseEntity.ok(memberList)
    }

    // 관리자의 단일 회원 조회
    @GetMapping("/{memberId}")
    fun getMember(
        @PathVariable memberId: String
    ): ResponseEntity<MemberResponseDTO> {
        val member = memberService.read(memberId)
        return ResponseEntity.ok(member)
    }

    // 관리자의 회원 정보 수정(role 수정 포함)
    @PutMapping("/{memberId}")
    fun updateMember(
        @PathVariable memberId: String,
        @Validated @RequestBody memberUpdateDTO: MemberUpdateDTO
    ): ResponseEntity<MemberResponseDTO> {
        val response = memberService.adminModify(memberId, memberUpdateDTO)
        return ResponseEntity.ok(response)
    }

    // 관리자의 회원 삭제 -> 새로운 코드✨
    @DeleteMapping("/{memberId}")
    fun deleteMember(@PathVariable memberId: String): ResponseEntity<Map<String, String>> {
        memberService.adminRemove(memberId)
        return ResponseEntity.ok(mapOf("message" to "$memberId 정보 삭제 성공"))
    }

}