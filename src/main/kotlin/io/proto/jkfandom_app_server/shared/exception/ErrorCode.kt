package io.proto.jkfandom_app_server.shared.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val code: String,
    val message: String,
    val httpStatus: HttpStatus
) {
    INVALID_INPUT("C001", "입력값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("U404", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ALREADY_REGISTERED("U001", "이미 가입된 사용자입니다.", HttpStatus.CONFLICT),

    UNAUTHORIZED("A401", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("A403", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

    INTERNAL_ERROR("S500", "서버 내부 오류", HttpStatus.INTERNAL_SERVER_ERROR);
}
