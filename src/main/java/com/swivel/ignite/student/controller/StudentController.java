package com.swivel.ignite.student.controller;

import com.swivel.ignite.student.dto.request.StudentCreateRequestDto;
import com.swivel.ignite.student.dto.response.StudentListResponseDto;
import com.swivel.ignite.student.dto.response.StudentResponseDto;
import com.swivel.ignite.student.entity.Student;
import com.swivel.ignite.student.enums.ErrorResponseStatusType;
import com.swivel.ignite.student.enums.RoleType;
import com.swivel.ignite.student.enums.SuccessResponseStatusType;
import com.swivel.ignite.student.exception.*;
import com.swivel.ignite.student.service.StudentService;
import com.swivel.ignite.student.wrapper.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Student Controller
 */
@RestController
@RequestMapping("api/v1/student")
@Slf4j
public class StudentController extends Controller {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * This method creates a new student
     *
     * @param requestDto student create request dto
     * @return success(student response)/ error response
     */
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseWrapper> createStudent(@RequestBody StudentCreateRequestDto requestDto,
                                                         HttpServletRequest request) {
        String token = request.getHeader(AUTH_HEADER);
        try {
            requestDto.setRoleType(RoleType.STUDENT);
            if (!requestDto.isRequiredAvailable()) {
                log.error("Required fields missing in tuition create request DTO for creating student");
                return getBadRequestResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
            Student student = studentService.createStudent(requestDto, token);
            StudentResponseDto responseDto = new StudentResponseDto(student);
            log.debug("Created student {}", responseDto.toLogJson());
            return getSuccessResponse(SuccessResponseStatusType.CREATE_STUDENT, responseDto);
        } catch (StudentAlreadyExistsException e) {
            log.error("Student already exists for create student with requestDto: {}", requestDto.toLogJson(), e);
            return getBadRequestResponse(ErrorResponseStatusType.STUDENT_ALREADY_EXISTS);
        } catch (AuthServiceHttpClientErrorException e) {
            log.error("Error registering student in auth db", e);
            return getInternalServerErrorResponse(ErrorResponseStatusType.AUTH_INTERNAL_SERVER_ERROR, e.status,
                    e.responseBody);
        } catch (StudentServiceException e) {
            log.error("Creating tuition was failed for requestDto: {}", requestDto.toLogJson(), e);
            return getInternalServerErrorResponse();
        }
    }

    /**
     * This method is used to get a student by id
     *
     * @param studentId student id
     * @return success(student)/ error response
     */
    @GetMapping(path = "/get/{studentId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseWrapper> getStudentById(@PathVariable(name = "studentId") String studentId) {
        try {
            Student student = studentService.findById(studentId);
            StudentResponseDto responseDto = new StudentResponseDto(student);
            log.debug("Retrieved student of id: {}", studentId);
            return getSuccessResponse(SuccessResponseStatusType.GET_STUDENT, responseDto);
        } catch (StudentNotFoundException e) {
            log.error("Student not found for studentId: {}", studentId, e);
            return getBadRequestResponse(ErrorResponseStatusType.STUDENT_NOT_FOUND);
        } catch (StudentServiceException e) {
            log.error("Getting student was failed for studentId: {}", studentId, e);
            return getInternalServerErrorResponse();
        }
    }

    /**
     * This method is used to get a student by auth user id
     *
     * @param authUserId auth id
     * @return success(student)/ error response
     */
    @GetMapping(path = "/auth/get/{authUserId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseWrapper> getStudentByAuthUserId(@PathVariable(name = "authUserId") String authUserId) {
        try {
            Student student = studentService.findByAuthUserId(authUserId);
            StudentResponseDto responseDto = new StudentResponseDto(student);
            log.debug("Retrieved student of auth id: {}", authUserId);
            return getSuccessResponse(SuccessResponseStatusType.GET_STUDENT, responseDto);
        } catch (StudentNotFoundException e) {
            log.error("Student not found for auth id: {}", authUserId, e);
            return getBadRequestResponse(ErrorResponseStatusType.STUDENT_NOT_FOUND);
        } catch (StudentServiceException e) {
            log.error("Getting student was failed for auth id: {}", authUserId, e);
            return getInternalServerErrorResponse();
        }
    }

    /**
     * This method deletes a student by id
     *
     * @param studentId student id
     * @return success/ error response
     */
    @DeleteMapping(path = "/delete/{studentId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseWrapper> deleteStudent(@PathVariable(name = "studentId") String studentId,
                                                         HttpServletRequest request) {
        String token = request.getHeader(AUTH_HEADER);
        try {
            Student student = studentService.findById(studentId);
            studentService.deleteStudent(student, token);
            log.debug("Successfully deleted the student of id: {}", studentId);
            return getSuccessResponse(SuccessResponseStatusType.DELETE_STUDENT, null);
        } catch (StudentNotFoundException e) {
            log.error("Student not found for studentId: {}", studentId, e);
            return getBadRequestResponse(ErrorResponseStatusType.STUDENT_NOT_FOUND);
        } catch (TuitionServiceHttpClientErrorException e) {
            log.error("Failed to remove student from tuition in Tuition Micro Service.", e);
            return getInternalServerErrorResponse(ErrorResponseStatusType.TUITION_INTERNAL_SERVER_ERROR,
                    e.responseBody);
        } catch (PaymentServiceHttpClientErrorException e) {
            log.error("Failed to delete all payments by student of id: {} in Payment Micro Service.", studentId, e);
            return getInternalServerErrorResponse(ErrorResponseStatusType.PAYMENT_INTERNAL_SERVER_ERROR,
                    e.responseBody);
        } catch (AuthServiceHttpClientErrorException e) {
            log.error("Error deleting student in auth db", e);
            return getInternalServerErrorResponse(ErrorResponseStatusType.AUTH_INTERNAL_SERVER_ERROR, e.status,
                    e.responseBody);
        } catch (StudentServiceException e) {
            log.error("Deleting student was failed for studentId: {}", studentId, e);
            return getInternalServerErrorResponse();
        }
    }

    /**
     * This method is used to get all students
     *
     * @return success(student list)/ error response
     */
    @GetMapping(path = "/get/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseWrapper> getAllStudents() {
        try {
            List<Student> studentList = studentService.getAll();
            StudentListResponseDto responseDto = new StudentListResponseDto(studentList);
            log.debug("Returned all students");
            return getSuccessResponse(SuccessResponseStatusType.RETURNED_ALL_STUDENT, responseDto);
        } catch (StudentServiceException e) {
            log.error("Failed to get all tuition", e);
            return getInternalServerErrorResponse();
        }
    }

    /**
     * This method is used to add a tuition to a student
     *
     * @return success/ error response
     */
    @PostMapping(path = "/add/student/{studentId}/tuition/{tuitionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseWrapper> addTuitionToStudent(@PathVariable(name = "studentId") String studentId,
                                                               @PathVariable(name = "tuitionId") String tuitionId) {
        try {
            Student student = studentService.findById(studentId);
            if (student.getTuitionId() != null) {
                log.error("Student is already enrolled in a tuition");
                return getBadRequestResponse(ErrorResponseStatusType.STUDENT_ALREADY_ENROLLED_IN_A_TUITION);
            }
            studentService.addTuition(student, tuitionId);
            log.debug("Successfully added the tuition to student");
            return getSuccessResponse(SuccessResponseStatusType.ADD_TUITION_STUDENT, null);
        } catch (StudentServiceException e) {
            log.error("Failed to add tuition to student of id: {}", studentId, e);
            return getInternalServerErrorResponse();
        }
    }

    /**
     * This method is used to remove a tuition from a student
     *
     * @return success/ error response
     */
    @PostMapping(path = "/remove/student/{studentId}/tuition/{tuitionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ResponseWrapper> removeTuitionFromStudent(@PathVariable(name = "studentId") String studentId,
                                                                    @PathVariable(name = "tuitionId") String tuitionId) {
        try {
            Student student = studentService.findById(studentId);
            if (student.getTuitionId() == null || !student.getTuitionId().equals(tuitionId)) {
                log.error("Student is not enrolled in the tuition");
                return getBadRequestResponse(ErrorResponseStatusType.STUDENT_NOT_ENROLLED_IN_TUITION);
            }
            studentService.removeTuition(student);
            log.debug("Successfully removed the tuition from student");
            return getSuccessResponse(SuccessResponseStatusType.REMOVE_TUITION_STUDENT, null);
        } catch (StudentServiceException e) {
            log.error("Failed to remove tuition from student of id: {}", studentId, e);
            return getInternalServerErrorResponse();
        }
    }
}
