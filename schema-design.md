## MySQL Database Design

Table: patients

id: INT, Primary Key, Auto Increment

first_name: VARCHAR(100), Not Null

last_name: VARCHAR(100), Not Null

dob: DATE, Not Null

gender: ENUM('Male', 'Female', 'Other')

phone: VARCHAR(15), Unique, Not Null

email: VARCHAR(255), Unique

address: TEXT

Table: doctors

id: INT, Primary Key, Auto Increment

first_name: VARCHAR(100), Not Null

last_name: VARCHAR(100), Not Null

specialization: VARCHAR(255), Not Null

phone: VARCHAR(15), Unique, Not Null

email: VARCHAR(255), Unique, Not Null

availability_schedule: TEXT (JSON structure storing available time slots)

Notes:

Doctors should not have overlapping appointments, which will be enforced at the application layer.

Table: appointments

id: INT, Primary Key, Auto Increment

doctor_id: INT, Foreign Key → doctors(id), Not Null

patient_id: INT, Foreign Key → patients(id), Not Null

appointment_time: DATETIME, Not Null

status: ENUM('Scheduled', 'Completed', 'Cancelled'), Default 'Scheduled'

Table: admin

id: INT, Primary Key, Auto Increment

username: VARCHAR(50), Unique, Not Null

password_hash: VARCHAR(255), Not Null

role: ENUM('SuperAdmin', 'Staff'), Default 'Staff'

## MongoDB Collection Design

{
  "_id": "ObjectId('64abc123456')",
  "appointmentId": 51,
  "patientId": 12,
  "doctorId": 7,
  "medications": [
   {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "1 tablet every 6 hours",
      "duration_days": 5
    },
    {
      "name": "Cough Syrup",
      "dosage": "10ml",
      "frequency": "Twice daily",
      "duration_days": 7
    }
  ],
  "doctorNotes": "Patient should rest and stay hydrated.",
  "refillCount": 2,
  "pharmacy": {
    "name": "HealthPlus Pharmacy",
    "location": "Downtown Clinic"
  },
  "createdAt": "2025-08-24T10:00:00Z"
}
