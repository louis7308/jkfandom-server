package io.proto.jkfandom_app_server.presentation.controller

import io.proto.jkfandom_app_server.domain.user.UserStatus
import io.proto.jkfandom_app_server.infrastructure.security.CurrentUserPrincipal
import io.proto.jkfandom_app_server.application.usecase.UserService
import io.proto.jkfandom_app_server.shared.exception.BusinessException
import io.proto.jkfandom_app_server.shared.exception.ErrorCode
import io.proto.jkfandom_app_server.shared.response.ApiFandomResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(
    name = "Auth",
    description = "인증/가입 상태 확인 API"
)
@RestController
@RequestMapping("/v1/auth")
@SecurityRequirement(name = "FirebaseBearer")
class AuthController(
    private val userService: UserService
) {

    enum class AuthStatus {
        REGISTERED,
        NEED_ONBOARDING
    }

    data class AuthStatusResponse(
        val status: AuthStatus
    )

    @Operation(
        summary = "가입 상태 확인",
        description = """
Firebase 인증 토큰(Authorization: Bearer <id_token>)을 기반으로 현재 유저의 가입 여부를 확인합니다.
계정이 없으면 PROVISIONAL 상태로 자동 생성합니다.

응답 status 값:
- REGISTERED: 이미 가입 완료(userId 존재)
- NEED_ONBOARDING: 인증은 됐지만 가입 정보가 아직 없음(userId 없음)

요청:
- POST /v1/auth/status
- Header: Authorization: Bearer <firebase_id_token>
"""
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "성공: 가입 상태 반환",
                content = [Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "registered",
                            summary = "이미 가입된 유저",
                            value = """
{
  "success": true,
  "data": { "status": "REGISTERED" },
  "error": null
}
"""
                        ),
                        ExampleObject(
                            name = "need_onboarding",
                            summary = "온보딩 필요(미가입)",
                            value = """
{
  "success": true,
  "data": { "status": "NEED_ONBOARDING" },
  "error": null
}
"""
                        )
                    ]
                )]
            )
        ]
    )
    @PostMapping("/status")
    fun status(
        @AuthenticationPrincipal principal: CurrentUserPrincipal?
    ): ApiFandomResponse<AuthStatusResponse> {
        if (principal == null) {
            // 인증 토큰이 없거나 인증 실패
            // 여기서 401을 내고 싶으면 예외 던지는 게 깔끔
            throw BusinessException(ErrorCode.UNAUTHORIZED)
        }


        val user = userService.ensureProvisionalUser(principal)
        val status = if (user.status == UserStatus.ACTIVE) AuthStatus.REGISTERED else AuthStatus.NEED_ONBOARDING
        return ApiFandomResponse.ok(AuthStatusResponse(status))
    }
}
