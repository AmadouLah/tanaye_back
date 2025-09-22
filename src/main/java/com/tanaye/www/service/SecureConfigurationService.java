package com.tanaye.www.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service de configuration sécurisé
 * Valide les propriétés sensibles au démarrage
 */
@Slf4j
@Service
public class SecureConfigurationService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Value("${supabase.apiKey}")
    private String supabaseApiKey;

    /**
     * Valide la configuration de sécurité au démarrage
     */
    public void validateSecurityConfiguration() {
        log.info("🔐 Validation de la configuration de sécurité...");

        validateJwtConfiguration();
        validateDatabaseConfiguration();
        validateSupabaseConfiguration();

        log.info("✅ Configuration de sécurité validée");
    }

    private void validateJwtConfiguration() {
        if (jwtSecret == null || jwtSecret.length() < 32) {
            log.error("❌ JWT secret manquant ou trop faible (minimum 32 caractères)");
            throw new IllegalStateException("JWT secret invalide");
        }
        log.info("✅ Configuration JWT validée");
    }

    private void validateDatabaseConfiguration() {
        if (databasePassword == null || databasePassword.isEmpty()) {
            log.error("❌ Mot de passe de base de données manquant");
            throw new IllegalStateException("Configuration de base de données incomplète");
        }
        log.info("✅ Configuration de base de données validée");
    }

    private void validateSupabaseConfiguration() {
        if (supabaseApiKey == null || supabaseApiKey.isEmpty()) {
            log.error("❌ Clé API Supabase manquante");
            throw new IllegalStateException("Configuration Supabase incomplète");
        }
        log.info("✅ Configuration Supabase validée");
    }
}