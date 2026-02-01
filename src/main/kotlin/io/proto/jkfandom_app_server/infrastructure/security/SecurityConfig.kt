package io.proto.jkfandom_app_server.infrastructure.security

import io.proto.jkfandom_app_server.shared.exception.ErrorCode
import io.proto.jkfandom_app_server.shared.response.ApiFandomResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import tools.jackson.databind.ObjectMapper

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val firebaseAuthenticationFilter: FirebaseAuthenticationFilter,
    private val objectMapper: ObjectMapper
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/api/docs/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                    ).permitAll()
                    .requestMatchers(HttpMethod.POST, "/v1/auth/social/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/v1/onboarding/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/v1/onboarding/nickname/validate").permitAll()
                    .requestMatchers(HttpMethod.POST, "/v1/auth/status").permitAll()
                    .requestMatchers(HttpMethod.POST, "/v1/users/onboarding").authenticated()
                    .requestMatchers("/actuator/health").permitAll()
                    .requestMatchers("/v1/**").access(activeUserAuthorizationManager())
                    .anyRequest().authenticated()
            }
            .addFilterBefore(firebaseAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling { exception ->
                exception
                    .authenticationEntryPoint { _, response, _ ->
                        val body = ApiFandomResponse.error(ErrorCode.UNAUTHORIZED)

                        response.status = ErrorCode.UNAUTHORIZED.httpStatus.value()
                        response.contentType = MediaType.APPLICATION_JSON_VALUE
                        response.characterEncoding = "UTF-8"
                        response.writer.write(objectMapper.writeValueAsString(body))
                    }
                    .accessDeniedHandler { _, response, _ ->
                        val body = ApiFandomResponse.error(ErrorCode.FORBIDDEN)

                        response.status = ErrorCode.FORBIDDEN.httpStatus.value()
                        response.contentType = MediaType.APPLICATION_JSON_VALUE
                        response.characterEncoding = "UTF-8"
                        response.writer.write(objectMapper.writeValueAsString(body))
                    }
            }

        return http.build()
    }

    private fun activeUserAuthorizationManager(): AuthorizationManager<RequestAuthorizationContext> =
        AuthorizationManager { authentication, _ ->
            val principal = authentication.get()?.principal as? CurrentUserPrincipal
            AuthorizationDecision(principal?.isActive == true)
        }
}