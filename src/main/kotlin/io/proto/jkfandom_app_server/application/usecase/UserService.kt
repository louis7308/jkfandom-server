package io.proto.jkfandom_app_server.application.usecase

import io.proto.jkfandom_app_server.application.dto.OnboardingCommand
import io.proto.jkfandom_app_server.application.dto.UserResponse
import io.proto.jkfandom_app_server.domain.user.User
import io.proto.jkfandom_app_server.domain.user.UserRepository
import io.proto.jkfandom_app_server.domain.user.UserStatus
import io.proto.jkfandom_app_server.infrastructure.security.CurrentUserPrincipal
import io.proto.jkfandom_app_server.shared.exception.BusinessException
import io.proto.jkfandom_app_server.shared.exception.ErrorCode
import io.proto.jkfandom_app_server.shared.exception.UserAlreadyExistsException
import io.proto.jkfandom_app_server.shared.exception.UserNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val onboardingCatalogService: OnboardingCatalogService
) {

    fun getUserByUuid(uuid: String): UserResponse {
        val user = userRepository.findByUuid(uuid)
            ?: throw UserNotFoundException("사용자를 찾을 수 없습니다")

        return UserResponse.from(user)
    }

    @Transactional
    fun ensureProvisionalUser(principal: CurrentUserPrincipal): UserResponse {
        val existing = userRepository.findByFirebaseUid(principal.firebaseUid)
        if (existing != null) {
            return UserResponse.from(existing)
        }

        val safeNickname = principal.nickname.take(12)
        val user = User(
            firebaseUid = principal.firebaseUid,
            email = principal.email,
            nickname = safeNickname,
            status = UserStatus.PROVISIONAL
        )

        val savedUser = userRepository.save(user)
        return UserResponse.from(savedUser)
    }

    fun isNicknameAvailable(nickname: String, firebaseUid: String?): Boolean {
        if (nickname.isBlank()) return false
        val existing = if (firebaseUid.isNullOrBlank()) null else userRepository.findByFirebaseUid(firebaseUid)
        if (existing != null && existing.nickname == nickname) return true
        return !userRepository.existsByNickname(nickname)
    }

    @Transactional
    fun signUp(
        principal: CurrentUserPrincipal,
        command: OnboardingCommand
    ): UserResponse {
        val nickname = command.nickname.trim()
        val user = userRepository.findByFirebaseUid(principal.firebaseUid)

        if (user?.status == UserStatus.ACTIVE) {
            throw UserAlreadyExistsException("이미 가입된 사용자입니다")
        }

        if (nickname.isBlank() || nickname.length > 20) {
            throw BusinessException(ErrorCode.INVALID_INPUT, "닉네임은 1~20자여야 합니다")
        }

        if (!isNicknameAvailable(nickname, principal.firebaseUid)) {
            throw BusinessException(ErrorCode.INVALID_INPUT, "이미 사용 중인 닉네임입니다")
        }

        if (!onboardingCatalogService.existsCountry(command.countryCode)) {
            throw BusinessException(ErrorCode.INVALID_INPUT, "존재하지 않는 국가 코드입니다")
        }

        if (!onboardingCatalogService.existsRegion(command.countryCode, command.regionCode)) {
            throw BusinessException(ErrorCode.INVALID_INPUT, "존재하지 않는 지역 코드입니다")
        }

        if (!onboardingCatalogService.existsGenres(command.favoriteGenres)) {
            throw BusinessException(ErrorCode.INVALID_INPUT, "존재하지 않는 장르가 포함되어 있습니다")
        }

        if (command.favoriteArtistIds.distinct().isEmpty()) {
            throw BusinessException(ErrorCode.INVALID_INPUT, "favoriteArtistIds는 최소 1개 이상 필요합니다")
        }

        if (!command.favoriteArtistIds.contains(command.topArtistId)) {
            throw BusinessException(ErrorCode.INVALID_INPUT, "topArtistId는 favoriteArtistIds에 포함되어야 합니다")
        }

        val invalidArtist = command.favoriteArtistIds.firstOrNull { !onboardingCatalogService.existsArtist(it) }
        if (invalidArtist != null || !onboardingCatalogService.existsArtist(command.topArtistId)) {
            throw BusinessException(ErrorCode.INVALID_INPUT, "존재하지 않는 아티스트가 포함되어 있습니다")
        }

        val target = user ?: User(
            firebaseUid = principal.firebaseUid,
            email = principal.email,
            nickname = principal.nickname,
            status = UserStatus.PROVISIONAL
        )

        target.language = command.language
        target.nickname = nickname
        target.birthDate = command.birthDate
        target.gender = command.gender
        target.countryCode = command.countryCode.uppercase()
        target.regionCode = command.regionCode
        target.favoriteGenres = command.favoriteGenres.distinct().toMutableList()
        target.favoriteArtistIds = command.favoriteArtistIds.distinct().toMutableList()
        target.topArtistId = command.topArtistId
        target.marketingAgree = command.agreements.marketingAccepted
        target.termsAccepted = command.agreements.termsAccepted
        target.privacyAccepted = command.agreements.privacyAccepted
        target.status = UserStatus.ACTIVE

        val savedUser = userRepository.save(target)
        return UserResponse.from(savedUser)
    }
}
