package com.swivel.ignite.student.dto.response;

import com.swivel.ignite.student.entity.Student;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Student List DTO for response
 */
@Getter
public class StudentListResponseDto extends ResponseDto {

    private final List<StudentResponseDto> students = new ArrayList<>();

    public StudentListResponseDto(List<Student> students) {
        for (Student student : students) {
            StudentResponseDto studentResponseDto = new StudentResponseDto(student);
            this.students.add(studentResponseDto);
        }
    }
}
