package com.tanaye.www.service;

import com.tanaye.www.entity.Utilisateur;
import com.tanaye.www.repository.UtilisateurRepository;
import com.tanaye.www.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String identifiant) throws UsernameNotFoundException {
        if (identifiant == null || identifiant.isBlank()) {
            throw new UsernameNotFoundException("Identifiant requis");
        }

        Utilisateur utilisateur = utilisateurRepository.findByEmail(identifiant)
                .or(() -> utilisateurRepository.findByTelephone(identifiant))
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

        return new CustomUserDetails(utilisateur);
    }
}
