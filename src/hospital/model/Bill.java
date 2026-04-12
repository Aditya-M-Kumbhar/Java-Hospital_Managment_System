package hospital.model;

import java.time.LocalDateTime;

public class Bill {
    private final Patient patient;
    private final double amount;
    private final LocalDateTime generatedAt;

    public Bill(Patient patient, double amount) {
        this.patient     = patient;
        this.amount      = amount;
        this.generatedAt = LocalDateTime.now();
    }

    public Patient getPatient() { return patient; }
    public double getAmount() { return amount; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }

    public void generateBill() {
        System.out.println("  ========== BILL ===========");
        System.out.println("  Patient  : " + patient.getName());
        System.out.printf ("  Amount   : Rs.%.2f%n", amount);
        System.out.println("  Generated: " + generatedAt);
        System.out.println("  ============================");
    }
}
