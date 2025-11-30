package com.tanaye.www.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret:mySecretKey123456789012345678901234567890}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24 heures par défaut
    private Long jwtExpiration;

    public String extraireNomUtilisateur(String token) {
        return extraireReclamation(token, Claims::getSubject);
    }

    public <T> T extraireReclamation(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraireToutesReclamations(token);
        return claimsResolver.apply(claims);
    }

    public String genererToken(UserDetails userDetails) {
        return genererToken(new HashMap<>(), userDetails);
    }

    public String genererToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return construireToken(extraClaims, userDetails, jwtExpiration);
    }

    private String construireToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(obtenirCleSignature(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean estTokenValide(String token, UserDetails userDetails) {
        final String nomUtilisateur = extraireNomUtilisateur(token);
        return (nomUtilisateur.equals(userDetails.getUsername())) && !estTokenExpire(token);
    }

    private boolean estTokenExpire(String token) {
        return extraireExpiration(token).before(new Date());
    }

    private Date extraireExpiration(String token) {
        return extraireReclamation(token, Claims::getExpiration);
    }

    private Claims extraireToutesReclamations(String token) {
        return Jwts.parser()
                .verifyWith(obtenirCleSignature())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey obtenirCleSignature() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
