package hospital.service;

import hospital.exception.AppointmentConflictException;
import hospital.exception.EntityNotFoundException;
import hospital.exception.HospitalException;
import hospital.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Thread-safe Appointment Service.
 *
 * Uses ReentrantLock(fair=true) so threads are served in order —
 * simulates a real hospital queue where first-come-first-served matters.
 *
 * synchronized keyword is also used in Doctor.blockDate() to show
 * both locking mechanisms in the project.
 */
public class AppointmentService implements Schedulable {

    private final List<Appointment> appointments = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock(true); // fair lock

    @Override
    public void bookAppointment(Appointment appt) throws HospitalException {
        lock.lock();
        try {
            Doctor doc  = appt.getDoctor();
            LocalDate d = appt.getDate();
            LocalTime t = appt.getTime();

            // 1. Doctor blocked on this date? (surgery/leave)
            if (doc.isBlockedOn(d)) {
                throw new AppointmentConflictException(
                        "Dr. " + doc.getName() + " is unavailable on " + d + " (Surgery/Leave scheduled).");
            }

            // 2. Valid slot?
            if (!SlotManager.isValidSlot(t)) {
                throw new AppointmentConflictException(
                        "Invalid slot " + t + ". Choose from available slots.");
            }

            // 3. Slot already taken?
            boolean conflict = appointments.stream()
                    .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED)
                    .anyMatch(a -> a.slotKey().equals(appt.slotKey()));

            if (conflict) {
                throw new AppointmentConflictException(
                        "Slot " + t + " on " + d + " already booked for Dr. " + doc.getName() + ".");
            }

            appointments.add(appt);
            System.out.println("  [OK] Thread [" + Thread.currentThread().getName()
                    + "] -> Appointment #" + appt.getId() + " booked for "
                    + appt.getPatient().getName() + " with Dr. " + doc.getName()
                    + " on " + d + " at " + t);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void cancelAppointment(int id) throws HospitalException {
        lock.lock();
        try {
            Appointment appt = findById(id);
            if (appt.getStatus() == AppointmentStatus.CANCELLED) {
                System.out.println("  [WARN] Appointment #" + id + " is already cancelled.");
                return;
            }
            appt.setStatus(AppointmentStatus.CANCELLED);
            System.out.println("  [OK] Appointment #" + id + " cancelled.");
        } finally {
            lock.unlock();
        }
    }

    public void markCompleted(int id) throws HospitalException {
        lock.lock();
        try {
            findById(id).setStatus(AppointmentStatus.COMPLETED);
            System.out.println("  [OK] Appointment #" + id + " marked as COMPLETED.");
        } finally {
            lock.unlock();
        }
    }

    /** Returns booked slots for a doctor on a date */
    public List<LocalTime> getBookedSlots(int doctorId, LocalDate date) {
        return appointments.stream()
                .filter(a -> a.getDoctor().getId() == doctorId
                        && a.getDate().equals(date)
                        && a.getStatus() == AppointmentStatus.SCHEDULED)
                .map(Appointment::getTime)
                .collect(Collectors.toList());
    }

    /** Returns all appointments */
    public List<Appointment> getAll() {
        return new ArrayList<>(appointments);
    }

    public void showAppointments() {
        if (appointments.isEmpty()) { System.out.println("  No appointments."); return; }
        appointments.forEach(Appointment::display);
    }

    public void showByStatus(AppointmentStatus status) {
        appointments.stream()
                .filter(a -> a.getStatus() == status)
                .forEach(Appointment::display);
    }

    private Appointment findById(int id) throws EntityNotFoundException {
        return appointments.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Appointment #" + id + " not found."));
    }
}
