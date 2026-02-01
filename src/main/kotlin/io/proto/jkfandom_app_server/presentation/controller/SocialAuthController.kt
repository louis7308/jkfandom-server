package io.proto.jkfandom_app_server.presentation.controller

import io.swagger.v3.oas.annotations.responses.ApiResponse as OApiResponse
import io.proto.jkfandom_app_server.application.dto.SocialCustomTokenResponse
import io.proto.jkfandom_app_server.application.usecase.SocialAuthService
import io.proto.jkfandom_app_server.shared.response.ApiFandomResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(
    name = "Social Auth",
    description = "소셜 Access Token을 검증하고 Firebase Custom Token을 발급합니다."
)
@RestController
@RequestMapping("/v1/auth/social")
class SocialAuthController(
    private val socialAuthService: SocialAuthService
) {

    /**
     * Flutter:
     *  - Header: X-Social-Access-Token: <kakaoAccessToken>
     */
    @Operation(
        summary = "카카오: AccessToken 검증 → Firebase Custom Token 발급",
        description = """
Flutter에서 카카오 로그인 SDK로 발급받은 access_token을 헤더로 전달합니다.
서버는 Kakao API `/v2/user/me` 호출로 토큰 유효성을 검증합니다.
성공 시 Firebase Custom Token을 반환합니다.

요청 헤더:
- X-Social-Access-Token: kakao access token
"""
    )
    @ApiResponses(
        value = [
            OApiResponse(
                responseCode = "200",
                description = "성공: Firebase Custom Token 발급",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "success",
                        value = """
{
  "success": true,
  "data": {
    "customToken": "firebase_custom_token_here"
  },
  "error": null
}
"""
                    )]
                )]
            ),
            OApiResponse(
                responseCode = "401",
                description = "A401: 카카오 토큰이 유효하지 않음/만료",
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
    "message": "카카오 토큰이 유효하지 않습니다"
  }
}
"""
                    )]
                )]
            ),
            OApiResponse(
                responseCode = "403",
                description = "A403: 카카오 API 접근 거부",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "forbidden",
                        value = """
{
  "success": false,
  "data": null,
  "error": {
    "code": "A403",
    "message": "카카오 API 접근이 거부되었습니다"
  }
}
"""
                    )]
                )]
            ),
            OApiResponse(
                responseCode = "400",
                description = "C001: 카카오 인증 요청이 실패(클라이언트 오류/요청 형식 문제 등)",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "bad_request",
                        value = """
{
  "success": false,
  "data": null,
  "error": {
    "code": "C001",
    "message": "카카오 인증 요청이 실패했습니다"
  }
}
"""
                    )]
                )]
            ),
            OApiResponse(
                responseCode = "500",
                description = "S500: 서버 내부 오류 또는 카카오 서버 오류",
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
    @PostMapping("/kakao")
    fun kakao(
        @Parameter(
            `in` = ParameterIn.HEADER,
            name = "X-Social-Access-Token",
            description = "카카오 로그인 SDK에서 발급받은 access_token",
            required = true,
            example = "kakao_access_token_here"
        )
        @RequestHeader("X-Social-Access-Token") accessToken: String
    ): ApiFandomResponse<SocialCustomTokenResponse> {
        val res = socialAuthService.issueCustomTokenFromKakao(accessToken)
        return ApiFandomResponse.ok(res)
    }

    /**
     * Flutter:
     *  - Header: X-Social-Access-Token: <lineAccessToken>
     */
    @Operation(
        summary = "LINE: AccessToken 검증 → Firebase Custom Token 발급",
        description = """
Flutter에서 LINE 로그인으로 발급받은 access token을 헤더로 전달합니다.
서버는 LINE API `/v2/profile` 호출로 토큰 유효성을 검증합니다.
성공 시 Firebase Custom Token을 반환합니다.

요청 헤더:
- X-Social-Access-Token: line access token
"""
    )
    @ApiResponses(
        value = [
            OApiResponse(
                responseCode = "200",
                description = "성공: Firebase Custom Token 발급",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "success",
                        value = """
{
  "success": true,
  "data": {
    "customToken": "firebase_custom_token_here"
  },
  "error": null
}
"""
                    )]
                )]
            ),
            OApiResponse(
                responseCode = "401",
                description = "A401: LINE 토큰이 유효하지 않음/만료",
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
    "message": "인증이 필요합니다"
  }
}
"""
                    )]
                )]
            ),
            OApiResponse(
                responseCode = "400",
                description = "C001: LINE 인증 요청이 실패(클라이언트 오류/요청 형식 문제 등)",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "bad_request",
                        value = """
{
  "success": false,
  "data": null,
  "error": {
    "code": "C001",
    "message": "입력값이 올바르지 않습니다."
  }
}
"""
                    )]
                )]
            ),
            OApiResponse(
                responseCode = "500",
                description = "S500: 서버 내부 오류 또는 LINE 서버 오류",
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
    @PostMapping("/line")
    fun line(
        @Parameter(
            `in` = ParameterIn.HEADER,
            name = "X-Social-Access-Token",
            description = "LINE 로그인으로 발급받은 access token",
            required = true,
            example = "line_access_token_here"
        )
        @RequestHeader("X-Social-Access-Token") accessToken: String
    ): ApiFandomResponse<SocialCustomTokenResponse> {
        val res = socialAuthService.issueCustomTokenFromLine(accessToken)
        return ApiFandomResponse.ok(res)
    }

    /**
     * Flutter:
     *  - Header: X-Social-Access-Token: <xAccessToken>
     */
    @Operation(
        summary = "X: AccessToken 검증 -> Firebase Custom Token 발급",
        description = """
Flutter에서 X 로그인으로 발급받은 access token을 헤더로 전달합니다.
서버는 X API `/2/users/me` 호출로 토큰 유효성을 검증합니다.
성공 시 Firebase Custom Token을 반환합니다.

요청 헤더:
- X-Social-Access-Token: x access token
"""
    )
    @ApiResponses(
        value = [
            OApiResponse(
                responseCode = "200",
                description = "성공: Firebase Custom Token 발급",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "success",
                        value = """
{
  "success": true,
  "data": {
    "customToken": "firebase_custom_token_here"
  },
  "error": null
}
"""
                    )]
                )]
            ),
            OApiResponse(
                responseCode = "401",
                description = "A401: X 토큰이 유효하지 않음/만료",
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
    "message": "인증이 필요합니다"
  }
}
"""
                    )]
                )]
            ),
            OApiResponse(
                responseCode = "400",
                description = "C001: X 인증 요청이 실패(클라이언트 오류/요청 형식 문제 등)",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        name = "bad_request",
                        value = """
{
  "success": false,
  "data": null,
  "error": {
    "code": "C001",
    "message": "입력값이 올바르지 않습니다."
  }
}
"""
                    )]
                )]
            ),
            OApiResponse(
                responseCode = "500",
                description = "S500: 서버 내부 오류 또는 X 서버 오류",
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
    @PostMapping("/x")
    fun x(
        @Parameter(
            `in` = ParameterIn.HEADER,
            name = "X-Social-Access-Token",
            description = "X 로그인으로 발급받은 access token",
            required = true,
            example = "x_access_token_here"
        )
        @RequestHeader("X-Social-Access-Token") accessToken: String
    ): ApiFandomResponse<SocialCustomTokenResponse> {
        val res = socialAuthService.issueCustomTokenFromX(accessToken)
        return ApiFandomResponse.ok(res)
    }
}