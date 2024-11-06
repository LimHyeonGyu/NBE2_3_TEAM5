package edu.example.dev_3_5_cc.dto.member

import edu.example.dev_3_5_cc.entity.Member
import edu.example.dev_3_5_cc.entity.MemberImage
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

data class MemberRequestDTO(
    @field:NotBlank(message = "회원 ID는 필수 항목입니다.")
    var memberId: String? = null,

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

    var image: String? = null
) {
    fun toEntity(bCryptPasswordEncoder: BCryptPasswordEncoder): Member = Member(
        memberId = memberId,
        email = email,
        phoneNumber = phoneNumber,
        name = name,
        password = bCryptPasswordEncoder.encode(password),
        sex = sex,
        address = address,
        image = image?.let { MemberImage(it) }
    )

}