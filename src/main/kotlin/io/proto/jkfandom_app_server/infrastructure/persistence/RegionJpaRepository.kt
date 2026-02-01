package io.proto.jkfandom_app_server.infrastructure.persistence

import io.proto.jkfandom_app_server.domain.onboarding.Region
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RegionJpaRepository : JpaRepository<Region, String> {
    fun findByCountryCode(countryCode: String): List<Region>
}
