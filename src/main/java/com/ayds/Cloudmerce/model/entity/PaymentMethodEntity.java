package com.ayds.Cloudmerce.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "payment_method", schema = "sales_control")
@Entity(name = "payment_method")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class PaymentMethodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "method_name")
    private String methodName;

}