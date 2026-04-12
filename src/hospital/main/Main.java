package hospital.main;

import hospital.exception.HospitalException;
import hospital.model.*;
import hospital.service.*;
import hospital.repository.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

/**
 * Hospital Management System — Production Level v3.0
 *
 * Java Concepts:
 *  Abstract Class, Interface, Generics, Enum (x2), Optional,
 *  Custom Exception Hierarchy, Singleton (double-checked + volatile),
 *  ReentrantLock, synchronized, volatile, CountDownLatch,
 *  Streams, Lambda, Method References, LocalDate/LocalTime
 */
public class Main {

    private static final Scanner sc  = new Scanner(System.in);
    private static final HospitalSystem sys = HospitalSystem.getInstance();
    private static int apptId = 1;

    public static void main(String[] args) {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║    HOSPITAL MANAGEMENT SYSTEM  v3.0     ║");
        System.out.println("╚══════════════════════════════════════════╝");

        DataLoader.load(sys);  // pre-load 10 patients + 5 doctors

        while (true) {
            System.out.println("\n══════════════ MAIN MENU ══════════════");
            System.out.println("  1. Patient Menu");
            System.out.println("  2. Doctor Menu");
            System.out.println("  3. Appointment Menu");
            System.out.println("  4. Billing");
            System.out.println("  5. 🔴 Run Concurrent Booking Simulation");
            System.out.println("  6. Exit");
            System.out.print("  Choice: ");

            switch (readInt()) {
                case 1 -> patientMenu();
                case 2 -> doctorMenu();
                case 3 -> appointmentMenu();
                case 4 -> billingMenu();
                case 5 -> runSimulation();
                case 6 -> { System.out.println("\n  Goodbye! Stay healthy! 👋"); return; }
                default -> System.out.println("  [ERROR] Invalid choice.");
            }
        }
    }

    // ══════════════════════ PATIENT MENU ══════════════════════

    static void patientMenu() {
        while (true) {
            System.out.println("\n─── Patient Menu ───");
            System.out.println("  1. Add Patient");
            System.out.println("  2. View All Patients");
            System.out.println("  3. Back");
            System.out.print("  Choice: ");

            switch (readInt()) {
                case 1 -> {
                    System.out.print("  Patient ID : "); int id = readInt();
                    if (sys.patients.existsById(id)) { System.out.println("  [ERROR] ID exists."); break; }
                    System.out.print("  Name       : "); String name = sc.nextLine();
                    System.out.print("  Phone      : "); String phone = sc.nextLine();
                    sys.patients.add(new Patient(id, name, phone));
                    System.out.println("  [✓] Patient added.");
                }
                case 2 -> sys.patients.findAll().forEach(Patient::displayDetails);
                case 3 -> { return; }
            }
        }
    }

    // ══════════════════════ DOCTOR MENU ══════════════════════

    static void doctorMenu() {
        while (true) {
            System.out.println("\n─── Doctor Menu ───");
            System.out.println("  1. Add Doctor");
            System.out.println("  2. View All Doctors");
            System.out.println("  3. Update Doctor Status (Surgery/Leave)");
            System.out.println("  4. Schedule Surgery Date (block date)");
            System.out.println("  5. Back");
            System.out.print("  Choice: ");

            switch (readInt()) {
                case 1 -> {
                    System.out.print("  Doctor ID      : "); int id = readInt();
                    if (sys.doctors.existsById(id)) { System.out.println("  [ERROR] ID exists."); break; }
                    System.out.print("  Name           : "); String name = sc.nextLine();
                    System.out.print("  Phone          : "); String phone = sc.nextLine();
                    System.out.print("  Specialization : "); String spec = sc.nextLine();
                    sys.doctors.add(new Doctor(id, name, phone, spec));
                    System.out.println("  [✓] Doctor added.");
                }
                case 2 -> sys.doctors.findAll().forEach(Doctor::displayDetails);
                case 3 -> updateDoctorStatus();
                case 4 -> scheduleSurgeryDate();
                case 5 -> { return; }
            }
        }
    }

    static void updateDoctorStatus() {
        System.out.print("  Doctor ID: "); int did = readInt();
        sys.doctors.findById(did).ifPresentOrElse(doc -> {
            System.out.println("  1. AVAILABLE  2. IN_SURGERY  3. ON_LEAVE");
            System.out.print("  Choose status: ");
            switch (readInt()) {
                case 1 -> { doc.setStatus(DoctorStatus.AVAILABLE);  System.out.println("  [✓] Status: AVAILABLE"); }
                case 2 -> { doc.setStatus(DoctorStatus.IN_SURGERY); System.out.println("  [✓] Status: IN_SURGERY"); }
                case 3 -> { doc.setStatus(DoctorStatus.ON_LEAVE);   System.out.println("  [✓] Status: ON_LEAVE"); }
                default -> System.out.println("  [ERROR] Invalid.");
            }
        }, () -> System.out.println("  [ERROR] Doctor not found."));
    }

    static void scheduleSurgeryDate() {
        System.out.print("  Doctor ID: "); int did = readInt();
        sys.doctors.findById(did).ifPresentOrElse(doc -> {
            // Surgery must be 2+ days ahead — show only those dates
            LocalDate today = LocalDate.now();
            LocalDate d1 = today.plusDays(2);
            LocalDate d2 = today.plusDays(3);
            LocalDate d3 = today.plusDays(4);

            System.out.println("  Select date to block for Surgery/Leave:");
            System.out.println("  1. " + d1);
            System.out.println("  2. " + d2);
            System.out.println("  3. " + d3);
            System.out.print("  Choice (1-3): ");

            LocalDate chosen = switch (readInt()) {
                case 1 -> d1;
                case 2 -> d2;
                case 3 -> d3;
                default -> null;
            };

            if (chosen == null) { System.out.println("  [ERROR] Invalid choice."); return; }

            doc.blockDate(chosen);  // synchronized method in Doctor
            System.out.println("  [✓] Dr. " + doc.getName() + " blocked on " + chosen + " (Surgery/Leave).");
        }, () -> System.out.println("  [ERROR] Doctor not found."));
    }

    // ══════════════════════ APPOINTMENT MENU ══════════════════════

    static void appointmentMenu() {
        while (true) {
            System.out.println("\n─── Appointment Menu ───");
            System.out.println("  1. Show Available Slots");
            System.out.println("  2. Book Appointment");
            System.out.println("  3. Cancel Appointment");
            System.out.println("  4. Mark Completed");
            System.out.println("  5. View All Appointments");
            System.out.println("  6. View Scheduled Only");
            System.out.println("  7. Back");
            System.out.print("  Choice: ");

            switch (readInt()) {
                case 1 -> showAvailableSlots();
                case 2 -> bookAppointment();
                case 3 -> {
                    System.out.print("  Appointment ID: "); int id = readInt();
                    try { sys.appointments.cancelAppointment(id); }
                    catch (HospitalException e) { System.out.println("  [ERROR] " + e.getMessage()); }
                }
                case 4 -> {
                    System.out.print("  Appointment ID: "); int id = readInt();
                    try { sys.appointments.markCompleted(id); }
                    catch (HospitalException e) { System.out.println("  [ERROR] " + e.getMessage()); }
                }
                case 5 -> sys.appointments.showAppointments();
                case 6 -> sys.appointments.showByStatus(AppointmentStatus.SCHEDULED);
                case 7 -> { return; }
            }
        }
    }

    static void showAvailableSlots() {
        System.out.print("  Doctor ID: "); int did = readInt();
        sys.doctors.findById(did).ifPresentOrElse(doc -> {
            System.out.println("\n  Available slots for Dr. " + doc.getName() + " (next 3 days):");
            System.out.println("  [Note: 13:00-15:00 is lunch break — no slots]");

            LocalDate today = LocalDate.now();
            List<LocalTime> allSlots = SlotManager.getAllSlots();

            for (int i = 1; i <= 3; i++) {
                LocalDate date = today.plusDays(i);
                System.out.println("\n  📅 " + date + (doc.isBlockedOn(date) ? " ── [DOCTOR UNAVAILABLE - Surgery/Leave]" : ""));

                if (doc.isBlockedOn(date)) continue;

                List<LocalTime> booked = sys.appointments.getBookedSlots(did, date);
                System.out.print("  ");
                for (LocalTime slot : allSlots) {
                    if (booked.contains(slot)) {
                        System.out.printf("  [%-5s BOOKED]", slot);
                    } else {
                        System.out.printf("  [%-5s FREE ]", slot);
                    }
                    // new line every 4 slots for readability
                    if (allSlots.indexOf(slot) % 4 == 3) System.out.print("\n  ");
                }
                System.out.println();
            }
        }, () -> System.out.println("  [ERROR] Doctor not found."));
    }

    static void bookAppointment() {
        System.out.print("  Patient ID : "); int pid = readInt();
        System.out.print("  Doctor  ID : "); int did = readInt();

        var patient = sys.patients.findById(pid);
        var doctor  = sys.doctors.findById(did);

        if (patient.isEmpty()) { System.out.println("  [ERROR] Patient not found."); return; }
        if (doctor.isEmpty())  { System.out.println("  [ERROR] Doctor not found.");  return; }

        // Show real dates — user picks 1 / 2 / 3
        LocalDate today = LocalDate.now();
        LocalDate d1 = today.plusDays(1);
        LocalDate d2 = today.plusDays(2);
        LocalDate d3 = today.plusDays(3);

        System.out.println("\n  Select date:");
        System.out.println("  1. " + d1 + (doctor.get().isBlockedOn(d1) ? "  [UNAVAILABLE]" : ""));
        System.out.println("  2. " + d2 + (doctor.get().isBlockedOn(d2) ? "  [UNAVAILABLE]" : ""));
        System.out.println("  3. " + d3 + (doctor.get().isBlockedOn(d3) ? "  [UNAVAILABLE]" : ""));
        System.out.print("  Choice (1-3): ");

        LocalDate date = switch (readInt()) {
            case 1 -> d1;
            case 2 -> d2;
            case 3 -> d3;
            default -> null;
        };
        if (date == null) { System.out.println("  [ERROR] Invalid choice."); return; }

        // Show available slots for chosen date
        List<LocalTime> allSlots  = SlotManager.getAllSlots();
        List<LocalTime> booked    = sys.appointments.getBookedSlots(did, date);
        List<LocalTime> freeSlots = allSlots.stream()
                .filter(s -> !booked.contains(s))
                .toList();

        if (freeSlots.isEmpty()) { System.out.println("  [ERROR] No free slots on " + date); return; }

        System.out.println("\n  Available slots on " + date + ":");
        for (int i = 0; i < freeSlots.size(); i++) {
            System.out.printf("  %2d. %s%n", i + 1, freeSlots.get(i));
        }
        System.out.print("  Pick slot number: ");
        int slotChoice = readInt();
        if (slotChoice < 1 || slotChoice > freeSlots.size()) {
            System.out.println("  [ERROR] Invalid slot choice.");
            return;
        }
        LocalTime time = freeSlots.get(slotChoice - 1);

        Appointment appt = new Appointment(apptId++, patient.get(), doctor.get(), date, time);

        // Book via a new thread — shows threading in action even for single booking
        Thread booker = new Thread(() -> {
            try { sys.appointments.bookAppointment(appt); }
            catch (HospitalException e) { System.out.println("  [ERROR] " + e.getMessage()); }
        }, "BookingThread-" + appt.getId());
        booker.start();
        try { booker.join(); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    // ══════════════════════ BILLING ══════════════════════

    static void billingMenu() {
        System.out.println("\n─── Billing ───");
        System.out.print("  Patient ID : "); int id = readInt();
        System.out.print("  Amount     : Rs."); double amt = readDouble();

        sys.patients.findById(id).ifPresentOrElse(
                p  -> new Bill(p, amt).generateBill(),
                ()  -> System.out.println("  [ERROR] Patient not found.")
        );
    }

    // ══════════════════════ SIMULATION ══════════════════════

    /**
     * 🔴 The main showpiece — runs 6 patients trying to book the
     * exact same slot at the exact same time using CountDownLatch.
     * ReentrantLock ensures only 1 wins.
     */
    static void runSimulation() {
        System.out.println("\n  Pick a doctor for simulation:");
        sys.doctors.findAll().forEach(Doctor::displayDetails);
        System.out.print("  Doctor ID: "); int did = readInt();

        sys.doctors.findById(did).ifPresentOrElse(doc -> {
            LocalDate date = LocalDate.now().plusDays(1);
            LocalTime time = LocalTime.of(10, 0);

            // pick first 6 patients for the race
            List<Patient> racers = sys.patients.findAll().subList(0, 6);

            ConcurrentBookingSimulator sim = new ConcurrentBookingSimulator(sys, apptId);
            apptId = sim.simulate(racers, doc, date, time);

            System.out.println("  Current appointments after simulation:");
            sys.appointments.showByStatus(AppointmentStatus.SCHEDULED);

        }, () -> System.out.println("  [ERROR] Doctor not found."));
    }

    // ══════════════════════ INPUT HELPERS ══════════════════════

    static int readInt() {
        while (true) {
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.print("  [ERROR] Enter a number: "); }
        }
    }

    static double readDouble() {
        while (true) {
            try { return Double.parseDouble(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.print("  [ERROR] Enter valid amount: "); }
        }
    }


}
