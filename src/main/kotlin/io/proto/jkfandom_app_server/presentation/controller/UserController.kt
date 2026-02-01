package io.proto.jkfandom_app_server.presentation.controller

import io.proto.jkfandom_app_server.application.dto.UserResponse
import io.proto.jkfandom_app_server.infrastructure.security.CurrentUserPrincipal
import io.proto.jkfandom_app_server.application.usecase.UserService
import io.proto.jkfandom_app_server.presentation.dto.OnboardingRequest
import io.proto.jkfandom_app_server.shared.response.ApiFandomResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(
    name = "Users",
    description = "유저 조회/온보딩 API"
)
@RestController
@RequestMapping("/v1/users")
class UserController(
    private val userService: UserService
) {

    @Operation(
        summary = "내 정보 조회",
        description = """
Firebase 인증 토큰 기반으로 현재 로그인한 유저의 정보를 조회합니다.

- 가입 완료 유저만 접근 가능(status = ACTIVE)
- Header: Authorization: Bearer <firebase_id_token>
"""
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "성공",
                content = [Content(
                    mediaType = "application/json",
                    // 제네릭 래퍼 스키마가 깨질 수 있어서, data 모델(UserResponse)라도 노출시키는 타협안
                    schema = Schema(implementation = UserResponse::class),
                    examples = [ExampleObject(
                        name = "success",
                        value = """
{
  "success": true,
  "data": {
    "uuid": "b6d3f7b1-7c67-4c7e-a4e0-3b2c83f9c7c1",
    "firebaseUid": "kakao:1234567890",
    "email": "user@example.com",
    "nickname": "홍길동",
    "status": "ACTIVE",
    "language": "JA",
    "birthDate": "1999-03-21",
    "gender": "FEMALE",
    "countryCode": "JP",
    "regionCode": "JP-13",
    "favoriteArtistIds": [1, 2],
    "topArtistId": 1,
    "createdAt": "2026-01-26T21:00:00"
  },
  "error": null
}
"""
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "A401: 인증 필요(토큰 누락/만료/유효하지 않음) 또는 미가입 상태",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "unauthorized",
                        value = """
{
  "success": false,
  "data": null,
  "error": {
    "code": "A401",
    "message": "인증이 필요합니다."
  }
}
"""
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "U404: 유저를 찾을 수 없음",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "not_found",
                        value = """
{
  "success": false,
  "data": null,
  "error": {
    "code": "U404",
    "message": "사용자를 찾을 수 없습니다"
  }
}
"""
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "500",
                description = "S500: 서버 내부 오류",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "internal_error",
                        value = """
{
  "success": false,
  "data": null,
  "error": {
    "code": "S500",
    "message": "서버 내부 오류"
  }
}
"""
                    )]
                )]
            )
        ]
    )
    @GetMapping("/me")
    fun getCurrentUser(
        @Parameter(hidden = true)
        @AuthenticationPrincipal principal: CurrentUserPrincipal
    ): ApiFandomResponse<UserResponse> {
        val user = userService.getUserByUuid(principal.userId!!)
        return ApiFandomResponse.ok(user)
    }

    @Operation(
        summary = "온보딩",
        description = """
온보딩 입력값을 받아 가입을 완료합니다.

요청:
- Header: Authorization: Bearer <firebase_id_token>
- Body: 온보딩 입력값(언어/닉네임/생년월일/성별/국가/지역코드/선호 장르/마이 스타/최애/약관 동의 등)
"""
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "성공: 온보딩 완료(가입 완료)",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "created",
                        value = """
{
  "success": true,
  "data": {
    "uuid": "b6d3f7b1-7c67-4c7e-a4e0-3b2c83f9c7c1",
    "firebaseUid": "kakao:1234567890",
    "email": "user@example.com",
    "nickname": "홍길동",
    "status": "ACTIVE",
    "language": "JA",
    "birthDate": "1999-03-21",
    "gender": "FEMALE",
    "countryCode": "JP",
    "regionCode": "JP-13",
    "favoriteArtistIds": [1, 2],
    "topArtistId": 1,
    "createdAt": "2026-01-26T21:00:00"
  },
  "error": null
}
"""
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "A401: 인증 필요(토큰 누락/만료/유효하지 않음)",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "unauthorized",
                        value = """
{
  "success": false,
  "data": null,
  "error": {
    "code": "A401",
    "message": "인증이 필요합니다."
  }
}
"""
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "409",
                description = "U001: 이미 가입된 사용자",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "conflict",
                        value = """
{
  "success": false,
  "data": null,
  "error": {
    "code": "U001",
    "message": "이미 가입된 사용자입니다."
  }
}
"""
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "500",
                description = "S500: 서버 내부 오류",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "internal_error",
                        value = """
{
  "success": false,
  "data": null,
  "error": {
    "code": "S500",
    "message": "서버 내부 오류"
  }
}
"""
                    )]
                )]
            )
        ]
    )
    @PostMapping("/onboarding")
    fun onboarding(
        @Parameter(hidden = true)
        @AuthenticationPrincipal principal: CurrentUserPrincipal,
        @Valid @RequestBody request: OnboardingRequest
    ): ResponseEntity<ApiFandomResponse<UserResponse>> {

        val user = userService.signUp(
            principal = principal,
            command = request.toCommand()
        )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiFandomResponse.ok(user))
    }
}