package hospital.gui;

import hospital.service.HospitalSystem;
import hospital.service.DataLoader;

import javax.swing.*;

/**
 * Main GUI window for Hospital Management System
 * Uses a tabbed interface for different modules
 */
public class HospitalGUI extends JFrame {
    
    public HospitalGUI() {
        setTitle("Hospital Management System - v3.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Load demo data on startup
        HospitalSystem system = HospitalSystem.getInstance();
        if (system.patients.getAll().isEmpty()) {
            DataLoader.load(system);
        }
        
        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("👤 Patients", new PatientPanel());
        tabbedPane.addTab("👨‍⚕️ Doctors", new DoctorPanel());
        tabbedPane.addTab("📅 Appointments", new AppointmentPanel());
        tabbedPane.addTab("💰 Billing", new BillingPanel());
        tabbedPane.addTab("🔴 Simulation", new SimulationPanel());
        
        add(tabbedPane);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HospitalGUI());
    }
}
