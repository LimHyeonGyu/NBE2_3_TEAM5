package edu.example.dev_3_5_cc.dto.member

import jakarta.validation.constraints.Email


data class MemberUpdateDTO(
    var memberId: String,

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    var email: String? = null,

    var phoneNumber: String? = null,
    var name: String? = null,
    var password: String? = null,
    var sex: String? = null,
    var address: String? = null,
    var image: String? = null
)