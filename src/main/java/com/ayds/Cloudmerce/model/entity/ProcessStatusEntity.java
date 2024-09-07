package com.ayds.Cloudmerce.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "process_status", schema = "sales_control")
@Entity(name = "process_status")
@Getter
@Setter
@NoArgsConstructor
public class ProcessStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "status")
    private String status;
}
