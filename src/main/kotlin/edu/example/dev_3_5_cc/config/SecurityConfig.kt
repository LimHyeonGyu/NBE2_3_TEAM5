package edu.example.dev_3_5_cc.config

import edu.example.dev_3_5_cc.jwt.JWTFilter
import edu.example.dev_3_5_cc.jwt.JWTUtil
import edu.example.dev_3_5_cc.jwt.LoginFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val jwtUtil: JWTUtil
) {

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(configuration: AuthenticationConfiguration): AuthenticationManager {
        return configuration.authenticationManager
    }

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        http.csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/login", "/signup","/",
                    "cc/reply", "/cc/member", "/cc/product/**",
                    "/cc/review/**", "/cc/board", "/cc/board/{boardId}",
                    "/css/**", "/js/**", "/images/**", "/app/**", "/uploadPath/**"
                ).permitAll()
                    .requestMatchers("/cc/mypage/**", "cc/memberImage/**").hasRole("USER")
                    .requestMatchers("/cc/admin/**", "cc/productImage/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .addFilterBefore(JWTFilter(jwtUtil), LoginFilter::class.java)
            .addFilterAt(
                LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

        return http.build()
    }
}