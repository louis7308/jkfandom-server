package io.proto.jkfandom_app_server.infrastructure.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class FirebaseAuthenticationToken(
    private val principalObj: CurrentUserPrincipal,
    authorities: Collection<GrantedAuthority> = emptyList()
) : AbstractAuthenticationToken(authorities) {

    init {
        isAuthenticated = true
    }

    override fun getCredentials(): Any = ""
    override fun getPrincipal(): Any = principalObj

    val userId: String? get() = principalObj.userId
    val firebaseUid: String get() = principalObj.firebaseUid
    val email: String? get() = principalObj.email
    val nickname: String get() = principalObj.nickname
}