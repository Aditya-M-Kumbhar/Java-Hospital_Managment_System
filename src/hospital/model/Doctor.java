package hospital.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Doctor extends Person {
    private final String specialization;
    private volatile DoctorStatus status;
    // dates on which doctor is blocked (surgery/leave) — scheduled in advance
    private final Set<LocalDate> blockedDates = new HashSet<>();

    public Doctor(int id, String name, String phone, String specialization) {
        super(id, name, phone);
        this.specialization = specialization;
        this.status = DoctorStatus.AVAILABLE;
    }

    public String getSpecialization() { return specialization; }
    public DoctorStatus getStatus()   { return status; }
    public void setStatus(DoctorStatus s) { this.status = s; }

    /** Schedule surgery/leave for a future date (must be 2+ days ahead) */
    public synchronized void blockDate(LocalDate date) {
        blockedDates.add(date);
    }

    public synchronized boolean isBlockedOn(LocalDate date) {
        return blockedDates.contains(date);
    }

    public synchronized Set<LocalDate> getBlockedDates() {
        return new HashSet<>(blockedDates);
    }

    @Override
    public void displayDetails() {
        System.out.printf("  Doctor   | ID: %-3d | Name: %-20s | Spec: %-15s | Status: %s%n",
                id, name, specialization, status);
    }

    // @Override
    // public String toString() {
    //     return "[ID: " + id + "] Dr. " + name + " (" + specialization + ")";
    // }

    @Override
    public String toString() {
        return id + " - " + name + " (" + specialization + ")";
    }
}
