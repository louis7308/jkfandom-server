package io.proto.jkfandom_app_server.domain.onboarding

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "genres")
class Genre(
    @Id
    @Column(name = "code", length = 20)
    val code: String,

    @Column(name = "image_url", nullable = true, length = 255)
    val imageUrl: String? = null,

    @Column(name = "name_ko", nullable = false, length = 50)
    val nameKo: String,

    @Column(name = "name_ja", nullable = false, length = 50)
    val nameJa: String,

    @Column(name = "name_en", nullable = false, length = 50)
    val nameEn: String
)
