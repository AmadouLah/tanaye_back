package com.tanaye.www.configuration;

import com.tanaye.www.security.JwtAuthenticationFilter;
import com.tanaye.www.service.UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    // ===== CONSTANTES DE CONFIGURATION =====

    // Endpoints publics
    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/**"
    };

    // Documentation et monitoring
    private static final String[] DOCUMENTATION_ENDPOINTS = {
            "/actuator/**",
            "/swagger-ui/**", "/swagger-ui.html", "/swagger-ui/index.html",
            "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**"
    };

    // WebSocket
    private static final String[] WEBSOCKET_ENDPOINTS = {
            "/ws/**"
    };

    // Données de référence publiques
    private static final String[] REFERENCE_DATA_ENDPOINTS = {
            "/api/pays/**",
            "/api/regions/**",
            "/api/villes/**"
    };

    // Endpoints authentifiés
    private static final String[] AUTHENTICATED_ENDPOINTS = {
            "/api/utilisateurs/**",
            "/api/voyages/**",
            "/api/colis/**",
            "/api/avis/**",
            "/api/messages/**",
            "/api/localisations/**",
            "/api/notifications/**",
            "/api/paiements/**",
            "/api/recus/**",
            "/api/reclamations/**",
            "/api/incidents/**",
            "/api/historiques/**"
    };

    // CORS - Origines autorisées
    private static final String[] ALLOWED_ORIGINS = {
            "https://tanaye.vercel.app",
            "http://localhost:3000",
            "http://localhost:4200",
            "http://localhost:5173"
    };

    // CORS - Méthodes autorisées
    private static final String[] ALLOWED_METHODS = {
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        // Endpoints publics
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()

                        // Documentation et monitoring
                        .requestMatchers(DOCUMENTATION_ENDPOINTS).permitAll()

                        // WebSocket
                        .requestMatchers(WEBSOCKET_ENDPOINTS).permitAll()

                        // Données de référence publiques
                        .requestMatchers(REFERENCE_DATA_ENDPOINTS).permitAll()

                        // Endpoints authentifiés
                        .requestMatchers(AUTHENTICATED_ENDPOINTS).authenticated()

                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Configuration CORS
        configuration.setAllowedOrigins(List.of(ALLOWED_ORIGINS));
        configuration.setAllowedMethods(List.of(ALLOWED_METHODS));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}