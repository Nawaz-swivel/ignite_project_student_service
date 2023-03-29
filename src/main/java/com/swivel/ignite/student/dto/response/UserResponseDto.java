package com.swivel.ignite.student.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User DTO for response
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto extends ResponseDto {

    private String userId;
    private String userName;
    private String password;
    private String role;
}
