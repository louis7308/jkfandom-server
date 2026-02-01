package io.proto.jkfandom_app_server.application.usecase

import com.google.firebase.auth.FirebaseAuth
import io.proto.jkfandom_app_server.application.dto.SocialCustomTokenResponse
import io.proto.jkfandom_app_server.infrastructure.security.social.KakaoTokenVerifier
import io.proto.jkfandom_app_server.infrastructure.security.social.LineTokenVerifier
import io.proto.jkfandom_app_server.infrastructure.security.social.XTokenVerifier
import org.springframework.stereotype.Service

@Service
class SocialAuthService(
    private val kakaoTokenVerifier: KakaoTokenVerifier,
    private val lineTokenVerifier: LineTokenVerifier,
    private val xTokenVerifier: XTokenVerifier
) {

    fun issueCustomTokenFromKakao(accessToken: String): SocialCustomTokenResponse {
        val socialUser = kakaoTokenVerifier.verify(accessToken)
        val firebaseUid = "kakao:${socialUser.providerUserId}"
        val customToken = FirebaseAuth.getInstance().createCustomToken(firebaseUid)
        return SocialCustomTokenResponse(customToken,)
    }

    fun issueCustomTokenFromLine(accessToken: String): SocialCustomTokenResponse {
        val socialUser = lineTokenVerifier.verify(accessToken)
        val firebaseUid = "line:${socialUser.providerUserId}"
        val customToken = FirebaseAuth.getInstance().createCustomToken(firebaseUid)
        return SocialCustomTokenResponse(customToken)
    }

    fun issueCustomTokenFromX(accessToken: String): SocialCustomTokenResponse {
        val socialUser = xTokenVerifier.verify(accessToken)
        val firebaseUid = "x:${socialUser.providerUserId}"
        val customToken = FirebaseAuth.getInstance().createCustomToken(firebaseUid)
        return SocialCustomTokenResponse(customToken)
    }
}
