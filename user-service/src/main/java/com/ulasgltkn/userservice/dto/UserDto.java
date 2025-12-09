package com.ulasgltkn.userservice.dto;


import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private Date createdAt;
}
