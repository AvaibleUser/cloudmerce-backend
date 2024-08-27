package com.ayds.Cloudmerce.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.ayds.Cloudmerce.model.entity.UserEntity;

@Service
public class TokenService {

    @Value("${security.jwt.token.secret-key}")
    private String JWT_SECRET;

    @Value("${security.jwt.token.expiration-time-min}")
    private long expirationTimeMin;

    private Algorithm algorithm;

    public String generateAccessToken(UserEntity user) {
        try {
            if (algorithm == null) {
                algorithm = Algorithm.HMAC256(JWT_SECRET);
            }

            return JWT.create()
                    .withSubject(String.valueOf(user.getId()))
                    .withClaim("name", user.getName())
                    .withExpiresAt(genAccessExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new JWTCreationException("Error while generating token", exception);
        }
    }

    public String validateToken(String token) {
        try {
            if (algorithm == null) {
                algorithm = Algorithm.HMAC256(JWT_SECRET);
            }

            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Error while validating token", exception);
        }
    }

    private Instant genAccessExpirationDate() {
        return LocalDateTime.now()
                .plusMinutes(expirationTimeMin)
                .toInstant(ZoneOffset.of("-06:00"));
    }
}