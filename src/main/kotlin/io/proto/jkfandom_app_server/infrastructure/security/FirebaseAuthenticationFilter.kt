package io.proto.jkfandom_app_server.infrastructure.security

import io.proto.jkfandom_app_server.infrastructure.firebase.FirebaseAuthService
import io.proto.jkfandom_app_server.infrastructure.firebase.InvalidFirebaseTokenException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class FirebaseAuthenticationFilter(
    private val firebaseAuthService: FirebaseAuthService,
    private val authUserResolver: AuthUserResolver
) : OncePerRequestFilter() {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = extractToken(request)
        if (token == null) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val firebaseUser = firebaseAuthService.verifyToken(token)

            // 유저 있으면 uuid 가져오고, 없으면 null
            val user = authUserResolver.findUserByFirebaseUid(firebaseUser.uid)

            if (user == null && !isAuthStatusPath(request)) {
                filterChain.doFilter(request, response)
                return
            }

            val userId = user?.uuid
            val status = user?.status


            val principal = CurrentUserPrincipal(
                userId = userId,
                firebaseUid = firebaseUser.uid,
                email = firebaseUser.email,
                nickname = firebaseUser.nickname,
                status = status
            )

            SecurityContextHolder.getContext().authentication =
                FirebaseAuthenticationToken(principal)

        } catch (e: InvalidFirebaseTokenException) {
            SecurityContextHolder.clearContext()
            // 토큰이 invalid면 여기서 401을 보내도 되고,
            // EntryPoint로 넘기려면 그냥 clear만 하고 진행해도 됨
        }

        filterChain.doFilter(request, response)

    }

    private fun extractToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)
        return if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            bearerToken.substring(BEARER_PREFIX.length)
        } else null
    }

    private fun isAuthStatusPath(request: HttpServletRequest): Boolean {
        return request.requestURI == "/v1/auth/status"
    }
}