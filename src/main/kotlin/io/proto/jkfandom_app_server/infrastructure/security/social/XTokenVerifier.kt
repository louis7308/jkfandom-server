package io.proto.jkfandom_app_server.infrastructure.security.social

import io.proto.jkfandom_app_server.shared.exception.BusinessException
import io.proto.jkfandom_app_server.shared.exception.ErrorCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class XTokenVerifier(
    private val webClient: WebClient
) : SocialTokenVerifier {

    override fun verify(accessToken: String): SocialUser {
        val res = webClient.get()
            .uri("https://api.x.com/2/users/me")
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .onStatus({ it.value() == 401 }) {
                reactor.core.publisher.Mono.error(
                    BusinessException(ErrorCode.UNAUTHORIZED, "X 토큰이 유효하지 않습니다")
                )
            }
            .onStatus({ it.value() == 403 }) {
                reactor.core.publisher.Mono.error(
                    BusinessException(ErrorCode.FORBIDDEN, "X API 접근이 거부되었습니다")
                )
            }
            .onStatus({ it.is4xxClientError }) {
                reactor.core.publisher.Mono.error(
                    BusinessException(ErrorCode.INVALID_INPUT, "X 인증 요청이 실패했습니다")
                )
            }
            .onStatus({ it.is5xxServerError }) {
                reactor.core.publisher.Mono.error(
                    BusinessException(ErrorCode.INTERNAL_ERROR, "X 서버 오류")
                )
            }
            .bodyToMono(XMeResponse::class.java)
            .block() ?: throw BusinessException(ErrorCode.INTERNAL_ERROR, "X 응답이 비었습니다")

        return SocialUser(
            providerUserId = res.data.id,
            email = null // X는 기본적으로 이메일 안 줌
        )
    }

    private data class XMeResponse(
        val data: XUser
    )

    private data class XUser(
        val id: String,
        val username: String
    )
}