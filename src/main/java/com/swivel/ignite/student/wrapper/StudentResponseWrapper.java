package com.swivel.ignite.student.wrapper;

import com.swivel.ignite.student.dto.BaseDto;
import com.swivel.ignite.student.dto.response.StudentResponseDto;
import com.swivel.ignite.student.enums.ResponseStatusType;
import lombok.Getter;
import lombok.Setter;

/**
 * Student response wrapper
 */
@Getter
@Setter
public class StudentResponseWrapper implements BaseDto {

    private ResponseStatusType status;
    private String message;
    private StudentResponseDto data;
    private String displayMessage;

    @Override
    public String toLogJson() {
        return toJson();
    }
}
