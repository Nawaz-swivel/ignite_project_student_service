package com.swivel.ignite.student.controller;

import com.swivel.ignite.student.dto.request.StudentCreateRequestDto;
import com.swivel.ignite.student.entity.Student;
import com.swivel.ignite.student.enums.ErrorResponseStatusType;
import com.swivel.ignite.student.enums.SuccessResponseStatusType;
import com.swivel.ignite.student.exception.*;
import com.swivel.ignite.student.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This class tests {@link StudentController} class
 */
class StudentControllerTest {

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN = "Bearer 123456789";
    private static final String STUDENT_ID = "sid-123456789";
    private static final String TUITION_ID = "tid-123456789";
    private static final String USER_ID = "uid-123456789";
    private static final String STUDENT_NAME = "Mohamed Nawaz";
    private static final String STUDENT_PASSWORD = "123456789";
    private static final String SUCCESS_STATUS = "SUCCESS";
    private static final String ERROR_STATUS = "ERROR";
    private static final String SUCCESS_MESSAGE = "Successfully returned the data.";
    private static final String ERROR_MESSAGE = "Oops!! Something went wrong. Please try again.";
    private static final String ERROR = "ERROR";
    private static final String CREATE_STUDENT_URI = "/api/v1/student";
    private static final String GET_STUDENT_BY_ID_URI = "/api/v1/student/get/{studentId}";
    private static final String GET_STUDENT_BY_AUTH_USER_ID_URI = "/api/v1/student/auth/get/{authUserId}";
    private static final String DELETE_STUDENT_URI = "/api/v1/student/delete/{studentId}";
    private static final String GET_ALL_STUDENT_URI = "/api/v1/student/get/all";
    private static final String ADD_TUITION_TO_STUDENT_URI = "/api/v1/student/add/student/{studentId}/tuition/{tuitionId}";
    private static final String REMOVE_TUITION_FROM_STUDENT_URI = "/api/v1/student/remove/student/{studentId}/tuition/{tuitionId}";
    private MockMvc mockMvc;
    @Mock
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        initMocks(this);
        StudentController studentController = new StudentController(studentService);
        mockMvc = MockMvcBuilders.standaloneSetup(studentController)
                .setControllerAdvice(new CustomizedExceptionHandling())
                .build();
    }

    /**
     * Start of tests for create student
     * Api context: /api/v1/student/create
     */
    @Test
    void Should_ReturnOk_When_CreatingStudentIsSuccessful() throws Exception {
        when(studentService.createStudent(any(StudentCreateRequestDto.class), anyString()))
                .thenReturn(getSampleStudent());

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_STUDENT_URI)
                        .header(AUTH_HEADER, TOKEN)
                        .content(getSampleStudentCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.CREATE_STUDENT.getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.CREATE_STUDENT.getCode()))
                .andExpect(jsonPath("$.data.studentId").value(STUDENT_ID))
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_CreatingStudentForMissingRequiredFields() throws Exception {
        StudentCreateRequestDto dto = getSampleStudentCreateRequestDto();
        dto.setUsername("");

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_STUDENT_URI)
                        .header(AUTH_HEADER, TOKEN)
                        .content(dto.toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_CreatingStudentForStudentAlreadyExists() throws Exception {
        doThrow(new StudentAlreadyExistsException(ERROR)).when(studentService)
                .createStudent(any(StudentCreateRequestDto.class), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_STUDENT_URI)
                        .header(AUTH_HEADER, TOKEN)
                        .content(getSampleStudentCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.STUDENT_ALREADY_EXISTS
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.STUDENT_ALREADY_EXISTS
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_CreatingStudentForFailedToRegisterStudentInAuthDb() throws Exception {
        doThrow(new AuthServiceHttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR)).when(studentService)
                .createStudent(any(StudentCreateRequestDto.class), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_STUDENT_URI)
                        .header(AUTH_HEADER, TOKEN)
                        .content(getSampleStudentCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.AUTH_INTERNAL_SERVER_ERROR
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.AUTH_INTERNAL_SERVER_ERROR
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_CreatingStudentIsFailed() throws Exception {
        doThrow(new StudentServiceException(ERROR)).when(studentService)
                .createStudent(any(StudentCreateRequestDto.class), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_STUDENT_URI)
                        .header(AUTH_HEADER, TOKEN)
                        .content(getSampleStudentCreateRequestDto().toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    /**
     * Start of tests for get student by id
     * Api context: /api/v1/student/get/{studentId}
     */
    @Test
    void Should_ReturnOk_When_GettingStudentByIdIsSuccessful() throws Exception {
        when(studentService.findById(anyString())).thenReturn(getSampleStudent());

        String uri = GET_STUDENT_BY_ID_URI.replace("{studentId}", STUDENT_ID);
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.GET_STUDENT.getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.GET_STUDENT.getCode()))
                .andExpect(jsonPath("$.data.studentId").value(STUDENT_ID))
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_GettingStudentByIdForStudentNotFound() throws Exception {
        when(studentService.findById(anyString())).thenThrow(new StudentNotFoundException(ERROR));

        String uri = GET_STUDENT_BY_ID_URI.replace("{studentId}", STUDENT_ID);
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.STUDENT_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.STUDENT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_GettingStudentByIdIsFailed() throws Exception {
        when(studentService.findById(anyString())).thenThrow(new StudentServiceException(ERROR));

        String uri = GET_STUDENT_BY_ID_URI.replace("{studentId}", STUDENT_ID);
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    /**
     * Start of tests for get student by auth user id
     * Api context: /api/v1/student/auth/get/{authUserId}
     */
    @Test
    void Should_ReturnOk_When_GettingStudentByAuthUserIdIsSuccessful() throws Exception {
        when(studentService.findByAuthUserId(anyString())).thenReturn(getSampleStudent());

        String uri = GET_STUDENT_BY_AUTH_USER_ID_URI.replace("{authUserId}", USER_ID);
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.GET_STUDENT.getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.GET_STUDENT.getCode()))
                .andExpect(jsonPath("$.data.username").value(STUDENT_NAME))
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_GettingStudentByAuthUserIdForStudentNotFound() throws Exception {
        when(studentService.findByAuthUserId(anyString())).thenThrow(new StudentNotFoundException(ERROR));

        String uri = GET_STUDENT_BY_AUTH_USER_ID_URI.replace("{authUserId}", USER_ID);
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.STUDENT_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.STUDENT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_GettingStudentByAuthUserIdIsFailed() throws Exception {
        when(studentService.findByAuthUserId(anyString())).thenThrow(new StudentServiceException(ERROR));

        String uri = GET_STUDENT_BY_AUTH_USER_ID_URI.replace("{authUserId}", USER_ID);
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    /**
     * Start of tests for delete student
     * Api context: /api/v1/student/delete/{studentId}
     */
    @Test
    void Should_ReturnOk_When_DeletingStudentIsSuccessful() throws Exception {
        when(studentService.findById(anyString())).thenReturn(getSampleStudent());
        doNothing().when(studentService).deleteStudent(any(Student.class), anyString());

        String uri = DELETE_STUDENT_URI.replace("{studentId}", STUDENT_ID);
        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.DELETE_STUDENT.getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.DELETE_STUDENT.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_DeletingStudentForStudentNotFound() throws Exception {
        when(studentService.findById(anyString())).thenThrow(new StudentNotFoundException(ERROR));

        String uri = DELETE_STUDENT_URI.replace("{studentId}", STUDENT_ID);
        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.STUDENT_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.STUDENT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_DeletingStudentForFailedToRemoveStudentFromTuition() throws Exception {
        when(studentService.findById(anyString())).thenReturn(getSampleStudent());
        doThrow(new TuitionServiceHttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR))
                .when(studentService).deleteStudent(any(Student.class), anyString());

        String uri = DELETE_STUDENT_URI.replace("{studentId}", STUDENT_ID);
        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.TUITION_INTERNAL_SERVER_ERROR
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.TUITION_INTERNAL_SERVER_ERROR
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_DeletingStudentForFailedToDeleteAllPaymentsByStudent() throws Exception {
        when(studentService.findById(anyString())).thenReturn(getSampleStudent());
        doThrow(new PaymentServiceHttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR))
                .when(studentService).deleteStudent(any(Student.class), anyString());

        String uri = DELETE_STUDENT_URI.replace("{studentId}", STUDENT_ID);
        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.PAYMENT_INTERNAL_SERVER_ERROR
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.PAYMENT_INTERNAL_SERVER_ERROR
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_DeletingStudentForFailedToDeleteStudentInAuthDb() throws Exception {
        when(studentService.findById(anyString())).thenReturn(getSampleStudent());
        doThrow(new AuthServiceHttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR))
                .when(studentService).deleteStudent(any(Student.class), anyString());

        String uri = DELETE_STUDENT_URI.replace("{studentId}", STUDENT_ID);
        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.AUTH_INTERNAL_SERVER_ERROR
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.AUTH_INTERNAL_SERVER_ERROR
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_DeletingStudentIsFailed() throws Exception {
        when(studentService.findById(anyString())).thenThrow(new StudentServiceException(ERROR));

        String uri = DELETE_STUDENT_URI.replace("{studentId}", STUDENT_ID);
        mockMvc.perform(MockMvcRequestBuilders.delete(uri)
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    /**
     * Start of tests for getAllStudents
     * Api context: /api/v1/student/get/all
     */
    @Test
    void Should_ReturnOk_When_GettingAllStudentIsSuccessful() throws Exception {
        when(studentService.getAll()).thenReturn(getSampleStudentList());

        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_STUDENT_URI)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.RETURNED_ALL_STUDENT
                        .getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.RETURNED_ALL_STUDENT
                        .getCode()))
                .andExpect(jsonPath("$.data.students[0].studentId").value(STUDENT_ID))
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_GettingAllStudentIsFailed() throws Exception {
        when(studentService.getAll()).thenThrow(new StudentServiceException(ERROR));

        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_STUDENT_URI)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR
                        .getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    /**
     * Start of tests for add tuition to student
     * Api context: /api/v1/student/add/student/{studentId}/tuition/{tuitionId}
     */
    @Test
    void Should_ReturnOk_When_AddingTuitionToStudentIsSuccessful() throws Exception {
        when(studentService.findById(anyString())).thenReturn(getSampleStudent());
        doNothing().when(studentService).addTuition(any(Student.class), anyString());

        String uri = ADD_TUITION_TO_STUDENT_URI.replace("{studentId}", STUDENT_ID)
                .replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.ADD_TUITION_STUDENT
                        .getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.ADD_TUITION_STUDENT
                        .getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_AddingTuitionToStudentForStudentAlreadyEnrolledInTuition() throws Exception {
        Student student = getSampleStudent();
        student.setTuitionId(TUITION_ID);

        when(studentService.findById(anyString())).thenReturn(student);

        String uri = ADD_TUITION_TO_STUDENT_URI.replace("{studentId}", STUDENT_ID)
                .replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType
                        .STUDENT_ALREADY_ENROLLED_IN_A_TUITION.getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType
                        .STUDENT_ALREADY_ENROLLED_IN_A_TUITION.getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_AddingTuitionToStudentIsFailed() throws Exception {
        when(studentService.findById(anyString())).thenThrow(new StudentServiceException(ERROR));

        String uri = ADD_TUITION_TO_STUDENT_URI.replace("{studentId}", STUDENT_ID)
                .replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType
                        .INTERNAL_SERVER_ERROR.getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType
                        .INTERNAL_SERVER_ERROR.getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    /**
     * Start of tests for remove tuition from student
     * Api context: /api/v1/student/remove/student/{studentId}/tuition/{tuitionId}
     */
    @Test
    void Should_ReturnOk_When_RemovingTuitionFromStudentIsSuccessful() throws Exception {
        Student student = getSampleStudent();
        student.setTuitionId(TUITION_ID);

        when(studentService.findById(anyString())).thenReturn(student);
        doNothing().when(studentService).removeTuition(any(Student.class));

        String uri = REMOVE_TUITION_FROM_STUDENT_URI.replace("{studentId}", STUDENT_ID)
                .replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.REMOVE_TUITION_STUDENT
                        .getMessage()))
                .andExpect(jsonPath("$.statusCode").value(SuccessResponseStatusType.REMOVE_TUITION_STUDENT
                        .getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.displayMessage").value(SUCCESS_MESSAGE));
    }

    @Test
    void Should_ReturnBadRequest_When_RemovingTuitionFromStudentForStudentNotEnrolledInTuition() throws Exception {
        when(studentService.findById(anyString())).thenReturn(getSampleStudent());

        String uri = REMOVE_TUITION_FROM_STUDENT_URI.replace("{studentId}", STUDENT_ID)
                .replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType
                        .STUDENT_NOT_ENROLLED_IN_TUITION.getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType
                        .STUDENT_NOT_ENROLLED_IN_TUITION.getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
    }

    @Test
    void Should_ReturnInternalServerError_When_RemovingTuitionFromStudentIsFailed() throws Exception {
        when(studentService.findById(anyString())).thenThrow(new StudentServiceException(ERROR));

        String uri = REMOVE_TUITION_FROM_STUDENT_URI.replace("{studentId}", STUDENT_ID)
                .replace("{tuitionId}", TUITION_ID);
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType
                        .INTERNAL_SERVER_ERROR.getMessage()))
                .andExpect(jsonPath("$.errorCode").value(ErrorResponseStatusType
                        .INTERNAL_SERVER_ERROR.getCode()))
                .andExpect(jsonPath("$.displayMessage").value(ERROR_MESSAGE));
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
     * This method returns a sample student
     *
     * @return Student
     */
    private Student getSampleStudent() {
        Student student = new Student();
        student.setId(STUDENT_ID);
        student.setUsername(STUDENT_NAME);
        return student;
    }

    /**
     * This method returns a sample student list
     *
     * @return
     */
    private List<Student> getSampleStudentList() {
        List<Student> studentList = new ArrayList<>();
        studentList.add(getSampleStudent());
        return studentList;
    }
}
