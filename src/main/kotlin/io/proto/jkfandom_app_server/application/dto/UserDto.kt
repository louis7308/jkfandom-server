package io.proto.jkfandom_app_server.application.dto

import io.proto.jkfandom_app_server.domain.user.AppLanguage
import io.proto.jkfandom_app_server.domain.user.Gender
import io.proto.jkfandom_app_server.domain.user.User
import io.proto.jkfandom_app_server.domain.user.UserStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime

@Schema(description = "유저 정보 응답")
data class UserResponse(

    @field:Schema(description = "유저 UUID", example = "b6d3f7b1-7c67-4c7e-a4e0-3b2c83f9c7c1")
    val uuid: String,

    @field:Schema(description = "Firebase UID", example = "kakao:1234567890")
    val firebaseUid: String,

    @field:Schema(description = "이메일", example = "user@example.com", nullable = true)
    val email: String?,

    @field:Schema(description = "닉네임", example = "홍길동")
    val nickname: String,

    @field:Schema(description = "유저 상태", example = "ACTIVE")
    val status: UserStatus,

    // 온보딩/프로필 필드들(네 createUser에서 받는 값 기준)
    @field:Schema(description = "언어 설정", example = "JA")
    val language: AppLanguage?,

    @field:Schema(description = "생년월일", example = "1999-03-21", nullable = true)
    val birthDate: LocalDate?,

    @field:Schema(description = "성별", example = "FEMALE", nullable = true)
    val gender: Gender?,

    @field:Schema(description = "국가 코드(대문자)", example = "JP")
    val countryCode: String?,

    @field:Schema(description = "지역 코드", example = "JP-13", nullable = true)
    val regionCode: String?,

    @field:Schema(description = "마이 스타 아티스트 IDs", example = "[1,2]")
    val favoriteArtistIds: List<Long>,

    @field:Schema(description = "최애 아티스트 ID", example = "1", nullable = true)
    val topArtistId: Long?,

    @field:Schema(description = "가입 일시", example = "2026-01-26T21:00:00")
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(user: User): UserResponse =
            UserResponse(
                uuid = user.uuid,
                firebaseUid = user.firebaseUid,
                email = user.email,
                nickname = user.nickname,
                status = user.status,

                language = user.language,
                birthDate = user.birthDate,
                gender = user.gender,
                countryCode = user.countryCode,
                regionCode = user.regionCode,
                favoriteArtistIds = user.favoriteArtistIds,
                topArtistId = user.topArtistId,

                createdAt = user.createdAt
            )
    }
}