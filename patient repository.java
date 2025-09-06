package com.project.back_end.repository;

import com.project.back_end.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // 1. Retrieve a patient by email (derived query)
    Optional<Patient> findByEmail(String email);

    // 2. Retrieve a patient by email OR phone (custom query)
    @Query("SELECT p FROM Patient p WHERE p.email = :identifier OR p.phone = :identifier")
    Optional<Patient> findByEmailOrPhone(String identifier);
}
