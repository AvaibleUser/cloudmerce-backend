package com.ayds.Cloudmerce.model.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Lazy;

@Table(name = "user_permission",schema = "user_control")
@Entity(name = "user_permission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserPermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;


    @JoinColumn(name = "user_id::integer", referencedColumnName = "id")
    @Lazy
    @ManyToOne
    private UserEntity user;



    @Column(name = "permission_id::integer")
    private Integer permissionId;



}
