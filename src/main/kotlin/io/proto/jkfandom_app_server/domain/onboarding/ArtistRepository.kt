package io.proto.jkfandom_app_server.domain.onboarding

interface ArtistRepository {
    fun findAll(): List<Artist>
    fun existsById(id: Long): Boolean
}
