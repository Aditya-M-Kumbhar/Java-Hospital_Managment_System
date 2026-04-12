package hospital.service;

import hospital.exception.AppointmentConflictException;
import hospital.exception.EntityNotFoundException;
import hospital.exception.HospitalException;
import hospital.model.Appointment;
import hospital.model.AppointmentStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe appointment service.
 *
 * Key concepts demonstrated:
 *  - ReentrantLock  : prevents double-booking when 2 patients hit the same slot concurrently
 *  - volatile status field in Appointment : guarantees cross-thread visibility
 *  - Streams + lambda : clean filtering / searching
 *  - Custom exception hierarchy
 */
public class AppointmentService implements Schedulable {

    private final List<Appointment> appointments = new ArrayList<>();
    // ReentrantLock instead of synchronized — gives fairness + tryLock capability
    private final ReentrantLock lock = new ReentrantLock(true);

    @Override
    public void bookAppointment(Appointment appt) throws HospitalException {
        lock.lock();
        try {
            boolean conflict = appointments.stream()
                    .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED)
                    .anyMatch(a -> a.slotKey().equals(appt.slotKey()));

            if (conflict) {
                throw new AppointmentConflictException(
                        "Slot already booked for Dr. " + appt.getDoctor().getName()
                        + " on " + appt.getDate() + " at " + appt.getTime());
            }
            appointments.add(appt);
            System.out.println("[SUCCESS] Appointment #" + appt.getId() + " booked.");
        } finally {
            lock.unlock();   // always release — even if exception thrown
        }
    }

    @Override
    public void cancelAppointment(int id) throws HospitalException {
        lock.lock();
        try {
            Appointment appt = appointments.stream()
                    .filter(a -> a.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Appointment #" + id + " not found."));

            if (appt.getStatus() == AppointmentStatus.CANCELLED) {
                System.out.println("[WARN] Already cancelled.");
                return;
            }
            appt.setStatus(AppointmentStatus.CANCELLED);
            System.out.println("[SUCCESS] Appointment #" + id + " cancelled.");
        } finally {
            lock.unlock();
        }
    }

    public void markCompleted(int id) throws HospitalException {
        lock.lock();
        try {
            Appointment appt = appointments.stream()
                    .filter(a -> a.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Appointment #" + id + " not found."));
            appt.setStatus(AppointmentStatus.COMPLETED);
            System.out.println("[SUCCESS] Appointment #" + id + " marked completed.");
        } finally {
            lock.unlock();
        }
    }

    public void showAppointments() {
        if (appointments.isEmpty()) {
            System.out.println("  No appointments found.");
            return;
        }
        appointments.forEach(Appointment::display);  // method reference
    }

    public void showByStatus(AppointmentStatus status) {
        appointments.stream()
                .filter(a -> a.getStatus() == status)
                .forEach(Appointment::display);
    }
}
