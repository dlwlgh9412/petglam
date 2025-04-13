package com.copago.petglam.config

import com.copago.petglam.filter.JwtAuthenticationFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class WebConfig(private val jwtAuthenticationFilter: JwtAuthenticationFilter) {

    /**
     * JWT 인증 필터 등록
     */
    @Bean
    fun jwtFilterRegistration(): FilterRegistrationBean<JwtAuthenticationFilter> {
        val registrationBean = FilterRegistrationBean<JwtAuthenticationFilter>()
        registrationBean.filter = jwtAuthenticationFilter
        registrationBean.order = Ordered.HIGHEST_PRECEDENCE + 100 // CORS 필터 다음에 실행
        return registrationBean
    }

    /**
     * CORS 필터 등록
     */
    @Bean
    fun corsFilter(): FilterRegistrationBean<CorsFilter> {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        
        // CORS 설정
        config.allowCredentials = true
        // 개발용 출처 (실제 환경에 맞게 설정 필요)
        config.addAllowedOriginPattern("*") // 모든 출처를 패턴으로 허용
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        config.maxAge = 3600L
        
        source.registerCorsConfiguration("/**", config)
        
        val bean = FilterRegistrationBean(CorsFilter(source))
        bean.order = Ordered.HIGHEST_PRECEDENCE
        
        return bean
    }
}
