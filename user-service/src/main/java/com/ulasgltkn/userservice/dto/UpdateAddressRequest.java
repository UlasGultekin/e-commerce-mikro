package com.ulasgltkn.userservice.dto;


import lombok.Data;

@Data
public class UpdateAddressRequest {

    private String title;
    private String fullAddress;
    private String city;
    private String country;
    private String zipCode;
    private Boolean isDefault; // null gelirse değiştirme
}
