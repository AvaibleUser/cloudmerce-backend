package com.ayds.Cloudmerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayds.Cloudmerce.model.dto.SignUpDto;
import com.ayds.Cloudmerce.model.dto.UserChangeDto;
import com.ayds.Cloudmerce.model.dto.UserDto;
import com.ayds.Cloudmerce.model.dto.UserWithGoogleSecretDto;
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

    private UserWithGoogleSecretDto toUserForGoogleAuth(UserEntity user) {
        return new UserWithGoogleSecretDto(user.getId(), user.getName(), user.getEmail(), user.getRole().getName(),
                user.getPaymentPreference().getName(), user.getGoogleAuthKey());
    }

    private UserDto toUserDto(UserEntity user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getAddress(), user.getNit(),
                user.getCreatedAt(), user.getGoogleAuthKey() != null, user.getRole().getName(),
                user.getPaymentPreference().getName(),
                user.getUserPermissions() == null
                        ? List.of()
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

    public Optional<UserWithGoogleSecretDto> findUserWithGoogleKeyByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toUserForGoogleAuth);
    }

    @Transactional
    public UserWithGoogleSecretDto changeUserPassword(Long userId, String password, String repeatedPassword) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar los registros del usuario"));

        String encryptedPassword = encoder.encode(password);
        if (encoder.matches(repeatedPassword, encryptedPassword)) {
            throw new BadRequestException("Las contraseñas no coinciden");
        }

        user.setPassword(encryptedPassword);

        return toUserForGoogleAuth(userRepository.save(user));
    }

    @Transactional
    public UserDto changeUserRole(Long userId, String role) {
        RoleEntity dbRole = Optional.of(role)
                .filter("ADMIN"::equals)
                .flatMap(roleRepository::findByName)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar el role"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar los registros del usuario"));

        user.setRole(dbRole);

        return toUserDto(userRepository.save(user));
    }

    @Transactional
    public UserWithGoogleSecretDto changeUserInfo(Long userId, UserChangeDto user) {
        UserEntity dbUser = userRepository.findById(userId).get();

        user.address().ifPresent(dbUser::setAddress);

        user.paymentMethod().map(paymentMethodRepository::findByName).ifPresent(dbUser::setPaymentPreference);

        user.currentPassword()
                .filter(passwd -> user.newPassword().isPresent())
                .filter(passwd -> encoder.matches(passwd, dbUser.getPassword()))
                .flatMap(passwd -> user.newPassword())
                .map(encoder::encode)
                .ifPresent(dbUser::setPassword);

        return toUserForGoogleAuth(dbUser);
    }

    @Transactional
    public UserDto addGoogleAuthentication(Long userId, String authKey) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar los registros del usuario"));

        user.setGoogleAuthKey(authKey);

        return toUserDto(userRepository.save(user));
    }

    @Transactional
    public UserDto registerUser(SignUpDto user) {
        if (userRepository.existsByEmail(user.email())) {
            throw new BadRequestException("El email que se intenta registrar ya esta en uso");
        }
        String encryptedPassword = encoder.encode(user.password());

        RoleEntity role = roleRepository.findByName("CLIENTE").orElseThrow();
        PaymentMethodEntity paymentMethod = paymentMethodRepository.findById(user.paymentPreferenceId()).orElseThrow();

        UserEntity newUser = new UserEntity(user.name(), user.email(), user.address(), user.nit(), encryptedPassword,
                role, paymentMethod);

        return toUserDto(userRepository.save(newUser));
    }
}
