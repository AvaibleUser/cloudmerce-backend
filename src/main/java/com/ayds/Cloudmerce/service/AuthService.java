package com.ayds.Cloudmerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ayds.Cloudmerce.model.dto.SignUpDto;
import com.ayds.Cloudmerce.model.entity.UserEntity;
import com.ayds.Cloudmerce.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserEntity registerUser(SignUpDto user) {
        if (userRepository.existsByName(user.username())) {
            throw new DuplicateKeyException("Username already exists");
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(user.password()); //
        UserEntity newUser = new UserEntity(); //TODO crear un mapper que retorne un nuevo usuario (como entidad) en base al DTO
        return userRepository.save(newUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
