package com.ayds.Cloudmerce.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.ayds.Cloudmerce.model.dto.TokenDto;
import com.ayds.Cloudmerce.model.dto.UserDto;

@Service
public class TokenService {

    @Value("${security.jwt.token.expiration-time-min}")
    private long expirationTimeMin;

    @Autowired
    private Algorithm algorithm;

    public TokenDto generateAccessToken(UserDto user) {
        try {
            return new TokenDto(JWT.create()
                    .withSubject(String.valueOf(user.id()))
                    .withClaim("email", user.email())
                    .withExpiresAt(genAccessExpirationDate())
                    .sign(algorithm));
        } catch (JWTCreationException exception) {
            throw new JWTCreationException("Error while generating token", exception);
        }
    }

    public String getIdFromToken(String token) {
        try {
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