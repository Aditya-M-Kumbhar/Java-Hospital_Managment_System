package hospital.main;

import hospital.exception.HospitalException;
import hospital.model.*;
import hospital.service.AppointmentService;
import hospital.service.HospitalSystem;
import hospital.repository.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Hospital Management System — Production-Level CLI
 *
 * Java concepts used:
 *  - Inheritance / Abstract class (Person → Patient, Doctor)
 *  - Interface + implementation (Schedulable → AppointmentService)
 *  - Generics (Repository<T>)
 *  - Enum (AppointmentStatus)
 *  - Optional (safe lookups in Repository)
 *  - Custom exception hierarchy (HospitalException)
 *  - Singleton (HospitalSystem — thread-safe double-checked locking)
 *  - Multithreading — ReentrantLock in AppointmentService
 *  - volatile fields (AppointmentStatus, Singleton instance)
 *  - Streams + lambda / method references
 *  - LocalDate / LocalTime for real date-time handling
 */
public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final HospitalSystem sys = HospitalSystem.getInstance();
    private static int apptId = 1;

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("     HOSPITAL MANAGEMENT SYSTEM v2.0    ");
        System.out.println("=========================================");

        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Patient Menu");
            System.out.println("2. Doctor Menu");
            System.out.println("3. Appointment Menu");
            System.out.println("4. Billing");
            System.out.println("5. Exit");
            System.out.print("Choice: ");

            switch (readInt()) {
                case 1 -> patientMenu();
                case 2 -> doctorMenu();
                case 3 -> appointmentMenu();
                case 4 -> billingMenu();
                case 5 -> { System.out.println("Goodbye!"); return; }
                default -> System.out.println("[ERROR] Invalid choice.");
            }
        }
    }

    // ─────────────────────────── Patient ───────────────────────────

    static void patientMenu() {
        while (true) {
            System.out.println("\n--- Patient Menu ---");
            System.out.println("1. Add Patient");
            System.out.println("2. View All Patients");
            System.out.println("3. Back");
            System.out.print("Choice: ");

            switch (readInt()) {
                case 1 -> {
                    System.out.print("Patient ID : "); int id = readInt();
                    if (sys.patients.existsById(id)) { System.out.println("[ERROR] ID already exists."); break; }
                    System.out.print("Name       : "); String name = sc.nextLine();
                    System.out.print("Phone      : "); String phone = sc.nextLine();
                    sys.patients.add(new Patient(id, name, phone));
                    System.out.println("[SUCCESS] Patient added.");
                }
                case 2 -> {
                    var list = sys.patients.findAll();
                    if (list.isEmpty()) System.out.println("  No patients registered.");
                    else list.forEach(Patient::displayDetails);
                }
                case 3 -> { return; }
                default -> System.out.println("[ERROR] Invalid choice.");
            }
        }
    }

    // ─────────────────────────── Doctor ────────────────────────────

    static void doctorMenu() {
        while (true) {
            System.out.println("\n--- Doctor Menu ---");
            System.out.println("1. Add Doctor");
            System.out.println("2. View All Doctors");
            System.out.println("3. Back");
            System.out.print("Choice: ");

            switch (readInt()) {
                case 1 -> {
                    System.out.print("Doctor ID      : "); int id = readInt();
                    if (sys.doctors.existsById(id)) { System.out.println("[ERROR] ID already exists."); break; }
                    System.out.print("Name           : "); String name = sc.nextLine();
                    System.out.print("Phone          : "); String phone = sc.nextLine();
                    System.out.print("Specialization : "); String spec = sc.nextLine();
                    sys.doctors.add(new Doctor(id, name, phone, spec));
                    System.out.println("[SUCCESS] Doctor added.");
                }
                case 2 -> {
                    var list = sys.doctors.findAll();
                    if (list.isEmpty()) System.out.println("  No doctors registered.");
                    else list.forEach(Doctor::displayDetails);
                }
                case 3 -> { return; }
                default -> System.out.println("[ERROR] Invalid choice.");
            }
        }
    }

    // ─────────────────────────── Appointment ───────────────────────

    static void appointmentMenu() {
        while (true) {
            System.out.println("\n--- Appointment Menu ---");
            System.out.println("1. Book Appointment");
            System.out.println("2. Cancel Appointment");
            System.out.println("3. Mark Completed");
            System.out.println("4. View All");
            System.out.println("5. View Scheduled");
            System.out.println("6. Back");
            System.out.print("Choice: ");

            switch (readInt()) {
                case 1 -> bookAppointment();
                case 2 -> {
                    System.out.print("Appointment ID: "); int aid = readInt();
                    try { sys.appointments.cancelAppointment(aid); }
                    catch (HospitalException e) { System.out.println("[ERROR] " + e.getMessage()); }
                }
                case 3 -> {
                    System.out.print("Appointment ID: "); int aid = readInt();
                    try { sys.appointments.markCompleted(aid); }
                    catch (HospitalException e) { System.out.println("[ERROR] " + e.getMessage()); }
                }
                case 4 -> sys.appointments.showAppointments();
                case 5 -> sys.appointments.showByStatus(AppointmentStatus.SCHEDULED);
                case 6 -> { return; }
                default -> System.out.println("[ERROR] Invalid choice.");
            }
        }
    }

    static void bookAppointment() {
        System.out.print("Patient ID : "); int pid = readInt();
        System.out.print("Doctor  ID : "); int did = readInt();

        // Optional — safe null-free lookup
        var patient = sys.patients.findById(pid);
        var doctor  = sys.doctors.findById(did);

        if (patient.isEmpty()) { System.out.println("[ERROR] Patient not found."); return; }
        if (doctor.isEmpty())  { System.out.println("[ERROR] Doctor not found.");  return; }

        System.out.print("Date (YYYY-MM-DD) : ");
        LocalDate date = readDate();
        if (date == null) return;

        System.out.print("Time (HH:MM)      : ");
        LocalTime time = readTime();
        if (time == null) return;

        // Concurrent booking demo — wraps in a Thread to show thread-safety
        Appointment appt = new Appointment(apptId++, patient.get(), doctor.get(), date, time);
        Thread booker = new Thread(() -> {
            try { sys.appointments.bookAppointment(appt); }
            catch (HospitalException e) { System.out.println("[ERROR] " + e.getMessage()); }
        }, "BookingThread-" + appt.getId());
        booker.start();
        try { booker.join(); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    // ─────────────────────────── Billing ───────────────────────────

    static void billingMenu() {
        System.out.println("\n--- Billing ---");
        System.out.print("Patient ID : "); int id = readInt();
        System.out.print("Amount     : Rs."); double amt = readDouble();

        sys.patients.findById(id).ifPresentOrElse(
                p -> new Bill(p, amt).generateBill(),
                ()  -> System.out.println("[ERROR] Patient not found.")
        );
    }

    // ─────────────────────────── Helpers ───────────────────────────

    static int readInt() {
        while (true) {
            try {
                int v = Integer.parseInt(sc.nextLine().trim());
                return v;
            } catch (NumberFormatException e) {
                System.out.print("[ERROR] Enter a valid number: ");
            }
        }
    }

    static double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("[ERROR] Enter a valid amount: ");
            }
        }
    }

    static LocalDate readDate() {
        try {
            return LocalDate.parse(sc.nextLine().trim());
        } catch (DateTimeParseException e) {
            System.out.println("[ERROR] Invalid date format. Use YYYY-MM-DD.");
            return null;
        }
    }

    static LocalTime readTime() {
        try {
            return LocalTime.parse(sc.nextLine().trim());
        } catch (DateTimeParseException e) {
            System.out.println("[ERROR] Invalid time format. Use HH:MM.");
            return null;
        }
    }
}
