package io.proto.jkfandom_app_server.shared.response

import io.proto.jkfandom_app_server.shared.exception.ErrorCode
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "공통 API 응답 포맷")
data class ApiFandomResponse<T>(

    @field:Schema(
        description = "요청 성공 여부",
        example = "true"
    )
    val success: Boolean,

    @field:Schema(
        description = "응답 데이터 (성공 시 존재)"
    )
    val data: T? = null,

    @field:Schema(
        description = "에러 정보 (실패 시 존재)"
    )
    val error: ErrorBody? = null
) {

    @Schema(description = "에러 상세 정보")
    data class ErrorBody(

        @field:Schema(
            description = "에러 코드",
            example = "U001"
        )
        val code: String,

        @field:Schema(
            description = "에러 메시지",
            example = "이미 가입된 사용자입니다."
        )
        val message: String
    )

    companion object {
        fun <T> ok(data: T): ApiFandomResponse<T> =
            ApiFandomResponse(success = true, data = data)

        fun error(
            errorCode: ErrorCode,
            messageOverride: String? = null
        ): ApiFandomResponse<Unit> =
            ApiFandomResponse(
                success = false,
                error = ErrorBody(
                    code = errorCode.code,
                    message = messageOverride ?: errorCode.message
                )
            )
    }
}
