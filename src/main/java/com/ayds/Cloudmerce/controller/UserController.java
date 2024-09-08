package com.ayds.Cloudmerce.controller;

import static java.util.function.Predicate.not;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.Cloudmerce.model.dto.GoogleAuthDto;
import com.ayds.Cloudmerce.model.dto.GoogleAuthKeyDto;
import com.ayds.Cloudmerce.model.dto.PasswordChangeDto;
import com.ayds.Cloudmerce.model.dto.TokenDto;
import com.ayds.Cloudmerce.model.dto.UserDto;
import com.ayds.Cloudmerce.model.dto.UserWithGoogleSecretDto;
import com.ayds.Cloudmerce.model.exception.BadRequestException;
import com.ayds.Cloudmerce.service.GoogleAuthService;
import com.ayds.Cloudmerce.service.TokenService;
import com.ayds.Cloudmerce.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private GoogleAuthService googleAuthService;

    @Autowired
    private TokenService tokenService;

    private TokenDto addTokenToUserData(UserWithGoogleSecretDto user) {
        String token = tokenService.generateAccessToken(user.id());
        return new TokenDto(token, user.id(), user.name(), user.email(), user.role(), user.paymentMethod());
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyInformation(@NonNull HttpServletRequest request) {
        long id = tokenService.getIdFromToken(request);

        return ResponseEntity.of(userService.findUserById(id));
    }

    @PatchMapping("/password")
    public ResponseEntity<TokenDto> changePassword(@NonNull HttpServletRequest request,
            @RequestBody @Valid PasswordChangeDto user) {
        long id = tokenService.getIdFromToken(request);

        UserWithGoogleSecretDto dbUser = userService.changeUserPassword(id, user.password(), user.repeatedPassword());

        return ResponseEntity.ok(addTokenToUserData(dbUser));
    }

    @GetMapping("/multifactor-authentication")
    public ResponseEntity<GoogleAuthDto> generateMultiFactorAuthentication(@NonNull HttpServletRequest request) {
        long id = tokenService.getIdFromToken(request);

        GoogleAuthDto googleAuth = userService.findUserById(id)
                .filter(not(UserDto::hasMultiFactorAuth))
                .map(user -> {
                    String googleAuthKey = googleAuthService.getUserGoogleAuthKey();
                    String qrUrl = googleAuthService.generateGoogleAuthQrUrl("Cloudmerce", user.name(), googleAuthKey);
                    return new GoogleAuthDto(qrUrl, googleAuthKey);
                })
                .orElseThrow(() -> new BadRequestException(
                        "El usuario ya tiene activada la autenticacion por dos factores"));

        return ResponseEntity.ok(googleAuth);
    }

    @PatchMapping("/multifactor-authentication")
    public ResponseEntity<UserDto> addMultiFactorAuthentication(@NonNull HttpServletRequest request,
            @RequestBody GoogleAuthKeyDto googleKey) {
        long id = tokenService.getIdFromToken(request);

        if (!googleAuthService.authencateUserWithGoogleAuth(googleKey.authKey(), googleKey.code())) {
            throw new BadRequestException("El codigo no es valido");
        }
        UserDto user = userService.addGoogleAuthentication(id, googleKey.authKey());

        return ResponseEntity.ok(user);
    }
}
