package hospital.service;

import hospital.model.Doctor;
import hospital.model.Patient;
import hospital.repository.Repository;

/**
 * Singleton — one hospital system per JVM.
 * Centralizes access to repositories and services.
 */
public class HospitalSystem {

    private static volatile HospitalSystem instance;   // volatile for double-checked locking

    public final Repository<Patient> patients   = new Repository<>();
    public final Repository<Doctor>  doctors    = new Repository<>();
    public final AppointmentService  appointments = new AppointmentService();

    private HospitalSystem() {}

    /** Double-checked locking — thread-safe lazy singleton */
    public static HospitalSystem getInstance() {
        if (instance == null) {
            synchronized (HospitalSystem.class) {
                if (instance == null) {
                    instance = new HospitalSystem();
                }
            }
        }
        return instance;
    }
}
