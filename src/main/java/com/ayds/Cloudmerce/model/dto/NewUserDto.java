package com.ayds.Cloudmerce.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {

    private String name;

    private String email;

    private String address;

    private String nit;

    private String password;

    @JsonProperty("role_id")
    private Integer roleId;

    @JsonProperty("payment_preference_id")
    private Integer paymentPreferenceId;

}
