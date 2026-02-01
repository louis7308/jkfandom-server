package io.proto.jkfandom_app_server.domain.user

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_firebase_uid", columnList = "firebase_uid", unique = true)
    ]
)
class User(
    @Id
    val uuid: String = UUID.randomUUID().toString(),

    @Column(name = "firebase_uid", nullable = false, unique = true, length = 128)
    val firebaseUid: String,

    @Column(name = "email", length = 255)
    val email: String? = null,

    @Column(name = "nickname", nullable = true, length = 12)
    var nickname: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: UserStatus = UserStatus.PROVISIONAL,

    /** 앱 표시 언어 */
    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = true, length = 10)
    var language: AppLanguage? = null,

    /** 생년월일 */
    @Column(name = "birth_date", nullable = true)
    var birthDate: LocalDate? = null,

    /** 성별 */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = true, length = 20)
    var gender: Gender? = null,

    /** 국가코드 */
    @Column(name = "country_code", nullable = true, length = 2)
    var countryCode: String? = null,

    /** 지역 코드 */
    @Column(name = "region_code", nullable = true, length = 10)
    var regionCode: String? = null,

    /** 선호 장르 (별도 테이블: user_favorite_genres) */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "user_favorite_genres",
        joinColumns = [JoinColumn(name = "user_uuid", referencedColumnName = "uuid")]
    )
    @Column(name = "genre", nullable = false, length = 50)
    var favoriteGenres: MutableList<String> = mutableListOf(),

    /** 마이 스타 (별도 테이블: user_favorite_artists) */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "user_favorite_artists",
        joinColumns = [JoinColumn(name = "user_uuid", referencedColumnName = "uuid")]
    )
    @Column(name = "artist_id", nullable = false)
    var favoriteArtistIds: MutableList<Long> = mutableListOf(),

    /** 최애 아티스트 ID */
    @Column(name = "top_artist_id", nullable = true)
    var topArtistId: Long? = null,

    /** 마케팅 동의 */
    @Column(name = "marketing_agree", nullable = false)
    var marketingAgree: Boolean = false,

    /** 약관 동의(필수) - 저장 권장 */
    @Column(name = "terms_accepted", nullable = false)
    var termsAccepted: Boolean = false,

    @Column(name = "privacy_accepted", nullable = false)
    var privacyAccepted: Boolean = false,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)