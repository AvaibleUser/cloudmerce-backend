package com.ayds.Cloudmerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayds.Cloudmerce.model.dto.RegisteredUserDto;
import com.ayds.Cloudmerce.model.dto.SignUpDto;
import com.ayds.Cloudmerce.model.dto.UserDto;
import com.ayds.Cloudmerce.model.entity.PaymentMethodEntity;
import com.ayds.Cloudmerce.model.entity.PermissionEntity;
import com.ayds.Cloudmerce.model.entity.RoleEntity;
import com.ayds.Cloudmerce.model.entity.UserEntity;
import com.ayds.Cloudmerce.model.entity.UserPermissionEntity;
import com.ayds.Cloudmerce.model.exception.BadRequestException;
import com.ayds.Cloudmerce.model.exception.ValueNotFoundException;
import com.ayds.Cloudmerce.repository.PaymentMethodRepository;
import com.ayds.Cloudmerce.repository.RoleRepository;
import com.ayds.Cloudmerce.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private GoogleAuthService googleAuthService;

    @Autowired
    private ConcurrentMap<String, String> signUpCondifmationCodes;

    private UserDto toUserDto(UserEntity user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getAddress(), user.getNit(),
                user.getCreatedAt(), user.getRole().getName(),
                user.getPaymentPreference().getName(),
                user.getUserPermissions() == null ? List.of()
                        : user.getUserPermissions()
                                .stream()
                                .map(UserPermissionEntity::getPermission)
                                .map(PermissionEntity::getName)
                                .toList());
    }

    private RegisteredUserDto toRegisteredUserDto(UserEntity user) {
        return new RegisteredUserDto(user.getId(), user.getName(), user.getEmail(), user.getAddress(), user.getNit(),
                user.getGoogleAuthKey(), user.getCreatedAt(), user.getRole().getName(),
                googleAuthService.generateGoogleAuthQrUrl("Cloudmerce", user.getName(), user.getGoogleAuthKey()),
                user.getPaymentPreference().getName(),
                user.getUserPermissions() == null ? List.of()
                        : user.getUserPermissions()
                                .stream()
                                .map(UserPermissionEntity::getPermission)
                                .map(PermissionEntity::getName)
                                .toList());
    }

    public Optional<UserDto> findUserById(Long userId) {
        return userRepository.findById(userId).map(this::toUserDto);
    }

    public Optional<UserDto> findUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toUserDto);
    }

    public Optional<UserDto> logInUserByEmailAndCode(String email, int code) {
        return userRepository.findByEmail(email)
                .filter(user -> googleAuthService.authencateUserWithGoogleAuth(user.getGoogleAuthKey(), code))
                .map(this::toUserDto);
    }

    public UserDto changeUserPassword(Long userId, String password, String repeatedPassword) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ValueNotFoundException("The user was not found"));

        String encryptedPassword = encoder.encode(password);
        if (encoder.matches(repeatedPassword, encryptedPassword)) {
            throw new BadRequestException("Las contraseñas no coinciden");
        }

        user.setPassword(encryptedPassword);

        return toUserDto(userRepository.save(user));
    }

    @Transactional
    public RegisteredUserDto registerUser(SignUpDto user) {
        if (userRepository.existsByEmail(user.email())) {
            throw new DuplicateKeyException("Email already exists");
        }
        String encryptedPassword = encoder.encode(user.password());

        RoleEntity role = roleRepository.findById(user.roleId()).orElseThrow();
        PaymentMethodEntity paymentMethod = paymentMethodRepository.findById(user.paymentPreferenceId()).orElseThrow();

        UserEntity newUser = new UserEntity(user.name(), user.email(), user.address(), user.nit(), encryptedPassword,
                googleAuthService.getUserGoogleAuthKey(), role, paymentMethod);

        return toRegisteredUserDto(userRepository.save(newUser));
    }

    @Transactional
    public Authentication authenticateUser(Authentication authUser) throws AuthenticationException {
        String email = authUser.getPrincipal().toString();
        String password = authUser.getCredentials().toString();

        if (signUpCondifmationCodes.containsKey(email)) {
            throw new InsufficientAuthenticationException("La cuenta aun no se ha confirmado");
        }

        Optional<UserEntity> optUser = userRepository.findByEmail(email);

        UserEntity user = optUser.filter(dbUser -> encoder.matches(password, dbUser.getPassword()))
                .orElseThrow(() -> new BadCredentialsException("El email o la contraseña es incorrecta"));

        List<SimpleGrantedAuthority> authorities = user.getUserPermissions()
                .stream()
                .map(UserPermissionEntity::getPermission)
                .map(PermissionEntity::getName)
                .map("PERMISSION_"::concat)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toCollection(ArrayList::new));

        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

        return new UsernamePasswordAuthenticationToken(email, password, authorities);
    }
}
