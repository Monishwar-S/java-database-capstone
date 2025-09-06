package com.project.back_end.service;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repository.AppointmentRepository;
import com.project.back_end.repository.DoctorRepository;
import com.project.back_end.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TokenService tokenService;

    // --- Book a new appointment ---
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1; // Success
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Failure
        }
    }

    // --- Update an existing appointment ---
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> existingOpt = appointmentRepository.findById(appointment.getId());
        if (existingOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment existing = existingOpt.get();

        // Example validation: ensure appointmentTime is in the future
        if (appointment.getAppointmentTime().isBefore(LocalDateTime.now())) {
            response.put("message", "Appointment time must be in the future");
            return ResponseEntity.badRequest().body(response);
        }

        existing.setAppointmentTime(appointment.getAppointmentTime());
        existing.setStatus(appointment.getStatus());
        existing.setDoctor(appointment.getDoctor());
        existing.setPatient(appointment.getPatient());

        appointmentRepository.save(existing);
        response.put("message", "Appointment updated successfully");
        return ResponseEntity.ok(response);
    }

    // --- Cancel an existing appointment ---
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> existingOpt = appointmentRepository.findById(id);
        if (existingOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment appointment = existingOpt.get();

        // Optional: check if token corresponds to the patient who booked
        String patientEmailFromToken = tokenService.extractEmail(token);
        if (!appointment.getPatient().getEmail().equals(patientEmailFromToken)) {
            response.put("message", "Unauthorized to cancel this appointment");
            return ResponseEntity.status(403).body(response);
        }

        appointmentRepository.delete(appointment);
        response.put("message", "Appointment canceled successfully");
        return ResponseEntity.ok(response);
    }

    // --- Retrieve appointments for a doctor on a specific date, optional patient name filter ---
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> result = new HashMap<>();

        String doctorEmailFromToken = tokenService.extractEmail(token);
        Optional<Doctor> doctorOpt = doctorRepository.findByEmail(doctorEmailFromToken);
        if (doctorOpt.isEmpty()) {
            result.put("message", "Doctor not found");
            result.put("appointments", Collections.emptyList());
            return result;
        }

        Doctor doctor = doctorOpt.get();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                doctor.getId(), start, end
        );

        // Filter by patient name if provided
        if (pname != null && !pname.isEmpty()) {
            appointments.removeIf(a -> !a.getPatient().getName().toLowerCase().contains(pname.toLowerCase()));
        }

        result.put("appointments", appointments);
        return result;
    }
}
