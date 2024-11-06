package edu.example.dev_3_5_cc.entity

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoProfile(
    @JsonProperty("id") var id: Long? = null,
    @JsonProperty("connected_at") var connectedAt: String? = null,
    @JsonProperty("properties") var properties: Properties? = null,
    @JsonProperty("kakao_account") var kakaoAccount: KakaoAccount? = null
)

data class Properties(
    @JsonProperty("nickname") var nickname: String? = null,
    @JsonProperty("profile_image") var profileImage: String? = null,
    @JsonProperty("thumbnail_image") var thumbnailImage: String? = null
)

data class Profile(
    @JsonProperty("nickname") var nickname: String? = null,
    @JsonProperty("thumbnail_image_url") var thumbnailImageUrl: String? = null,
    @JsonProperty("profile_image_url") var profileImageUrl: String? = null,
    @JsonProperty("is_default_image") var isDefaultImage: Boolean? = null,
    @JsonProperty("is_default_nickname") var isDefaultNickname: Boolean? = null
)

data class KakaoAccount(
    @JsonProperty("profile_nickname_needs_agreement") var profileNicknameNeedsAgreement: Boolean? = null,
    @JsonProperty("profile_image_needs_agreement") var profileImageNeedsAgreement: Boolean? = null,
    @JsonProperty("profile") var profile: Profile? = null
)
