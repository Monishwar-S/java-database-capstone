package com.project.back_end.service;

import com.project.back_end.repository.AdminRepository;
import com.project.back_end.repository.DoctorRepository;
import com.project.back_end.repository.PatientRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey signingKey;

    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // --- Generate JWT token for a given identifier ---
    public String generateToken(String identifier) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 7L * 24 * 60 * 60 * 1000); // 7 days

        return Jwts.builder()
                .setSubject(identifier)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // --- Extract identifier (subject) from token ---
    public String extractIdentifier(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // --- Validate token for specific user type ---
    public boolean validateToken(String token, String userType) {
        try {
            String identifier = extractIdentifier(token);

            switch (userType.toLowerCase()) {
                case "admin":
                    return adminRepository.existsByUsername(identifier);
                case "doctor":
                    return doctorRepository.existsByEmail(identifier);
                case "patient":
                    return patientRepository.existsByEmail(identifier);
                default:
                    return false;
            }
        } catch (Exception e) {
            return false; // invalid token
        }
    }

    // --- Retrieve signing key ---
    public SecretKey getSigningKey() {
        return signingKey;
    }
}
