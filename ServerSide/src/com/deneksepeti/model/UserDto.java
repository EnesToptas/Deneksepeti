package com.deneksepeti.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    String userId;
    String displayName;
    String email;
    String password;
}
