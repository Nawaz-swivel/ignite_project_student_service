package com.swivel.ignite.student.dto.response;

import com.swivel.ignite.student.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Student DTO for response
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponseDto extends ResponseDto {

    private String studentId;
    private String username;
    private String tuitionId;
    private Date tuitionJoinedOn;

    public StudentResponseDto(Student student) {
        this.studentId = student.getId();
        this.username = student.getUsername();
        this.tuitionId = student.getTuitionId();
        this.tuitionJoinedOn = student.getTuitionJoinedOn();
    }
}
