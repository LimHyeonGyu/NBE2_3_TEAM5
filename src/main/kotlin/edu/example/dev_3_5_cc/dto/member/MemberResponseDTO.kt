package edu.example.dev_3_5_cc.dto.member

import edu.example.dev_3_5_cc.entity.Member
import java.time.LocalDateTime

data class MemberResponseDTO(
    var memberId: String? = null,
    var email: String? = null,
    var phoneNumber: String? = null,
    var name: String? = null,
    var sex: String? = null,
    var address: String? = null,
    var image: String? = null,
    var role: String? = null,
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null
)
//{
//    constructor(member: Member): this(
//        memberId = member.memberId,
//        email = member.email,
//        phoneNumber = member.phoneNumber,
//        name = member.name,
//        sex = member.sex,
//        address = member.address,
//        image = member.image?.filename,
//        role = member.role,
//        createdAt = member.createdAt,
//        updatedAt = member.updatedAt
//    )
//}
//
