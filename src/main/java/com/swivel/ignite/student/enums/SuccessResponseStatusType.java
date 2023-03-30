package com.swivel.ignite.student.enums;

import lombok.Getter;

/**
 * Enum values for Success Response
 */
@Getter
public enum SuccessResponseStatusType {

    CREATE_STUDENT(201, "Successfully created the student"),
    ADD_TUITION_STUDENT(200, "Successfully added student to tuition"),
    DELETE_STUDENT(202, "Successfully deleted the student"),
    GET_STUDENT(200, "Successfully retrieved the student"),
    REMOVE_TUITION_STUDENT(200, "Successfully removed student from tuition"),
    RETURNED_ALL_STUDENT(200, "Successfully returned students list");

    private final int code;
    private final String message;

    SuccessResponseStatusType(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
