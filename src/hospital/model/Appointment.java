package hospital.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private final int id;
    private final Patient patient;
    private final Doctor doctor;
    private final LocalDate date;
    private final LocalTime time;
    private volatile AppointmentStatus status;

    public Appointment(int id, Patient patient, Doctor doctor, LocalDate date, LocalTime time) {
        this.id      = id;
        this.patient = patient;
        this.doctor  = doctor;
        this.date    = date;
        this.time    = time;
        this.status  = AppointmentStatus.SCHEDULED;
    }

    public int getId()                   { return id; }
    public Doctor getDoctor()            { return doctor; }
    public Patient getPatient()          { return patient; }
    public LocalDate getDate()           { return date; }
    public LocalTime getTime()           { return time; }
    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus s) { this.status = s; }

    /** Unique key per doctor+date+time slot */
    public String slotKey() {
        return doctor.getId() + "_" + date + "_" + time;
    }

    public void display() {
        System.out.printf("  Appt #%-3d | Patient: %-15s | Doctor: %-15s | %s %s | [%s]%n",
                id, patient.getName(), doctor.getName(), date, time, status);
    }
}
