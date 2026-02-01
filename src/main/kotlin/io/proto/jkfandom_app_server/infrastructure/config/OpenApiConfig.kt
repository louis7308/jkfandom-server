package io.proto.jkfandom_app_server.infrastructure.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    companion object {
        const val FIREBASE_BEARER = "FirebaseBearer"
    }

    @Bean
    fun openApi(): OpenAPI {
        val bearerScheme = SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description(
                """
Firebase ID Token을 사용합니다.
요청 헤더:
- Authorization: Bearer <firebase_id_token>
""".trimIndent()
            )

        return OpenAPI()
            .info(
                Info()
                    .title("JKFandom API")
                    .version("v1")
                    .description("JKFandom App Server API 문서")
            )
            .components(
                Components().addSecuritySchemes(FIREBASE_BEARER, bearerScheme)
            )
            // 기본: 전체 API는 인증 필요로 보이게
            .addSecurityItem(SecurityRequirement().addList(FIREBASE_BEARER))
    }
}