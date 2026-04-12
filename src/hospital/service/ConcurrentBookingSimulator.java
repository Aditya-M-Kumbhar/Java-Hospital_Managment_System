package hospital.service;

import hospital.exception.HospitalException;
import hospital.model.Appointment;
import hospital.model.Doctor;
import hospital.model.Patient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Simulates N patients trying to book the SAME doctor slot simultaneously.
 *
 * CountDownLatch is used so all threads START at exactly the same time
 * — this is the real stress test that shows ReentrantLock in action.
 *
 * Expected result: Only 1 booking succeeds. All others get conflict error.
 */
public class ConcurrentBookingSimulator {

    private final HospitalSystem sys;
    private int apptIdCounter;

    public ConcurrentBookingSimulator(HospitalSystem sys, int startId) {
        this.sys = sys;
        this.apptIdCounter = startId;
    }

    public int simulate(List<Patient> patients, Doctor doctor, LocalDate date, LocalTime time) {
        System.out.println("\n  ════════════════════════════════════════════════════");
        System.out.println("  🔴 CONCURRENT BOOKING SIMULATION");
        System.out.println("  " + patients.size() + " patients racing to book Dr. "
                + doctor.getName() + " | " + date + " | " + time);
        System.out.println("  ════════════════════════════════════════════════════");

        // CountDownLatch — all threads wait at the gate, then start together
        CountDownLatch startGate = new CountDownLatch(1);
        List<Thread> threads = new ArrayList<>();

        for (Patient p : patients) {
            final int id = apptIdCounter++;
            Thread t = new Thread(() -> {
                try {
                    startGate.await(); // wait for gun shot
                    Appointment appt = new Appointment(id, p, doctor, date, time);
                    sys.appointments.bookAppointment(appt);
                } catch (HospitalException e) {
                    System.out.println("  [✗] Thread [" + Thread.currentThread().getName()
                            + "] → " + p.getName() + " FAILED: " + e.getMessage());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }, "Patient-" + p.getName().split(" ")[0]);
            threads.add(t);
        }

        threads.forEach(Thread::start);
        startGate.countDown(); // 🚦 ALL threads released at same time

        threads.forEach(t -> {
            try { t.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        System.out.println("  ════════════════════════════════════════════════════");
        System.out.println("  ✅ Simulation complete. Only 1 booking should succeed.");
        System.out.println("  ════════════════════════════════════════════════════\n");

        return apptIdCounter;
    }
}
