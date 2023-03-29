package com.swivel.ignite.student.repository;

import com.swivel.ignite.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Student Repository
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    /**
     * This method returns a student by name
     *
     * @param username username
     * @return Student/null
     */
    Optional<Student> findByUsername(String username);
}
