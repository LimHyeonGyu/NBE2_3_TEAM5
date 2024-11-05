package edu.example.dev_3_5_cc.dto.member

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank


data class MemberUpdateDTO(
//  var memberId: String, // 📌수정할 때 필요 없는 것 같은데,,,이 필드 꼭 있어야 하는지 -> 없어도⭕

    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    @field:NotBlank(message = "이메일은 필수 항목입니다.")
    var email: String? = null,

    @field:NotBlank(message = "전화번호는 필수 항목입니다.")
    var phoneNumber: String? = null,

    @field:NotBlank(message = "이름은 필수 항목입니다.")
    var name: String? = null,

    @field:NotBlank(message = "비밀번호는 필수 항목입니다.")
    var password: String? = null,

    @field:NotBlank(message = "성별은 필수 항목입니다.")
    var sex: String? = null,

    @field:NotBlank(message = "주소는 필수 항목입니다.")
    var address: String? = null,

    var role: String? = null,
    var image: String? = null
)