package com.ayds.Cloudmerce.model.entity;


import jakarta.persistence.*;
import lombok.*;

@Table(name = "role",schema = "user_control")
@Entity(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "description")
    private String decription;

}
