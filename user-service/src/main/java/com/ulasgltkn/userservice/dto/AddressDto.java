package com.ulasgltkn.userservice.dto;


import lombok.Builder;
import lombok.Data;

import java.util.Date;


@Data
@Builder
public class AddressDto {
    private Long id;
    private String title;
    private String fullAddress;
    private String city;
    private String country;
    private String zipCode;
    private boolean isDefault;
    private Date createdAt;
}
