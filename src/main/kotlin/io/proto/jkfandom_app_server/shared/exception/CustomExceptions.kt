package io.proto.jkfandom_app_server.shared.exception

class UserNotFoundException(message: String? = null)
    : BusinessException(ErrorCode.USER_NOT_FOUND, message)

class UserAlreadyExistsException(message: String? = null)
    : BusinessException(ErrorCode.ALREADY_REGISTERED, message)

