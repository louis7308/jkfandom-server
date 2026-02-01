package io.proto.jkfandom_app_server.infrastructure.security.social

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class LineTokenVerifier(
    private val webClient: WebClient
) : SocialTokenVerifier {

    override fun verify(accessToken: String): SocialUser {
        val res = webClient.get()
            .uri("https://api.line.me/v2/profile")
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .bodyToMono(LineProfileResponse::class.java)
            .block() ?: throw IllegalArgumentException("LINE token verification failed")

        return SocialUser(
            providerUserId = res.userId,
            email = null // LINE은 profile에서 email이 보통 안 옴(스코프/플로우 별도)
        )
    }

    private data class LineProfileResponse(
        val userId: String,
        val displayName: String
    )
}