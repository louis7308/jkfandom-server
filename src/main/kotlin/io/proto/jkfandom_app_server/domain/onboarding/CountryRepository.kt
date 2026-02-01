package io.proto.jkfandom_app_server.domain.onboarding

interface CountryRepository {
    fun findAll(): List<Country>
}
