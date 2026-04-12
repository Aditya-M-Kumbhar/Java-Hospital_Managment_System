package hospital.service;

import hospital.model.Appointment;
import hospital.exception.HospitalException;

public interface Schedulable {
    void bookAppointment(Appointment appt) throws HospitalException;
    void cancelAppointment(int id) throws HospitalException;
}
