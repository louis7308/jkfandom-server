package io.proto.jkfandom_app_server.domain.onboarding

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "regions")
class Region(
    @Id
    @Column(name = "code", length = 10)
    val code: String,

    @Column(name = "country_code", nullable = false, length = 2)
    val countryCode: String,

    @Column(name = "name_ko", nullable = false, length = 50)
    val nameKo: String,

    @Column(name = "name_ja", nullable = false, length = 50)
    val nameJa: String,

    @Column(name = "name_en", nullable = false, length = 50)
    val nameEn: String
)
