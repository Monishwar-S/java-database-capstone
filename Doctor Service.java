package com.project.back_end.service;

import com.project.back_end.model.Doctor;
import com.project.back_end.model.Login;
import com.project.back_end.model.Appointment;
import com.project.back_end.repository.AppointmentRepository;
import com.project.back_end.repository.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // --- Get available slots for a doctor on a specific date ---
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorId);
        if (optionalDoctor.isEmpty()) return Collections.emptyList();

        Doctor doctor = optionalDoctor.get();
        List<String> allSlots = doctor.getAvailableTimes();

        List<Appointment> bookedAppointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(
                        doctorId,
                        date.atStartOfDay(),
                        date.atTime(LocalTime.MAX)
                );

        Set<String> bookedSlots = bookedAppointments.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .collect(Collectors.toSet());

        return allSlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }

    // --- Save new doctor ---
    public int saveDoctor(Doctor doctor) {
        if (doctorRepository.findByEmail(doctor.getEmail()).isPresent()) return -1;
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // --- Update existing doctor ---
    public int updateDoctor(Doctor doctor) {
        if (!doctorRepository.existsById(doctor.getId())) return -1;
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // --- Get all doctors ---
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    // --- Delete doctor by ID ---
    public int deleteDoctor(long id) {
        if (!doctorRepository.existsById(id)) return -1;
        try {
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // --- Validate doctor login ---
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        Optional<Doctor> optionalDoctor = doctorRepository.findByEmail(login.getEmail());
        if (optionalDoctor.isEmpty() || !optionalDoctor.get().getPassword().equals(login.getPassword())) {
            response.put("error", "Invalid credentials");
            return ResponseEntity.badRequest().body(response);
        }
        String token = tokenService.generateToken(optionalDoctor.get().getEmail());
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    // --- Find doctors by name ---
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        List<
