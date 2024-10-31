package edu.example.dev_3_5_cc.dto.member

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class MemberRequestDTO(
    @NotBlank(message = "회원 ID는 필수 항목입니다.")
    var memberId: String? = null,

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 항목입니다.")
    var email: String? = null,

    @NotBlank(message = "전화번호는 필수 항목입니다.")
    var phoneNumber: String? = null,

    @NotBlank(message = "이름은 필수 항목입니다.")
    var name: String? = null,

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    var password: String? = null,

    @NotBlank(message = "성별은 필수 항목입니다.")
    var sex: String? = null,

    @NotBlank(message = "주소는 필수 항목입니다.")
    var address: String? = null,

    var image: String? = null
)