package edu.example.dev_3_5_cc.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@Entity
data class Member (
    @Id
    var memberId: String? = null,

    @Email
    var email:String? = null,

    var phoneNumber: String? = null,
    var name: String? = null,
    var password: String? = null,
    var sex: String? = null,
    var address: String? = null,
    var role: String? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(
            name = "filename",
            column = Column(name = "member_image")
        )
    )
    var image: MemberImage? = null

) {
    fun addImage(memberImage: MemberImage?) {
        this.image = memberImage
    }

    fun clearImage() {
        this.image = MemberImage("default_avatar.png")
    }

}