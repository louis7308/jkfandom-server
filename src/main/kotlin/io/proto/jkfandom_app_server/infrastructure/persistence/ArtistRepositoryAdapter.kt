package io.proto.jkfandom_app_server.infrastructure.persistence

import io.proto.jkfandom_app_server.domain.onboarding.Artist
import io.proto.jkfandom_app_server.domain.onboarding.ArtistRepository
import org.springframework.stereotype.Repository

@Repository
class ArtistRepositoryAdapter(
    private val artistJpaRepository: ArtistJpaRepository
) : ArtistRepository {
    override fun findAll(): List<Artist> =
        artistJpaRepository.findAll()

    override fun existsById(id: Long): Boolean =
        artistJpaRepository.existsById(id)
}
