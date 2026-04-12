package hospital.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Patient extends Person {
    private final List<String> medicalHistory = new ArrayList<>();

    public Patient(int id, String name, String phone) {
        super(id, name, phone);
    }

    public void addHistory(String record) {
        medicalHistory.add(record);
    }

    public List<String> getMedicalHistory() {
        return Collections.unmodifiableList(medicalHistory);
    }

    @Override
    public void displayDetails() {
        System.out.println("Patient  | ID: " + id + " | Name: " + name + " | Phone: " + phone);
    }
}
