package com.ayds.Cloudmerce.model.entity;

import java.time.Instant;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user", schema = "user_control")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private String email;

    @NonNull
    private String address;

    @NonNull
    private String nit;

    @NonNull
    private String password;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "payment_preference_id")
    private PaymentMethodEntity paymentPreference;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<UserPermissionEntity> userPermissions;
}
