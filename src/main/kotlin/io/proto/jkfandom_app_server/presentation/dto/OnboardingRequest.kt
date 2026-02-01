package io.proto.jkfandom_app_server.presentation.dto

import io.proto.jkfandom_app_server.application.dto.OnboardingCommand
import io.proto.jkfandom_app_server.domain.user.AppLanguage
import io.proto.jkfandom_app_server.domain.user.Gender
import jakarta.validation.Valid
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class OnboardingRequest(

    /** 앱 표시 언어 */
    @field:NotNull(message = "language는 필수입니다")
    val language: AppLanguage,

    /** 앱 표시 언어 */
    @field:NotNull(message = "nickname은 필수입니다")
    @field:Size(min = 1, max = 12, message = "nickname은 최대 12자리까지 입니다.")
    val nickname: String,

    /** 생년월일 */
    @field:NotNull(message = "birthDate는 필수입니다")
    val birthDate: LocalDate,

    /** 성별 */
    @field:NotNull(message = "gender는 필수입니다")
    val gender: Gender,

    /** 국가 (ISO-3166-1 alpha-2 권장: JP, KR 등) */
    @field:NotBlank(message = "countryCode는 필수입니다")
    @field:Size(min = 2, max = 2, message = "countryCode는 2자리여야 합니다 (예: JP)")
    val countryCode: String,

    /** 지역 코드 */
    @field:NotBlank(message = "regionCode는 필수입니다")
    @field:Size(max = 10, message = "regionCode는 최대 10자입니다")
    val regionCode: String,

    /** 선호 장르 (최소 1개 ~ 최대 10개 권장) */
    @field:NotEmpty(message = "favoriteGenres는 최소 1개 이상 선택해야 합니다")
    @field:Size(max = 10, message = "favoriteGenres는 최대 10개까지 선택 가능합니다")
    val favoriteGenres: List<@NotBlank String>,

    /** 마이 스타 아티스트 IDs */
    @field:NotEmpty(message = "favoriteArtistIds는 최소 1개 이상 선택해야 합니다")
    val favoriteArtistIds: List<Long>,

    /** 최애 아티스트 ID */
    @field:NotNull(message = "topArtistId는 필수입니다")
    val topArtistId: Long,

    /** 약관 동의 */
    @field:Valid
    @field:NotNull(message = "agreements는 필수입니다")
    val agreements: AgreementsRequest
) {
    data class AgreementsRequest(
        /** 이용약관 동의(필수) */
        @field:AssertTrue(message = "termsAccepted는 필수 동의입니다")
        val termsAccepted: Boolean,

        /** 개인정보 처리방침 동의(필수) */
        @field:AssertTrue(message = "privacyAccepted는 필수 동의입니다")
        val privacyAccepted: Boolean,

        /** 마케팅 수신 동의(선택) */
        val marketingAccepted: Boolean = false
    )

    fun toCommand(): OnboardingCommand =
        OnboardingCommand(
            language = language,
            nickname = nickname,
            birthDate = birthDate,
            gender = gender,
            countryCode = countryCode,
            regionCode = regionCode,
            favoriteGenres = favoriteGenres,
            favoriteArtistIds = favoriteArtistIds,
            topArtistId = topArtistId,
            agreements = OnboardingCommand.Agreements(
                termsAccepted = agreements.termsAccepted,
                privacyAccepted = agreements.privacyAccepted,
                marketingAccepted = agreements.marketingAccepted
            )
        )
}
