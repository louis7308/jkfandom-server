package io.proto.jkfandom_app_server.application.usecase

import io.proto.jkfandom_app_server.domain.onboarding.ArtistRepository
import io.proto.jkfandom_app_server.domain.onboarding.CountryRepository
import io.proto.jkfandom_app_server.domain.onboarding.GenreRepository
import io.proto.jkfandom_app_server.domain.onboarding.RegionRepository
import io.proto.jkfandom_app_server.domain.user.AppLanguage
import org.springframework.stereotype.Service

@Service
class OnboardingCatalogService(
    private val countryRepository: CountryRepository,
    private val regionRepository: RegionRepository,
    private val genreRepository: GenreRepository,
    private val artistRepository: ArtistRepository
) {

    data class LanguageItem(val code: String, val labelKo: String, val labelJa: String, val labelEn: String)
    data class CountryItem(val code: String, val nameKo: String, val nameJa: String, val nameEn: String)
    data class RegionItem(val code: String, val nameKo: String, val nameJa: String, val nameEn: String)
    data class GenreItem(
        val code: String,
        val imageUrl: String?,
        val nameKo: String,
        val nameJa: String,
        val nameEn: String
    )
    data class ArtistItem(
        val id: Long,
        val groupName: String,
        val profileImageUrl: String?,
        val displayNameKo: String,
        val displayNameJa: String,
        val displayNameEn: String
    )

    fun listCountries(query: String?): List<CountryItem> =
        filterByQuery(
            countryRepository.findAll().map { CountryItem(it.code, it.nameKo, it.nameJa, it.nameEn) },
            query
        ) { listOf(it.code, it.nameKo, it.nameJa, it.nameEn) }

    fun listRegions(countryCode: String, query: String?): List<RegionItem> {
        val regions = regionRepository.findByCountryCode(countryCode.uppercase())
            .map { RegionItem(it.code, it.nameKo, it.nameJa, it.nameEn) }
        return filterByQuery(regions, query) { listOf(it.code, it.nameKo, it.nameJa, it.nameEn) }
    }

    fun listGenres(): List<GenreItem> =
        genreRepository.findAll().map { GenreItem(it.code, it.imageUrl, it.nameKo, it.nameJa, it.nameEn) }

    fun listArtists(query: String?): List<ArtistItem> =
        filterByQuery(
            artistRepository.findAll().map {
                ArtistItem(
                    it.id,
                    it.groupName,
                    it.profileImageUrl,
                    it.displayNameKo,
                    it.displayNameJa,
                    it.displayNameEn
                )
            },
            query
        ) { listOf(it.groupName, it.displayNameKo, it.displayNameJa, it.displayNameEn, it.id.toString()) }

    fun existsArtist(id: Long): Boolean = artistRepository.existsById(id)

    fun existsCountry(code: String): Boolean =
        countryRepository.findAll().any { it.code == code.uppercase() }

    fun existsRegion(countryCode: String, regionCode: String): Boolean =
        regionRepository.findByCountryCode(countryCode.uppercase()).any { it.code == regionCode }

    fun existsGenres(codes: List<String>): Boolean {
        val genreCodes = genreRepository.findAll().map { it.code }.toSet()
        return codes.all { genreCodes.contains(it) }
    }

    private fun <T> filterByQuery(
        items: List<T>,
        query: String?,
        fields: (T) -> List<String>
    ): List<T> {
        val q = query?.trim()?.lowercase()
        if (q.isNullOrEmpty()) return items
        return items.filter { item -> fields(item).any { it.lowercase().contains(q) } }
    }
}
