package com.ulasgltkn.userservice.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAddressRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String fullAddress;

    @NotBlank
    private String city;

    @NotBlank
    private String country;

    private String zipCode;

    private boolean isDefault;
}

