package com.example.clinic.controller;

import com.example.clinic.model.Prescription;
import com.example.clinic.service.PrescriptionService;
import com.example.clinic.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private TokenService tokenService;

    // Save Prescription
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @PathVariable String token,
            @Valid @RequestBody Prescription prescription) {

        Map<String, String> response = new HashMap<>();

        if (!tokenService.validateToken(token, "doctor")) {
            response.put("message", "Invalid token or unauthorized access");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        try {
            prescriptionService.savePrescription(prescription);
            response.put("message", "Prescription saved successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "Error saving prescription: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get Prescription by Appointment ID
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescriptionByAppointment(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        if (!tokenService.validateToken(token, "doctor")) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid token or unauthorized access");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Prescription prescription = prescriptionService.getPrescription(appointmentId);

        if (prescription != null) {
            return new ResponseEntity<>(prescription, HttpStatus.OK);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No prescription found for this appointment");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // Global validation exception handler
    @RestControllerAdvice
    public static class ValidationFailed {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : ex.getBindingResult().getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
    }
}
