package io.proto.jkfandom_app_server.infrastructure.persistence

import io.proto.jkfandom_app_server.domain.onboarding.Genre
import io.proto.jkfandom_app_server.domain.onboarding.GenreRepository
import org.springframework.stereotype.Repository

@Repository
class GenreRepositoryAdapter(
    private val genreJpaRepository: GenreJpaRepository
) : GenreRepository {
    override fun findAll(): List<Genre> =
        genreJpaRepository.findAll()
}
