package io.proto.jkfandom_app_server.infrastructure.persistence

import io.proto.jkfandom_app_server.domain.onboarding.Region
import io.proto.jkfandom_app_server.domain.onboarding.RegionRepository
import org.springframework.stereotype.Repository

@Repository
class RegionRepositoryAdapter(
    private val regionJpaRepository: RegionJpaRepository
) : RegionRepository {
    override fun findByCountryCode(countryCode: String): List<Region> =
        regionJpaRepository.findByCountryCode(countryCode)
}
