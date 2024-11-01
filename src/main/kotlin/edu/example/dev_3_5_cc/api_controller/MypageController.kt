package edu.example.dev_3_5_cc.api_controller

import edu.example.dev_3_5_cc.dto.member.MemberResponseDTO
import edu.example.dev_3_5_cc.dto.member.MemberUpdateDTO
import edu.example.dev_3_5_cc.exception.MemberException
import edu.example.dev_3_5_cc.exception.MemberTaskException
import edu.example.dev_3_5_cc.service.MemberService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cc/mypage")
class MypageController(
    private val memberService: MemberService,
) {

    //-----------------------------------------회원 정보 수정-------------------------------------------------

    // 마이페이지 내에서 자신의 회원 정보 조회
    @GetMapping("/{memberId}")
    fun getMember(
        @PathVariable memberId: String
    ): ResponseEntity<MemberResponseDTO> {
        val member = memberService.read(memberId)
        return ResponseEntity.ok(member)
    }

    // 마이페이지 내에서 회원의 직접 정보 수정(role 수정 미포함)
    @PutMapping("/{memberId}")
    fun updateMember(
        @PathVariable memberId: String,
        @Validated @RequestBody memberUpdateDTO: MemberUpdateDTO
    ): ResponseEntity<MemberResponseDTO> {
        val response = memberService.modify(memberId, memberUpdateDTO)
        return ResponseEntity.ok(response)
    }

    // 마이페이지 내에서 회원 삭제(=탈퇴)
    @DeleteMapping("/{memberId}")
    fun deleteMember(@PathVariable memberId: String): ResponseEntity<Map<String, String>> {
        memberService.remove(memberId)
        return ResponseEntity.ok(mapOf("message" to "$memberId 정보 삭제 성공"))
    }

}
