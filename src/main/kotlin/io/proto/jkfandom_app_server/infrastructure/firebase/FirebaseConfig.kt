package io.proto.jkfandom_app_server.infrastructure.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.FileInputStream

@Configuration
class FirebaseConfig(
    @Value("\${firebase.config.path:firebase-service-account.json}")
    private val firebaseConfigPath: String
) {

    @PostConstruct
    fun initialize() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                val serviceAccount = try {
                    // ClassPath에서 먼저 시도
                    ClassPathResource(firebaseConfigPath).inputStream
                } catch (e: Exception) {
                    // 실패 시 파일 시스템에서 시도
                    FileInputStream(firebaseConfigPath)
                }

                val options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build()

                FirebaseApp.initializeApp(options)
                println("Firebase initialized successfully")
            } catch (e: Exception) {
                throw IllegalStateException("Failed to initialize Firebase", e)
            }
        }
    }
}