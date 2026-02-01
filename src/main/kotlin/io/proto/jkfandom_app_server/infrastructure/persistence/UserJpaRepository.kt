package io.proto.jkfandom_app_server.infrastructure.persistence

import io.proto.jkfandom_app_server.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserJpaRepository : JpaRepository<User, String> {
    fun findByUuid(uuid: String): User?
    fun findByFirebaseUid(firebaseUid: String): User?
    fun existsByFirebaseUid(firebaseUid: String): Boolean
    fun existsByNickname(nickname: String): Boolean
}
