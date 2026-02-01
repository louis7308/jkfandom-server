package io.proto.jkfandom_app_server.application.dto

import io.proto.jkfandom_app_server.domain.user.AppLanguage
import io.proto.jkfandom_app_server.domain.user.Gender
import java.time.LocalDate

data class OnboardingCommand(
    val language: AppLanguage,
    val nickname: String,
    val birthDate: LocalDate,
    val gender: Gender,
    val countryCode: String,
    val regionCode: String,
    val favoriteGenres: List<String>,
    val favoriteArtistIds: List<Long>,
    val topArtistId: Long,
    val agreements: Agreements
) {
    data class Agreements(
        val termsAccepted: Boolean,
        val privacyAccepted: Boolean,
        val marketingAccepted: Boolean = false
    )
}
