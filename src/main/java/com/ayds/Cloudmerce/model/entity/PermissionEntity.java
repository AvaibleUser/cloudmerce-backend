package com.ayds.Cloudmerce.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "user_control.permission")
@Entity(name="permission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class PermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "permission_name")
    private String permissionName;

    @Column(name = "priority_level")
    private Integer priorityLevel;

}
