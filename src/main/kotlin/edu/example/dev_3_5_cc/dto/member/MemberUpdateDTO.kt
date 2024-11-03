package edu.example.dev_3_5_cc.dto.member

import jakarta.validation.constraints.Email


data class MemberUpdateDTO(
//  var memberId: String, // 📌수정할 때 필요 없는 것 같은데,,,이 필드 꼭 있어야 하는지 -> 없어도⭕

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    var email: String? = null,

    var phoneNumber: String? = null,
    var name: String? = null,
    var password: String? = null,
    var sex: String? = null,
    var address: String? = null,
    var role: String? = null,
    var image: String? = null
)