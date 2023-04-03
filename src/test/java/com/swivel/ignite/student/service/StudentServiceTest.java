package com.swivel.ignite.student.service;

import com.swivel.ignite.student.dto.request.StudentCreateRequestDto;
import com.swivel.ignite.student.dto.response.UserResponseDto;
import com.swivel.ignite.student.entity.Student;
import com.swivel.ignite.student.exception.StudentAlreadyExistsException;
import com.swivel.ignite.student.exception.StudentNotFoundException;
import com.swivel.ignite.student.exception.StudentServiceException;
import com.swivel.ignite.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.dao.DataAccessException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * This class tests {@link StudentService} class
 */
class StudentServiceTest {

    private static final String TOKEN = "Bearer 123456789";
    private static final String STUDENT_ID = "sid-123456789";
    private static final String TUITION_ID = "tid-123456789";
    private static final String USER_ID = "uid-123456789";
    private static final String STUDENT_NAME = "STUDENT_NAME";
    private static final String STUDENT_PASSWORD = "123456789";
    private static final String ERROR = "ERROR";
    @Mock
    private PaymentService paymentService;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private TuitionService tuitionService;
    @Mock
    private AuthService authService;
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        initMocks(this);
        studentService = new StudentService(studentRepository, paymentService, tuitionService, authService);
    }

    /**
     * Start of tests for createStudent method
     */
    @Test
    void Should_CreateStudent_When_CreatingStudentIsSuccessful() throws IOException {
        when(studentRepository.findById(anyString())).thenReturn(Optional.empty());
        when(authService.registerStudent(any(StudentCreateRequestDto.class), anyString()))
                .thenReturn(getSampleUserResponseDto());
        studentService.createStudent(getSampleStudentCreateRequestDto(), TOKEN);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void Should_ThrowStudentAlreadyExistsException_When_CreatingStudentForStudentAlreadyExists() throws IOException {
        StudentCreateRequestDto requestDto = getSampleStudentCreateRequestDto();

        when(studentRepository.findByUsername(anyString())).thenReturn(Optional.of(getSampleStudent()));
        StudentAlreadyExistsException exception = assertThrows(StudentAlreadyExistsException.class, () ->
                studentService.createStudent(requestDto, TOKEN));
        assertEquals("Student already exists in DB", exception.getMessage());
    }

    @Test
    void Should_ThrowRegistrationServiceException_When_CreatingStudentForFailedToCheckForStudentInDB() throws IOException {
        StudentCreateRequestDto requestDto = getSampleStudentCreateRequestDto();

        when(studentRepository.findByUsername(anyString())).thenThrow(new DataAccessException(ERROR) {
        });
        StudentServiceException exception = assertThrows(StudentServiceException.class, () ->
                studentService.createStudent(requestDto, TOKEN));
        assertEquals("Failed to check for student existence in DB for username: " + STUDENT_NAME,
                exception.getMessage());
    }

    @Test
    void Should_ThrowRegistrationServiceException_When_CreatingStudentForFailedToSaveStudentInDB() throws IOException {
        StudentCreateRequestDto requestDto = getSampleStudentCreateRequestDto();

        when(studentRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(authService.registerStudent(any(StudentCreateRequestDto.class), anyString()))
                .thenReturn(getSampleUserResponseDto());
        when(studentRepository.save(any(Student.class))).thenThrow(new DataAccessException(ERROR) {
        });
        StudentServiceException exception = assertThrows(StudentServiceException.class, () ->
                studentService.createStudent(requestDto, TOKEN));
        assertEquals("Failed to save student to DB", exception.getMessage());
    }

    /**
     * Start of tests for findById method
     */
    @Test
    void Should_ReturnStudent_When_FindingStudentByIdIsSuccessful() {
        when(studentRepository.findById(anyString())).thenReturn(Optional.of(getSampleStudent()));
        assertEquals(STUDENT_ID, studentService.findById(STUDENT_ID).getId());
    }

    @Test
    void Should_ThrowStudentNotFoundException_When_FindingStudentByIdForStudentNotFound() {
        when(studentRepository.findById(anyString())).thenReturn(Optional.empty());
        StudentNotFoundException exception = assertThrows(StudentNotFoundException.class, () ->
                studentService.findById(STUDENT_ID));
        assertEquals("Student not found for student id: " + STUDENT_ID, exception.getMessage());
    }

    @Test
    void Should_ThrowRegistrationServiceException_When_FindingStudentByIdForFailedToFindStudentById() {
        when(studentRepository.findById(anyString())).thenThrow(new DataAccessException(ERROR) {
        });
        StudentServiceException exception = assertThrows(StudentServiceException.class, () ->
                studentService.findById(STUDENT_ID));
        assertEquals("Failed to find student by id for student id: " + STUDENT_ID, exception.getMessage());
    }

    /**
     * Start of tests for findByAuthUserId method
     */
    @Test
    void Should_ReturnStudent_When_FindingStudentByAuthUserIdIsSuccessful() {
        when(studentRepository.findByAuthUserId(anyString())).thenReturn(Optional.of(getSampleStudent()));
        assertEquals(STUDENT_ID, studentService.findByAuthUserId(USER_ID).getId());
    }

    @Test
    void Should_ThrowStudentNotFoundException_When_FindingStudentByAuthUserIdForStudentNotFound() {
        when(studentRepository.findByAuthUserId(anyString())).thenReturn(Optional.empty());
        StudentNotFoundException exception = assertThrows(StudentNotFoundException.class, () -> studentService
                .findByAuthUserId(USER_ID));
        assertEquals("Student not found for auth id: " + USER_ID, exception.getMessage());
    }

    @Test
    void Should_ThrowStudentServiceException_When_FindingStudentByAuthUserIdIsFailed() {
        when(studentRepository.findByAuthUserId(anyString())).thenThrow(new DataAccessException(ERROR) {
        });
        StudentServiceException exception = assertThrows(StudentServiceException.class, () -> studentService
                .findByAuthUserId(USER_ID));
        assertEquals("Failed to find student by id for auth id: " + USER_ID, exception.getMessage());
    }

    /**
     * Start of tests for deleteStudent method
     */
    @Test
    void Should_DeleteStudent_When_DeletingStudentIsSuccessful() throws IOException {
        doNothing().when(paymentService).deleteByStudentId(STUDENT_ID, TOKEN);
        studentService.deleteStudent(getSampleStudent(), TOKEN);
        verify(studentRepository).delete(any(Student.class));
    }

    @Test
    void Should_ThrowRegistrationServiceException_When_DeletingStudentForFailedToDeleteStudent() throws IOException {
        Student student = getSampleStudent();

        doNothing().when(paymentService).deleteByStudentId(STUDENT_ID, TOKEN);
        doThrow(new DataAccessException(ERROR) {
        }).when(studentRepository).delete(any(Student.class));
        StudentServiceException exception = assertThrows(StudentServiceException.class, () ->
                studentService.deleteStudent(student, TOKEN));
        assertEquals("Failed to delete student of id: " + STUDENT_ID, exception.getMessage());
    }

    /**
     * Start of test for getAll method
     */
    @Test
    void Should_ReturnAllStudent_When_GettingAllIsSuccessful() {
        when(studentRepository.findAll()).thenReturn(getSampleStudentList());
        assertEquals(STUDENT_ID, studentService.getAll().get(0).getId());
    }

    @Test
    void Should_ThrowRegistrationServiceException_When_GettingAllIsFailed() {
        when(studentRepository.findAll()).thenThrow(new DataAccessException(ERROR) {
        });
        StudentServiceException exception = assertThrows(StudentServiceException.class, () ->
                studentService.getAll());
        assertEquals("Failed to to get all students", exception.getMessage());
    }

    /**
     * Start of test for addTuition method
     */
    @Test
    void Should_AddTuitionToStudent_When_AddingTuitionIsSuccessful() {
        studentService.addTuition(getSampleStudent(), TUITION_ID);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void Should_ThrowStudentServiceException_When_AddingTuitionIsFailed() {
        Student student = getSampleStudent();

        when(studentRepository.save(any(Student.class))).thenThrow(new DataAccessException(ERROR) {
        });
        StudentServiceException exception = assertThrows(StudentServiceException.class, () -> studentService
                .addTuition(student, TUITION_ID));
        assertEquals("Failed to add tuition of id: " + TUITION_ID + " to student", exception.getMessage());
    }

    /**
     * Start of test for removeTuition method
     */
    @Test
    void Should_RemoveTuitionFromStudent_When_RemovingTuitionIsSuccessful() {
        studentService.removeTuition(getSampleStudent());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void Should_ThrowStudentServiceException_When_RemovingTuitionIsFailed() {
        Student student = getSampleStudent();

        when(studentRepository.save(any(Student.class))).thenThrow(new DataAccessException(ERROR) {
        });
        StudentServiceException exception = assertThrows(StudentServiceException.class, () -> studentService
                .removeTuition(student));
        assertEquals("Failed to remove tuition from student of id: " + student.getId(), exception.getMessage());
    }

    /**
     * This method returns a sample student
     *
     * @return Student
     */
    private Student getSampleStudent() {
        Student student = new Student();
        student.setId(STUDENT_ID);
        student.setPassword(STUDENT_PASSWORD);
        return student;
    }

    /**
     * This method returns a sample StudentCreateRequestDto
     *
     * @return StudentCreateRequestDto
     */
    private StudentCreateRequestDto getSampleStudentCreateRequestDto() {
        StudentCreateRequestDto requestDto = new StudentCreateRequestDto();
        requestDto.setUsername(STUDENT_NAME);
        requestDto.setPassword(STUDENT_PASSWORD);
        return requestDto;
    }

    /**
     * This method returns a sample student list
     *
     * @return Student List
     */
    private List<Student> getSampleStudentList() {
        List<Student> studentList = new ArrayList<>();
        studentList.add(getSampleStudent());
        return studentList;
    }

    /**
     * This method returns a sample UserResponseDto
     *
     * @return UserResponseDto
     */
    private UserResponseDto getSampleUserResponseDto() {
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setUserId(USER_ID);
        return responseDto;
    }
}
