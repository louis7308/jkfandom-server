package io.proto.jkfandom_app_server.infrastructure.persistence

import io.proto.jkfandom_app_server.domain.onboarding.Country
import io.proto.jkfandom_app_server.domain.onboarding.CountryRepository
import org.springframework.stereotype.Repository

@Repository
class CountryRepositoryAdapter(
    private val countryJpaRepository: CountryJpaRepository
) : CountryRepository {
    override fun findAll(): List<Country> =
        countryJpaRepository.findAll()
}
