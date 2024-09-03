package com.ayds.Cloudmerce.model.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ayds.Cloudmerce.enums.UserRole;

@Table(name = "user",schema = "user_control")
@Entity(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "nit")
    private String nit;

    @Column(name = "password")
    private String password;

    @JoinColumn(name = "role_id::integer", referencedColumnName = "id")
    @Lazy
    @ManyToOne
    private RoleEntity role;


    @Column(name = "payment_preference_id")
    private PaymentMethodEntity paymentPreferenceId;  //TODO pendiente de agregar la entidad como llave foranea

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}