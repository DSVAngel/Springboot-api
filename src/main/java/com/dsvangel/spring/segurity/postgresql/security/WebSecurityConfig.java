package com.dsvangel.spring.segurity.postgresql.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.dsvangel.spring.segurity.postgresql.security.jwt.AuthEntryPointJwt;
import com.dsvangel.spring.segurity.postgresql.security.jwt.AuthTokenFilter;
import com.dsvangel.spring.segurity.postgresql.security.services.UserDetailsServiceImpl;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {
    
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - Authentication
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        
                        // Public endpoints - Tweets (GET only)
                        .requestMatchers(HttpMethod.GET, "/api/tweets/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tweets/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tweets/user/{userId}").permitAll()
                        
                        // Public endpoints - Reaction Types
                        .requestMatchers(HttpMethod.GET, "/api/reaction-types/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reaction-types/available").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reaction-types/{id}").permitAll()
                        
                        // Public endpoints - Tweet Reactions (GET only)
                        .requestMatchers(HttpMethod.GET, "/api/reactions/tweet/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reactions/user/**").permitAll()
                        
                        // Public endpoints - Comments (GET only)
                        .requestMatchers(HttpMethod.GET, "/api/comments/tweet/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/comments/*/replies").permitAll()
                        
                        // Protected endpoints - require authentication
                        .requestMatchers(HttpMethod.POST, "/api/tweets/create").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/tweets/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/tweets/**").authenticated()
                        
                        .requestMatchers(HttpMethod.POST, "/api/reactions/create").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/reactions/**").authenticated()
                        
                        .requestMatchers(HttpMethod.POST, "/api/comments/create").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/comments/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/comments/**").authenticated()
                        
                        // Admin-only endpoints
                        .requestMatchers("/api/reaction-types/create").hasRole("ADMIN")
                        .requestMatchers("/api/reaction-types/init").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/reaction-types/**").hasRole("ADMIN")
                        
                        // Test endpoints
                        .requestMatchers("/api/test/admin").hasRole("ADMIN")
                        .requestMatchers("/api/test/mod").hasRole("MODERATOR")
                        .requestMatchers("/api/test/user").hasAnyRole("USER", "MODERATOR", "ADMIN")
                        
                        // Default - require authentication for anything else
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}