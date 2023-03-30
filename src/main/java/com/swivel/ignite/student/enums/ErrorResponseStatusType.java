package com.swivel.ignite.student.enums;

import lombok.Getter;

/**
 * Enum values for Error Response
 */
@Getter
public enum ErrorResponseStatusType {

    MISSING_REQUIRED_FIELDS(400, "Missing required fields"),
    STUDENT_ALREADY_EXISTS(400, "Student already exists with given username"),
    STUDENT_NOT_FOUND(404, "Student not found"),
    STUDENT_NOT_ENROLLED_IN_TUITION(400, "Student not enrolled in tuition"),
    STUDENT_ALREADY_ENROLLED_IN_A_TUITION(400, "Student already enrolled in a tuition"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    AUTH_INTERNAL_SERVER_ERROR(500, "Auth Service - Internal Server Error"),
    TUITION_INTERNAL_SERVER_ERROR(500, "Tuition Service - Internal Server Error"),
    PAYMENT_INTERNAL_SERVER_ERROR(500, "Payment Service - Internal Server Error");

    private final int code;
    private final String message;

    ErrorResponseStatusType(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
