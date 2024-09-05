package com.ayds.Cloudmerce.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.Cloudmerce.model.dto.RecoverPasswordConfirmationDto;
import com.ayds.Cloudmerce.model.dto.RecoverPasswordDto;
import com.ayds.Cloudmerce.model.dto.RegisteredUserDto;
import com.ayds.Cloudmerce.model.dto.SignIn2faDto;
import com.ayds.Cloudmerce.model.dto.SignInDto;
import com.ayds.Cloudmerce.model.dto.SignUpConfirmationDto;
import com.ayds.Cloudmerce.model.dto.SignUpDto;
import com.ayds.Cloudmerce.model.dto.TokenDto;
import com.ayds.Cloudmerce.model.dto.UserDto;
import com.ayds.Cloudmerce.model.exception.RequestConflictException;
import com.ayds.Cloudmerce.model.exception.ValueNotFoundException;
import com.ayds.Cloudmerce.service.AuthConfirmationService;
import com.ayds.Cloudmerce.service.EmailService;
import com.ayds.Cloudmerce.service.TemplateRendererService;
import com.ayds.Cloudmerce.service.TokenService;
import com.ayds.Cloudmerce.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthConfirmationService authConfirmationService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TemplateRendererService templateRendererService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/sign-up")
    public ResponseEntity<RegisteredUserDto> signUp(@RequestBody @Valid SignUpDto user) {
        RegisteredUserDto dbUser = userService.registerUser(user);

        String code = authConfirmationService.generateEmailConfirmationCode(dbUser.email());

        Map<String, Object> templateVariables = Map.of("code", code, "user", dbUser);

        String confirmationHtml = templateRendererService.renderTemplate("sign-up-confirmation", templateVariables);

        try {
            emailService.sendHtmlEmail("Cloudmerce", dbUser.email(),
                    "Confirmacion de usuario en Cloudmerce", confirmationHtml);
        } catch (MessagingException e) {
            throw new RequestConflictException("No se pudo enviar el correo de confirmacion");
        }

        return new ResponseEntity<>(dbUser, HttpStatus.CREATED);
    }

    @GetMapping("/sign-up/confirm")
    public ResponseEntity<String> confirmSignUp(@Valid SignUpConfirmationDto user) {
        boolean confirmed = authConfirmationService.confirmUserEmailCode(user.email(), user.code());

        return confirmed
                ? ResponseEntity.ok("La cuenta ha sido confirmada, puede cerrar esta ventana")
                : ResponseEntity.unprocessableEntity().body("La cuenta no se pudo confirmar");
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInDto user) {
        var authenticableUser = new UsernamePasswordAuthenticationToken(user.email(), user.password());
        authenticationManager.authenticate(authenticableUser);

        return ResponseEntity.accepted().build();
    }

    @PostMapping("/sign-in/2fa")
    public ResponseEntity<TokenDto> signIn2fa(@RequestBody @Valid SignIn2faDto user) {
        TokenDto token = userService.logInUserByEmailAndCode(user.email(), user.code())
                .map(tokenService::generateAccessToken)
                .orElseThrow(() -> new InsufficientAuthenticationException("La autenticacion en dos factores fallo"));

        return ResponseEntity.ok(token);
    }

    @PostMapping("/recover-password")
    public ResponseEntity<?> recoverPassword(@RequestBody @Valid RecoverPasswordDto user) {
        UserDto dbUser = userService.findUserByEmail(user.email())
                .orElseThrow(
                        () -> new ValueNotFoundException("No se encontro un usuario con el email " + user.email()));

        String code = authConfirmationService.generateEmailConfirmationCode(dbUser.email());

        Map<String, Object> templateVariables = Map.of("code", code, "user", dbUser);

        String confirmationHtml = templateRendererService.renderTemplate("recover-password", templateVariables);

        try {
            emailService.sendHtmlEmail("Cloudmerce", dbUser.email(), "Recuperacion de contraseña en Cloudmerce",
                    confirmationHtml);
        } catch (MessagingException e) {
            throw new RequestConflictException("No se pudo enviar el correo para la recuperacion de contraseña");
        }

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/recover-password/confirm")
    public ResponseEntity<TokenDto> confirmSignUp(@Valid RecoverPasswordConfirmationDto user) {
        boolean confirmed = authConfirmationService.confirmUserEmailCode(user.email(), user.code());

        if (!confirmed) {
            throw new RequestConflictException("No se logro confirmar el cambio de contraseña");
        }

        TokenDto token = userService.findUserByEmail(user.email())
                .map(tokenService::generateTemporalAccessToken)
                .get();

        return ResponseEntity.ok(token);
    }
}
