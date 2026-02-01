package io.proto.jkfandom_app_server.domain.user

interface UserRepository {
    fun findByUuid(uuid: String): User?
    fun findByFirebaseUid(firebaseUid: String): User?
    fun existsByFirebaseUid(firebaseUid: String): Boolean
    fun existsByNickname(nickname: String): Boolean
    fun save(user: User): User
}