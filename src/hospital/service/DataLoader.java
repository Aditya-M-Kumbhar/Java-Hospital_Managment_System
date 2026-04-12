package hospital.service;

import hospital.model.Doctor;
import hospital.model.Patient;

/**
 * Pre-loads 10 patients and 5 doctors into the system on startup.
 */
public class DataLoader {

    public static void load(HospitalSystem sys) {
        // ── 5 Doctors ──────────────────────────────────────────────
        sys.doctors.add(new Doctor(1, "Dr. Arjun Sharma",   "9876541001", "Cardiologist"));
        sys.doctors.add(new Doctor(2, "Dr. Priya Mehta",    "9876541002", "Neurologist"));
        sys.doctors.add(new Doctor(3, "Dr. Rohan Desai",    "9876541003", "Orthopedic"));
        sys.doctors.add(new Doctor(4, "Dr. Sneha Kulkarni", "9876541004", "Pediatrician"));
        sys.doctors.add(new Doctor(5, "Dr. Vikram Joshi",   "9876541005", "Dermatologist"));

        // ── 10 Patients ────────────────────────────────────────────
        sys.patients.add(new Patient(1,  "Rahul Kumar",    "8888880001"));
        sys.patients.add(new Patient(2,  "Priya Singh",    "8888880002"));
        sys.patients.add(new Patient(3,  "Amit Shah",      "8888880003"));
        sys.patients.add(new Patient(4,  "Neha Patil",     "8888880004"));
        sys.patients.add(new Patient(5,  "Suresh Rao",     "8888880005"));
        sys.patients.add(new Patient(6,  "Anjali Nair",    "8888880006"));
        sys.patients.add(new Patient(7,  "Karan Verma",    "8888880007"));
        sys.patients.add(new Patient(8,  "Deepika Iyer",   "8888880008"));
        sys.patients.add(new Patient(9,  "Mohit Gupta",    "8888880009"));
        sys.patients.add(new Patient(10, "Kavya Reddy",    "8888880010"));

        System.out.println("  [OK] Loaded 5 doctors and 10 patients into the system.");
    }
}
