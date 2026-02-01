package io.proto.jkfandom_app_server.domain.onboarding

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "countries")
class Country(
    @Id
    @Column(name = "code", length = 2)
    val code: String,

    @Column(name = "name_ko", nullable = false, length = 50)
    val nameKo: String,

    @Column(name = "name_ja", nullable = false, length = 50)
    val nameJa: String,

    @Column(name = "name_en", nullable = false, length = 50)
    val nameEn: String
)
