package io.proto.jkfandom_app_server.domain.onboarding

interface RegionRepository {
    fun findByCountryCode(countryCode: String): List<Region>
}
