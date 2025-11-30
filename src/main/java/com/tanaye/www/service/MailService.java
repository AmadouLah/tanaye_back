package com.tanaye.www.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class MailService {

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";
    private static final Duration TIMEOUT_REQUETE = Duration.ofSeconds(30);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String brevoApiKey;
    private final String fromEmail;

    public MailService(
            @Value("${app.mail.brevo.api-key:}") String brevoApiKey,
            @Value("${app.mail.from:}") String fromEmail) {
        this.brevoApiKey = brevoApiKey;
        this.fromEmail = fromEmail;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT_REQUETE)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public void envoyerEmailVerification(String emailDestinataire, String code) {
        validerEntrees(emailDestinataire, code);
        validerConfiguration();

        String contenuTexte = construireContenuTexte(code);
        String contenuHtml = construireContenuHtml(code);
        BrevoEmailRequest requete = construireRequeteEmail(emailDestinataire, contenuTexte, contenuHtml);

        envoyerEmail(requete);
        log.info("Email de vérification envoyé à {} via Brevo", emailDestinataire);
    }

    private void validerEntrees(String emailDestinataire, String code) {
        if (emailDestinataire == null || emailDestinataire.trim().isEmpty()) {
            throw new IllegalArgumentException("Email destinataire requis");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Code de vérification requis");
        }
    }

    private void validerConfiguration() {
        if (brevoApiKey == null || brevoApiKey.isEmpty()) {
            log.error("BREVO_API_KEY n'est pas configurée");
            throw new RuntimeException("Configuration email manquante");
        }
    }

    private String construireContenuTexte(String code) {
        return String.format(
                "Bonjour,%n%n" +
                        "Voici votre code de vérification: %s%n" +
                        "Ce code expire dans 15 minutes.%n%n" +
                        "Si vous n'êtes pas à l'origine de cette demande, vous pouvez ignorer cet email.%n%n" +
                        "Cordialement,",
                code);
    }

    private String construireContenuHtml(String code) {
        return String.format(
                "<div style='font-family: Arial, sans-serif;'>" +
                        "<h2>Vérification de votre compte</h2>" +
                        "<p>Bonjour,</p>" +
                        "<p>Voici votre code de vérification: <strong style='font-size: 18px; color: #007bff;'>%s</strong></p>"
                        +
                        "<p>Ce code expire dans 15 minutes.</p>" +
                        "<p>Si vous n'êtes pas à l'origine de cette demande, vous pouvez ignorer cet email.</p>" +
                        "<p>Cordialement,<br>L'équipe Tanaye</p>" +
                        "</div>",
                code);
    }

    private BrevoEmailRequest construireRequeteEmail(String emailDestinataire, String contenuTexte,
            String contenuHtml) {
        return BrevoEmailRequest.builder()
                .sender(new BrevoSender("Tanaye", fromEmail))
                .to(List.of(new BrevoRecipient(emailDestinataire)))
                .subject("Vérification de votre compte")
                .textContent(contenuTexte)
                .htmlContent(contenuHtml)
                .build();
    }

    private void envoyerEmail(BrevoEmailRequest requete) {
        try {
            String corpsJson = objectMapper.writeValueAsString(requete);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(BREVO_API_URL))
                    .header("api-key", brevoApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(corpsJson))
                    .timeout(TIMEOUT_REQUETE)
                    .build();

            HttpResponse<String> reponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (reponse.statusCode() < 200 || reponse.statusCode() >= 300) {
                log.error("Erreur Brevo API - Status: {}, Body: {}", reponse.statusCode(), reponse.body());
                throw new RuntimeException("Erreur lors de l'envoi de l'email: " + reponse.statusCode());
            }
        } catch (Exception e) {
            log.error("Echec envoi d'email via Brevo: {}", e.getMessage(), e);
            throw new RuntimeException("Impossible d'envoyer l'email de vérification", e);
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class BrevoEmailRequest {
        private BrevoSender sender;
        private List<BrevoRecipient> to;
        private String subject;
        private String textContent;
        private String htmlContent;
    }

    @Data
    @AllArgsConstructor
    private static class BrevoSender {
        private String name;
        private String email;
    }

    @Data
    @AllArgsConstructor
    private static class BrevoRecipient {
        private String email;
    }
}
