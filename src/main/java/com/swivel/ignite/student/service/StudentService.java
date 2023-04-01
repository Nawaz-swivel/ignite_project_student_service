package com.swivel.ignite.student.service;

import com.swivel.ignite.student.dto.request.StudentCreateRequestDto;
import com.swivel.ignite.student.dto.response.UserResponseDto;
import com.swivel.ignite.student.entity.Student;
import com.swivel.ignite.student.exception.StudentAlreadyExistsException;
import com.swivel.ignite.student.exception.StudentNotFoundException;
import com.swivel.ignite.student.exception.StudentServiceException;
import com.swivel.ignite.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Student Service
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final PaymentService paymentService;
    private final TuitionService tuitionService;
    private final AuthService authService;

    @Autowired
    public StudentService(StudentRepository studentRepository, PaymentService paymentService,
                          TuitionService tuitionService, AuthService authService) {
        this.studentRepository = studentRepository;
        this.paymentService = paymentService;
        this.tuitionService = tuitionService;
        this.authService = authService;
    }

    /**
     * This method creates a Student in the database
     *
     * @param requestDto student create dto
     * @param token      access token
     */
    @Transactional
    public Student createStudent(StudentCreateRequestDto requestDto, String token) {
        Student student = new Student(requestDto);
        try {
            if (isStudentExists(student.getUsername()))
                throw new StudentAlreadyExistsException("Student already exists in DB");
            UserResponseDto userResponseDto = authService.registerStudent(requestDto, token);
            student.setAuthUserId(userResponseDto.getUserId());
            return studentRepository.save(student);
        } catch (DataAccessException | IOException e) {
            throw new StudentServiceException("Failed to save student to DB", e);
        }
    }

    /**
     * This method returns a student by id
     *
     * @param studentId student id
     * @return Student/null
     */
    public Student findById(String studentId) {
        try {
            Optional<Student> optionalStudent = studentRepository.findById(studentId);
            if (!optionalStudent.isPresent())
                throw new StudentNotFoundException("Student not found for student id: " + studentId);
            return optionalStudent.get();
        } catch (DataAccessException e) {
            throw new StudentServiceException("Failed to find student by id for student id: " + studentId, e);
        }
    }

    /**
     * This method returns a student by auth user id
     *
     * @param authUserId auth id
     * @return Student/null
     */
    public Student findByAuthUserId(String authUserId) {
        try {
            Optional<Student> optionalStudent = studentRepository.findByAuthUserId(authUserId);
            if (!optionalStudent.isPresent())
                throw new StudentNotFoundException("Student not found for auth id: " + authUserId);
            return optionalStudent.get();
        } catch (DataAccessException e) {
            throw new StudentServiceException("Failed to find student by id for auth id: " + authUserId, e);
        }
    }

    /**
     * This method deletes a student
     *
     * @param student student
     */
    @Transactional
    public void deleteStudent(Student student, String token) {
        try {
            paymentService.deleteByStudentId(student.getId(), token);
            if (student.getTuitionId() != null)
                tuitionService.removeStudent(student.getId(), student.getTuitionId(), token);
            studentRepository.delete(student);
            authService.deleteStudent(student.getUsername(), token);
        } catch (DataAccessException | IOException e) {
            throw new StudentServiceException("Failed to delete student of id: " + student.getId(), e);
        }
    }

    /**
     * This method add a tuition to a student
     *
     * @param student   student
     * @param tuitionId tuition id
     */
    public void addTuition(Student student, String tuitionId) {
        try {
            student.setTuitionId(tuitionId);
            student.setTuitionJoinedOn(new Date());
            studentRepository.save(student);
        } catch (DataAccessException e) {
            throw new StudentServiceException("Failed to add tuition of id: " + tuitionId + " to student", e);
        }
    }

    /**
     * This method removes a student from tuition
     *
     * @param student student
     */
    public void removeTuition(Student student) {
        try {
            student.setTuitionId(null);
            student.setTuitionJoinedOn(null);
            studentRepository.save(student);
        } catch (DataAccessException e) {
            throw new StudentServiceException("Failed to remove tuition from student of id: " + student.getId(), e);
        }
    }

    /**
     * This method checks if student already exists in the DB
     *
     * @param username username
     * @return true/false
     */
    private boolean isStudentExists(String username) {
        try {
            return studentRepository.findByUsername(username).isPresent();
        } catch (DataAccessException e) {
            throw new StudentServiceException("Failed to check for student existence in DB for username: " + username, e);
        }
    }

    /**
     * This method returns all students
     *
     * @return list of students
     */
    public List<Student> getAll() {
        try {
            return studentRepository.findAll();
        } catch (DataAccessException e) {
            throw new StudentServiceException("Failed to to get all students", e);
        }
    }
}
