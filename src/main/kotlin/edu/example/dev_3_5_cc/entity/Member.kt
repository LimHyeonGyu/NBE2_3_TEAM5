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
    val memberId: String? = null,

    var email: @Email String? = null,
    var phoneNumber: String? = null,
    var name: String? = null,
    var password: String? = null,
    val sex: String? = null,
    var address: String? = null,
    var role: String? = null,

    @CreatedDate
    val createdAt: LocalDateTime? = null,

    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,

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

    fun clearImage() { //
        this.image = null
    }

    fun changeEmail(email: String?) {
        this.email = email
    }

    fun changePhoneNumber(phoneNumber: String?) {
        this.phoneNumber = phoneNumber
    }

    fun changeName(name: String?) {
        this.name = name
    }

    fun changePassword(password: String?) {
        this.password = password
    }

    fun changeAddress(address: String?) {
        this.address = address
    }

    fun changeRole(role: String?) {
        this.role = role
    }

}
