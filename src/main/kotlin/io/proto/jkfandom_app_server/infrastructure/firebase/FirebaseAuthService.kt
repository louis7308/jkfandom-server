package io.proto.jkfandom_app_server.infrastructure.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseToken
import org.springframework.stereotype.Service

@Service
class FirebaseAuthService {

    data class FirebaseUser(
        val uid: String,
        val email: String?,
        val nickname: String
    )

    fun verifyToken(idToken: String): FirebaseUser {
        try {
            val decodedToken: FirebaseToken = FirebaseAuth.getInstance().verifyIdToken(idToken)
            return FirebaseUser(
                uid = decodedToken.uid,
                email = decodedToken.email,
                nickname = decodedToken.name ?: "이름 없음"
            )
        } catch (e: FirebaseAuthException) {
            throw InvalidFirebaseTokenException("Invalid Firebase token: ${e.message}")
        } catch (e: IllegalArgumentException) {
            throw InvalidFirebaseTokenException("Invalid token format: ${e.message}")
        }
    }
}

class InvalidFirebaseTokenException(message: String) : RuntimeException(message)