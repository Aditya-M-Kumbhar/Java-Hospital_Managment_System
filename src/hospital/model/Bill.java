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

    public void generateBill() {
        System.out.println("========== BILL ==========");
        System.out.println("Patient  : " + patient.getName());
        System.out.println("Amount   : Rs." + String.format("%.2f", amount));
        System.out.println("Generated: " + generatedAt);
        System.out.println("==========================");
    }
}
