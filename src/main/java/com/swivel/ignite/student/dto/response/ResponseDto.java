package com.swivel.ignite.student.dto.response;

import com.swivel.ignite.student.dto.BaseDto;

/**
 * ResponseDto - All responseDto classes are needed to extend this class.
 */
public class ResponseDto implements BaseDto {

    @Override
    public String toLogJson() {
        return toJson();
    }
}
