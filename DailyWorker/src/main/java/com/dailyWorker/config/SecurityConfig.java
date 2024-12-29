package com.dailyWorker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.dailyWorker.security.AuthenticationFilter;
import com.dailyWorker.security.TokenEntryPoint;

@Configuration
public class SecurityConfig {
    @Autowired
    private TokenEntryPoint point;
    @Autowired
    private AuthenticationFilter filter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // configuration
        http.csrf(csrf->csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
	            		 .requestMatchers("/auth/register-user").permitAll() // Allow unauthenticated access to registration endpoint
	            		 .requestMatchers("/auth/register-admin").permitAll()
	            		 .requestMatchers("/auth/register-worker").permitAll()
	            		 .requestMatchers("/auth/login").permitAll() // Allow unauthenticated access to login endpoint
	                     .requestMatchers("/admin/**").hasRole("ADMIN") // Restrict access to admin routes to ADMIN role
	                     .requestMatchers("/user/**").hasRole("USER")
	                     .requestMatchers("/worker/**").hasRole("WORKER")// Restrict access to user routes to USER role
	                     .anyRequest().authenticated()  // All other routes require authentication
	            )
                        .exceptionHandling(ex->ex.authenticationEntryPoint(point))
                        .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }

    private UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");  // Allows all origins
        config.addAllowedHeader("*");  // Allows all headers
        config.addAllowedMethod("*");  // Allows all HTTP methods

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // Applies this CORS configuration to all endpoints
        return source;
    }
}
