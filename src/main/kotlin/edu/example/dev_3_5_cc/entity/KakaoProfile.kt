package edu.example.dev_3_5_cc.entity

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName


data class KakaoProfile (
    @JsonProperty("id"            ) var id           : Long?          = null,
    @JsonProperty("connected_at"  ) var connected_at  : String?       = null,
    @JsonProperty("properties"    ) var properties   : Properties?   = Properties(),
    @JsonProperty("kakao_account" ) var kakao_account : KakaoAccount? = KakaoAccount()
)

data class Properties (
    @JsonProperty("nickname"        ) var nickname       : String? = null,
    @JsonProperty("profile_image"   ) var profile_image   : String? = null,
    @JsonProperty("thumbnail_image" ) var thumbnail_image : String? = null
)

data class Profile (
    @JsonProperty("nickname"            ) var nickname          : String?  = null,
    @JsonProperty("thumbnail_image_url" ) var thumbnail_image_url : String?  = null,
    @JsonProperty("profile_image_url"   ) var profile_image_url   : String?  = null,
    @JsonProperty("is_default_image"    ) var is_default_image    : Boolean? = null,
    @JsonProperty("is_default_nickname" ) var is_default_nickname : Boolean? = null
)

data class KakaoAccount (
    @JsonProperty("profile_nickname_needs_agreement" ) var profile_nickname_needs_agreement : Boolean? = null,
    @JsonProperty("profile_image_needs_agreement"    ) var profile_image_needs_agreement    : Boolean? = null,
    @JsonProperty("profile"                          ) var profile                       : Profile? = Profile()
)