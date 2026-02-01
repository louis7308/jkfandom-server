package io.proto.jkfandom_app_server.infrastructure.security

import io.proto.jkfandom_app_server.domain.user.UserStatus

data class CurrentUserPrincipal(
    val userId: String?,
    val firebaseUid: String,
    val email: String?,
    val nickname: String,
    val status: UserStatus?
) {
    val isRegistered: Boolean get() = userId != null
    val isActive: Boolean get() = status == UserStatus.ACTIVE
}
