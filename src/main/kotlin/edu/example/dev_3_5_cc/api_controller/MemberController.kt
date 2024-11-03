package edu.example.dev_3_5_cc.api_controller

import edu.example.dev_3_5_cc.dto.member.MemberRequestDTO
import edu.example.dev_3_5_cc.dto.member.MemberResponseDTO
import edu.example.dev_3_5_cc.log
import edu.example.dev_3_5_cc.service.MemberService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cc/member")
class MemberController {

    @Autowired
    lateinit var memberService: MemberService

    @PostMapping // 회원 생성
    fun registerMember(@Validated @RequestBody memberRequestDTO: MemberRequestDTO):
            ResponseEntity<MemberResponseDTO> {
        log.info("등록된 회원 정보: $memberRequestDTO")
        return ResponseEntity.ok(memberService.register(memberRequestDTO))
    }

}