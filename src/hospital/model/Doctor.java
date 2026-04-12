package hospital.model;

public class Doctor extends Person {
    private final String specialization;

    public Doctor(int id, String name, String phone, String specialization) {
        super(id, name, phone);
        this.specialization = specialization;
    }

    public String getSpecialization() { return specialization; }

    @Override
    public void displayDetails() {
        System.out.println("Doctor   | ID: " + id + " | Name: " + name
                + " | Phone: " + phone + " | Spec: " + specialization);
    }
}
