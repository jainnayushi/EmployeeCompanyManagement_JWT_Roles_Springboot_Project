package com.assignment.EmployeeCompany.config;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtGeneratorValidator {

    @Value("${EmployeeCompany.app.jwtSecret}")
    private String SECRET;

    @Value("${EmployeeCompany.app.jwtExpirationMs}")
    private int jwtExpirationMs;
    private final Logger logger = LoggerFactory.getLogger(JwtGeneratorValidator.class);

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Claims extractUserRole(String token) {
        return extractAllClaims(token);
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

//    public String generateToken(String username) {
//        Map<String, Object> claims = new HashMap<>();
//        return createToken(claims, username);
//    }
    public String generateToken(Authentication authentication) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, authentication);
    }
    private String createToken(Map<String, Object> claims, Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + TimeUnit.MINUTES.toMillis(jwtExpirationMs)); // 1 minute expiration time
        String role = authentication.getAuthorities().stream()
                .map(r -> r.getAuthority()).collect(Collectors.toSet()).iterator().next();
       String token = Jwts.builder().claim("role", role).setSubject(authentication.getName()).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();

        logger.trace("Generated JWT token for user '{}'", authentication);
        return token;
    }

//        private String createToken(Map<String, Object> claims, String subject) {
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + TimeUnit.MINUTES.toMillis(jwtExpirationMs)); // 1 minute expiration time
//
//        String token = Jwts.builder()
//                .setClaims(claims)
//                .setSubject(subject)
//                .setIssuedAt(now)
//                .setExpiration(expiryDate)
//                .signWith(SignatureAlgorithm.HS256, SECRET)
//                .compact();
//        logger.trace("Generated JWT token for user '{}'", subject);
//        return token;
//    }

    public Boolean validateToken(String token, UserDetails userDetails) throws Exception {
        try {
            final String username = extractUsername(token);
            boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            if (!isValid) {
                throw new Exception("Invalid JWT token for user '" + userDetails.getUsername() + "'");
            }
            return isValid;
        } catch (ExpiredJwtException ex) {
            logger.warn("Expired JWT token for user '{}'", userDetails.getUsername());
            throw new Exception("Expired JWT token for user '" + userDetails.getUsername() + "'");
        } catch (JwtException ex) {
            logger.warn("Invalid JWT token for user '{}'", userDetails.getUsername());
            throw ex;
        } catch (Exception ex) {
            logger.error("Error validating JWT token for user '{}'", userDetails.getUsername(), ex);
            throw new Exception("Invalid JWT token for user '" + userDetails.getUsername() + "'");
        }
    }
    public UsernamePasswordAuthenticationToken getAuthenticationToken(final String token, final Authentication existingAuth, final UserDetails userDetails) {
        Claims claims = extractAllClaims(token);
        final Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("role").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }
}