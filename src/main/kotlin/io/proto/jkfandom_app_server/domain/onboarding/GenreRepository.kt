package io.proto.jkfandom_app_server.domain.onboarding

interface GenreRepository {
    fun findAll(): List<Genre>
}
