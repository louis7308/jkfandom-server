package io.proto.jkfandom_app_server.infrastructure.persistence

import io.proto.jkfandom_app_server.domain.user.User
import io.proto.jkfandom_app_server.domain.user.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryAdapter(
    private val userJpaRepository: UserJpaRepository
) : UserRepository {
    override fun findByUuid(uuid: String): User? =
        userJpaRepository.findByUuid(uuid)

    override fun findByFirebaseUid(firebaseUid: String): User? =
        userJpaRepository.findByFirebaseUid(firebaseUid)

    override fun existsByFirebaseUid(firebaseUid: String): Boolean =
        userJpaRepository.existsByFirebaseUid(firebaseUid)

    override fun existsByNickname(nickname: String): Boolean =
        userJpaRepository.existsByNickname(nickname)

    override fun save(user: User): User =
        userJpaRepository.save(user)
}
