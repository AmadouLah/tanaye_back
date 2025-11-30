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

    @Value("${app.storage.supabase.service-role-key:}")
    private String supabaseServiceRoleKey;

    /**
     * Valide la configuration de sécurité au démarrage
     */
    public void validerConfigurationSecurite() {
        log.info("🔐 Validation de la configuration de sécurité...");

        validerConfigurationJwt();
        validerConfigurationBaseDeDonnees();
        validerConfigurationSupabase();

        log.info("✅ Configuration de sécurité validée");
    }

    private void validerConfigurationJwt() {
        if (jwtSecret == null || jwtSecret.length() < 32) {
            log.error("❌ JWT secret manquant ou trop faible (minimum 32 caractères)");
            throw new IllegalStateException("JWT secret invalide");
        }
        log.info("✅ Configuration JWT validée");
    }

    private void validerConfigurationBaseDeDonnees() {
        if (databasePassword == null || databasePassword.isEmpty()) {
            log.error("❌ Mot de passe de base de données manquant");
            throw new IllegalStateException("Configuration de base de données incomplète");
        }
        log.info("✅ Configuration de base de données validée");
    }

    private void validerConfigurationSupabase() {
        if (supabaseServiceRoleKey == null || supabaseServiceRoleKey.isEmpty()) {
            log.warn("⚠️ Clé API Supabase manquante - fonctionnalités de stockage désactivées");
            return;
        }
        log.info("✅ Configuration Supabase validée");
    }
}
