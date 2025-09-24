package com.tanaye.www.service;

import com.tanaye.www.dto.AuthResponse;
import com.tanaye.www.dto.LoginRequest;
import com.tanaye.www.dto.RegisterRequest;
import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.repository.UtilisateurRepository;
import com.tanaye.www.security.CustomUserDetails;
import com.tanaye.www.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int MAX_TENTATIVES_CONNEXION = 5;

    @Transactional
    public AuthResponse authenticate(LoginRequest request) {
        log.info("Tentative de connexion pour l'identifiant: {}", request.getIdentifiant());

        // Trouver l'utilisateur par email ou téléphone
        Utilisateur utilisateur = trouverUtilisateurParIdentifiant(request.getIdentifiant())
                .orElseThrow(() -> new BadCredentialsException("Identifiants invalides"));

        // Vérifier le mot de passe
        if (!passwordEncoder.matches(request.getMotDePasse(), utilisateur.getMotDePasse())) {
            log.warn("Tentative de connexion échouée pour l'utilisateur: {}", utilisateur.getEmail());
            throw new BadCredentialsException("Identifiants invalides");
        }

        // Vérifier le statut de l'utilisateur
        if (!utilisateur.getStatut().name().equals("ACTIF")) {
            throw new BadCredentialsException("Compte désactivé");
        }

        // Authentifier avec Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        utilisateur.getEmail(),
                        request.getMotDePasse()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Générer le token JWT
        String token = jwtService.generateToken(new CustomUserDetails(utilisateur));

        // Mettre à jour la dernière connexion
        utilisateur.setDateModification(LocalDateTime.now());
        utilisateurRepository.save(utilisateur);

        log.info("Connexion réussie pour l'utilisateur: {}", utilisateur.getEmail());
        return AuthResponse.from(utilisateur, token);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Tentative d'inscription pour l'email: {}", request.getEmail());

        // Vérifier que l'email n'existe pas déjà
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Un compte avec cet email existe déjà");
        }

        // Vérifier que les mots de passe correspondent
        if (!request.getMotDePasse().equals(request.getConfirmationMotDePasse())) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas");
        }

        // Vérifications complémentaires (pas de données personnelles dans le mot de
        // passe)
        validerMotDePasseContexteUtilisateur(request);

        // Créer le nouvel utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setTelephone(request.getTelephone());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setRole(request.getRole());
        utilisateur.setEstVerifie(false);

        Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);

        // Générer le token JWT
        String token = jwtService.generateToken(new CustomUserDetails(savedUtilisateur));

        log.info("Inscription réussie pour l'utilisateur: {}", savedUtilisateur.getEmail());
        return AuthResponse.from(savedUtilisateur, token);
    }

    private Optional<Utilisateur> trouverUtilisateurParIdentifiant(String identifiant) {
        // Essayer d'abord par email
        Optional<Utilisateur> utilisateur = utilisateurRepository.findByEmail(identifiant);

        // Si pas trouvé par email, essayer par téléphone
        if (utilisateur.isEmpty()) {
            utilisateur = utilisateurRepository.findByTelephone(identifiant);
        }

        return utilisateur;
    }

    // ===== Validation complémentaire de mot de passe (contexte utilisateur) =====
    private void validerMotDePasseContexteUtilisateur(RegisterRequest request) {
        String pwd = request.getMotDePasse();
        if (pwd == null)
            return; // la contrainte @NotBlank gère le reste

        String lower = pwd.toLowerCase();
        // éviter email, nom, prénom, téléphone inclus dans le mot de passe
        if (containsNonTrivial(lower, safe(request.getEmail()))
                || containsNonTrivial(lower, safe(request.getNom()))
                || containsNonTrivial(lower, safe(request.getPrenom()))
                || containsNonTrivial(lower, safe(request.getTelephone()))) {
            throw new IllegalArgumentException("Le mot de passe ne doit pas contenir vos informations personnelles");
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    private boolean containsNonTrivial(String haystack, String needle) {
        if (needle.isBlank())
            return false;
        // ignorer symboles communs dans téléphone/mail
        String normalized = needle.replaceAll("[\\s._@+()-]", "");
        if (normalized.length() < 3)
            return false; // au moins 3 chars significatifs
        return haystack.contains(normalized);
    }
}
