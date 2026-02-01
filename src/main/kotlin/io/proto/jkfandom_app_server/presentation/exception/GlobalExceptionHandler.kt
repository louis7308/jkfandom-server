package io.proto.jkfandom_app_server.presentation.exception

import io.proto.jkfandom_app_server.shared.exception.BusinessException
import io.proto.jkfandom_app_server.shared.exception.ErrorCode
import io.proto.jkfandom_app_server.shared.response.ApiFandomResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ApiFandomResponse<Unit>> {
        log.warn(
            "[BusinessException] code={}, message={}, topFrame={}, cause={}",
            e.errorCode.code,
            e.message,
            e.stackTrace.firstOrNull()?.toString(),
            e.cause?.javaClass?.name,
            e
        )

        return ResponseEntity
            .status(e.errorCode.httpStatus)
            .body(ApiFandomResponse.error(e.errorCode, e.message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ApiFandomResponse<Unit>> {
        val errorMessage = e.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        log.warn("[ValidationError] errors={}", errorMessage, e)

        return ResponseEntity
            .status(ErrorCode.INVALID_INPUT.httpStatus)
            .body(ApiFandomResponse.error(ErrorCode.INVALID_INPUT, errorMessage))
    }

    // (선택) query/form 바인딩 에러도 같이 잡고 싶으면
    @ExceptionHandler(BindException::class)
    fun handleBindException(e: BindException): ResponseEntity<ApiFandomResponse<Unit>> {
        val errorMessage = e.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        log.warn("[BindError] errors={}", errorMessage, e)

        return ResponseEntity
            .status(ErrorCode.INVALID_INPUT.httpStatus)
            .body(ApiFandomResponse.error(ErrorCode.INVALID_INPUT, errorMessage))
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): ResponseEntity<ApiFandomResponse<Unit>> {
        log.error("[UnhandledException] message={}", e.message, e)

        return ResponseEntity
            .status(ErrorCode.INTERNAL_ERROR.httpStatus)
            .body(ApiFandomResponse.error(ErrorCode.INTERNAL_ERROR))
    }
}