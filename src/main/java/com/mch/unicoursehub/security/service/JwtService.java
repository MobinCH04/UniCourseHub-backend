package com.mch.unicoursehub.security.service;

import com.mch.unicoursehub.ConstVal;
import com.mch.unicoursehub.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Service for generating, validating, and extracting information from JWT tokens.
 *
 * <p>
 * This service supports:
 * <ul>
 *     <li>Generating access and refresh tokens with role-specific expiration times</li>
 *     <li>Extracting claims like username and UUID from JWTs</li>
 *     <li>Validating token authenticity and expiration</li>
 * </ul>
 * </p>
 */
@Service
@Slf4j
public class JwtService {

    /**
     * Extracts the username (subject) from the JWT token.
     *
     * @param token the JWT token
     * @return the username contained in the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the UUID claim from the JWT token.
     *
     * @param token the JWT token
     * @return the UUID stored in the token
     */
    public String extractUUID(String token) {
        return extractClaim(token, claims -> (String) claims.get(ConstVal.UUID_KEY));
    }

    /**
     * Extracts a specific claim from the JWT token using a claims resolver function.
     *
     * @param token          the JWT token
     * @param claimsResolver function to extract the specific claim from {@link Claims}
     * @param <T>            type of the claim to return
     * @return the extracted claim value
     * @throws ExpiredJwtException if the token is expired
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws ExpiredJwtException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JWT access token for the given user with a UUID claim.
     * The expiration time is determined by the user's role.
     *
     * @param userDetails the authenticated user details
     * @param uuid        a unique identifier for the token
     * @return a JWT access token string
     */
    public String generateToken(UserDetails userDetails, UUID uuid) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(ConstVal.UUID_KEY, uuid);

        long expiration;
        User user = (User) userDetails;
        switch (user.getRole()) {
            case STUDENT -> expiration = ConstVal.JWT_EXPIRATION_STUDENT;
            case PROFESSOR -> expiration = ConstVal.JWT_EXPIRATION_PROFESSOR;
            case ADMIN -> expiration = ConstVal.JWT_EXPIRATION_ADMIN;
            default -> expiration = ConstVal.JWT_EXPIRATION_STUDENT;
        }

        return generateToken(extraClaims, userDetails, expiration);
    }


    /**
     * Generates a JWT token with custom claims and expiration.
     *
     * @param extraClaims extra claims to include in the token
     * @param userDetails user details for the subject
     * @param expiration  token expiration in milliseconds
     * @return a JWT token string
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return buildToken(extraClaims, userDetails, expiration);
    }

    /**
     * Generates a refresh token with a fixed expiration time.
     *
     * @param extraClaims extra claims to include in the token
     * @param userDetails user details for the subject
     * @return a JWT refresh token string
     */
    public String generateRefreshToken(Map<String, Object> extraClaims,UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, ConstVal.REFRESH_EXPIRATION);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
    }

    // ====================== Private Helper Methods ======================

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(ConstVal.SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
