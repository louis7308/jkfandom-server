package io.proto.jkfandom_app_server.presentation.controller

import io.proto.jkfandom_app_server.application.usecase.OnboardingCatalogService
import io.proto.jkfandom_app_server.application.usecase.UserService
import io.proto.jkfandom_app_server.shared.response.ApiFandomResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(
    name = "Onboarding",
    description = "온보딩용 기준 데이터/검증 API"
)
@RestController
@RequestMapping("/v1/onboarding")
class OnboardingController(
    private val catalogService: OnboardingCatalogService,
    private val userService: UserService
) {

    data class NicknameValidateRequest(val nickname: String)
    data class NicknameValidateResponse(val available: Boolean, val reason: String? = null)

    @Operation(summary = "국가 목록")
    @GetMapping("/countries")
    fun countries(
        @Parameter(description = "검색어(선택)")
        @RequestParam(required = false) q: String?
    ): ApiFandomResponse<List<OnboardingCatalogService.CountryItem>> =
        ApiFandomResponse.ok(catalogService.listCountries(q))

    @Operation(summary = "지역 목록")
    @GetMapping("/regions")
    fun regions(
        @Parameter(description = "국가 코드")
        @RequestParam countryCode: String,
        @Parameter(description = "검색어(선택)")
        @RequestParam(required = false) q: String?
    ): ApiFandomResponse<List<OnboardingCatalogService.RegionItem>> =
        ApiFandomResponse.ok(catalogService.listRegions(countryCode, q))

    @Operation(summary = "장르 목록")
    @GetMapping("/genres")
    fun genres(): ApiFandomResponse<List<OnboardingCatalogService.GenreItem>> =
        ApiFandomResponse.ok(catalogService.listGenres())

    @Operation(summary = "아티스트 목록")
    @GetMapping("/artists")
    fun artists(
        @Parameter(description = "검색어(선택)")
        @RequestParam(required = false) q: String?
    ): ApiFandomResponse<List<OnboardingCatalogService.ArtistItem>> =
        ApiFandomResponse.ok(catalogService.listArtists(q))

    @Operation(summary = "닉네임 중복 체크")
    @PostMapping("/nickname/validate")
    fun validateNickname(
        @RequestBody request: NicknameValidateRequest
    ): ApiFandomResponse<NicknameValidateResponse> {
        val nickname = request.nickname.trim()
        val available = when {
            nickname.isBlank() -> false
            nickname.length > 20 -> false
            else -> userService.isNicknameAvailable(nickname, null)
        }

        val reason = when {
            nickname.isBlank() -> "닉네임은 필수입니다"
            nickname.length > 20 -> "닉네임은 최대 20자까지 가능합니다"
            available -> null
            else -> "이미 사용 중인 닉네임입니다"
        }
        return ApiFandomResponse.ok(NicknameValidateResponse(available, reason))
    }
}
