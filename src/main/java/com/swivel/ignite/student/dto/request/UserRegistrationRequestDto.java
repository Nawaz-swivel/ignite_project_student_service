//package com.swivel.ignite.student.dto.request;
//
//import com.swivel.ignite.student.enums.RoleType;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.Setter;
//
///**
// * UserRegistration request dto
// */
//@AllArgsConstructor
//@Setter
//@Getter
//public class UserRegistrationRequestDto extends RequestDto {
//
//    private final String username;
//    private final String password;
//    private final RoleType roleType;
//
//    @Override
//    public String toLogJson() {
//        return toJson();
//    }
//
//    @Override
//    public boolean isRequiredAvailable() {
//        return isNonEmpty(username) && isNonEmpty(password) && roleType != null && isNonEmpty(roleType.name())
//                && roleType != RoleType.ADMIN;
//    }
//
//    @Override
//    public boolean isNonEmpty(String field) {
//        return field != null && !field.trim().isEmpty();
//    }
//}
