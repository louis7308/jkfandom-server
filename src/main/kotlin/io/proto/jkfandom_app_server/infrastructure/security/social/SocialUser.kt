package io.proto.jkfandom_app_server.infrastructure.security.social

data class SocialUser(
    val providerUserId: String,
    val email: String? = null
)