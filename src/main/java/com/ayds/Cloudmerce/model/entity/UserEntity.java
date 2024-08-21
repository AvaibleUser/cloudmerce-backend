package com.ayds.Cloudmerce.model.entity;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ayds.Cloudmerce.enums.UserRole;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table
@Entity(name = "users")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    private String username;

    private String password;

    private String nombre;

    private String correo;

    private String direccion;

    private String nit;

    //TODO agregar la entidad ROL como atributo
    //TODO agregar la entidad PREFERENCIA PAGO como atributo




    @Enumerated(EnumType.STRING)
    private UserRole role;

    public UserEntity(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        switch (role) {
            case ADMIN:
                return List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_NO_ADMIN"));

            default:
                return List.of(new SimpleGrantedAuthority("ROLE_NO_ADMIN"));
        }
    }
}