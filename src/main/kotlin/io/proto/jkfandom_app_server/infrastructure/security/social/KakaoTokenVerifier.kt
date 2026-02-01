package io.proto.jkfandom_app_server.infrastructure.security.social

import io.proto.jkfandom_app_server.shared.exception.BusinessException
import io.proto.jkfandom_app_server.shared.exception.ErrorCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class KakaoTokenVerifier(
    private val webClient: WebClient
) : SocialTokenVerifier {

    override fun verify(accessToken: String): SocialUser {
        val res = webClient.get()
            .uri("https://kapi.kakao.com/v2/user/me")
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .onStatus({ it.value() == 401 }) {
                // 카카오 토큰이 유효하지 않음/만료
                reactor.core.publisher.Mono.error(BusinessException(ErrorCode.UNAUTHORIZED, "카카오 토큰이 유효하지 않습니다"))
            }
            .onStatus({ it.value() == 403 }) {
                reactor.core.publisher.Mono.error(BusinessException(ErrorCode.FORBIDDEN, "카카오 API 접근이 거부되었습니다"))
            }
            .onStatus({ it.is4xxClientError }) {
                reactor.core.publisher.Mono.error(BusinessException(ErrorCode.INVALID_INPUT, "카카오 인증 요청이 실패했습니다"))
            }
            .onStatus({ it.is5xxServerError }) {
                reactor.core.publisher.Mono.error(BusinessException(ErrorCode.INTERNAL_ERROR, "카카오 서버 오류"))
            }
            .bodyToMono(KakaoMeResponse::class.java)
            .block() ?: throw BusinessException(ErrorCode.INTERNAL_ERROR, "카카오 응답이 비었습니다")

        return SocialUser(
            providerUserId = res.id.toString(),
            email = res.kakao_account?.email
        )
    }

    private data class KakaoMeResponse(
        val id: Long,
        val kakao_account: KakaoAccount?
    )

    private data class KakaoAccount(
        val email: String?
    )
}