package io.proto.jkfandom_app_server.domain.onboarding

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "artists")
class Artist(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "group_name", nullable = false, length = 100)
    val groupName: String,

    @Column(name = "profile_image_url", nullable = true, length = 255)
    val profileImageUrl: String? = null,

    @Column(name = "display_name_ko", nullable = false, length = 50)
    val displayNameKo: String,

    @Column(name = "display_name_ja", nullable = false, length = 50)
    val displayNameJa: String,

    @Column(name = "display_name_en", nullable = false, length = 50)
    val displayNameEn: String
)
