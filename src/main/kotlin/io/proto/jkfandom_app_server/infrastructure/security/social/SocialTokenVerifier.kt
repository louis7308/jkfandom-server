package io.proto.jkfandom_app_server.infrastructure.security.social

interface SocialTokenVerifier {
    fun verify(accessToken: String): SocialUser
}