package io.proto.jkfandom_app_server.shared.exception

open class BusinessException(
    val errorCode: ErrorCode,
    override val message: String? = null
) : RuntimeException(message ?: errorCode.message)
