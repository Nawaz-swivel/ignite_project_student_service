package com.swivel.ignite.student.wrapper;

import com.swivel.ignite.student.dto.BaseDto;
import com.swivel.ignite.student.dto.response.UserResponseDto;
import com.swivel.ignite.student.enums.ResponseStatusType;
import lombok.Getter;
import lombok.Setter;

/**
 * User response wrapper
 */
@Getter
@Setter
public class UserResponseWrapper implements BaseDto {

    private ResponseStatusType status;
    private String message;
    private UserResponseDto data;
    private String displayMessage;

    @Override
    public String toLogJson() {
        return toJson();
    }
}
