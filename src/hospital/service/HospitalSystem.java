package hospital.service;

import hospital.model.Doctor;
import hospital.model.Patient;
import hospital.repository.Repository;

public class HospitalSystem {
    private static volatile HospitalSystem instance;

    public final Repository<Patient> patients     = new Repository<>();
    public final Repository<Doctor>  doctors      = new Repository<>();
    public final AppointmentService  appointments = new AppointmentService();

    private HospitalSystem() {}

    public static HospitalSystem getInstance() {
        if (instance == null) {
            synchronized (HospitalSystem.class) {
                if (instance == null) instance = new HospitalSystem();
            }
        }
        return instance;
    }
}
