package com.tanaye.www.service;

import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.enums.RoleUtilisateur;
import com.tanaye.www.enums.StatutUtilisateur;
import com.tanaye.www.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service UserDetailsService
 * Pour l'instant, implémentation basique pour permettre le démarrage
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Implémentation temporaire - à remplacer par la vraie logique
        if (username == null || username.isEmpty()) {
            throw new UsernameNotFoundException("Nom d'utilisateur requis");
        }

        // Créer un utilisateur factice pour le démarrage
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(username);
        utilisateur.setMotDePasse("$2a$10$dummy"); // Mot de passe hashé factice
        utilisateur.setRole(RoleUtilisateur.VOYAGEUR);
        utilisateur.setStatut(StatutUtilisateur.ACTIF);

        return new CustomUserDetails(utilisateur);
    }
}
