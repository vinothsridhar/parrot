package ai.sridhar.faq.configs

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*


@Configuration
class SecurityConfig {

    @Value("\${spring.security.enabled:false}")
    val securityEnabled : Boolean = true

    private val ignoreEndpoints = arrayOf(
        "/actuator/**",
        "/v3/api-docs/**",
        "/configuration/ui",
        "/swagger-resources/**",
        "/configuration/security",
        "/swagger-ui.html",
        "/webjars/**",
        "/swagger-ui/**",
        "/v1/health",
        "/webhooks/**"
    )

    @Bean
    fun securityFilterChain(http: HttpSecurity) : SecurityFilterChain {
        if (securityEnabled) {
            http
                .cors {}
                .csrf { it.disable() }
                .authorizeHttpRequests {
                    it.requestMatchers(*ignoreEndpoints)
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                }
                .oauth2ResourceServer { it.jwt {} }
        } else {
            http
                .cors {}
                .csrf { it.disable() }
                .authorizeHttpRequests {
                    it.anyRequest().permitAll()
                }
        }
        return http.build()
    }
}

@Configuration
class InterceptorConfig(
    private val headersInterceptor: HeadersInterceptor,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(headersInterceptor).addPathPatterns(
            "/api/**",
        )
    }
}


@Configuration
class HeadersInterceptor(
    val requestContext: RequestContext,
) : HandlerInterceptor {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val requestId = request.getHeader("Request-Id") ?: UUID.randomUUID().toString()
        requestContext.setRequestId(requestId)

        logger.info("request[$requestId]: ${request.requestURI} - principal: ${requestContext.principal}")

        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?,
    ) {
        val latency = ChronoUnit.MILLIS.between(requestContext.requestTime, LocalDateTime.now())
        logger.info("response[${requestContext.requestId}]: status: ${response.status}[$latency ms]")
        requestContext.clear()
    }
}

data class UserPrincipal(
    val employeeId: String,
    val employeeCode: String,
    var role: String = "user",
    var read: Boolean = false,
    var write: Boolean = false
) {
    private val adminRoles = listOf("admin", "superadmin")

    fun isAdmin() = this.role in adminRoles
    fun isMember() = "member".contentEquals(this.role)

    fun isPartner() = "partner".contentEquals(this.role)
}
