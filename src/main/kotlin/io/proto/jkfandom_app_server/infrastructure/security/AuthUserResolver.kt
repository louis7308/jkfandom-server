package io.proto.jkfandom_app_server.infrastructure.security

import io.proto.jkfandom_app_server.domain.user.User
import io.proto.jkfandom_app_server.domain.user.UserRepository
import org.springframework.stereotype.Component

@Component
class AuthUserResolver(
    private val userRepository: UserRepository
) {
    fun findUserByFirebaseUid(firebaseUid: String): User? {
        return userRepository.findByFirebaseUid(firebaseUid)
    }
}