package hospital.gui;

import hospital.exception.HospitalException;
import hospital.model.Appointment;
import hospital.model.Doctor;
import hospital.model.Patient;
import hospital.service.HospitalSystem;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Simulation Panel for testing concurrent appointments
 */
public class SimulationPanel extends JPanel {
    private JTextArea outputArea;
    private HospitalSystem system = HospitalSystem.getInstance();
    
    public SimulationPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel for buttons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.NORTH);
        
        // Center panel for output
        JPanel outputPanel = createOutputPanel();
        add(outputPanel, BorderLayout.CENTER);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Concurrent Booking Simulation"));
        
        JButton runSimBtn = new JButton("Run Simulation (6 Simultaneous Bookings)");
        runSimBtn.addActionListener(e -> runSimulation());
        panel.add(runSimBtn);
        
        JButton clearBtn = new JButton("Clear Output");
        clearBtn.addActionListener(e -> outputArea.setText(""));
        panel.add(clearBtn);
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Simulation Output"));
        
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void runSimulation() {
        outputArea.setText("Starting concurrent booking simulation...\n\n");
        
        // Run simulation in a separate thread to not block GUI
        Thread simThread = new Thread(() -> {
            try {
                List<Patient> patients = system.patients.getAll();
                List<Doctor> doctors = system.doctors.getAll();
                
                if (patients.isEmpty() || doctors.isEmpty()) {
                    appendOutput("Error: Need at least 1 patient and 1 doctor!\n");
                    return;
                }
                
                Patient targetPatient = patients.get(0);
                Doctor targetDoctor = doctors.get(0);
                LocalDate targetDate = LocalDate.now().plusDays(5);
                LocalTime targetTime = LocalTime.of(10, 0);
                
                appendOutput("Target Booking Details:\n");
                appendOutput("  Patient: " + targetPatient.getName() + "\n");
                appendOutput("  Doctor: " + targetDoctor.getName() + "\n");
                appendOutput("  Date: " + targetDate + "\n");
                appendOutput("  Time: " + targetTime + "\n");
                appendOutput("  Number of Threads: 6\n\n");
                appendOutput("════════════════════════════════════════════\n");
                appendOutput("Starting concurrent requests...\n\n");
                
                // Run 6 threads trying to book the same slot
                Thread[] threads = new Thread[6];
                for (int i = 0; i < 6; i++) {
                    final int threadNum = i + 1;
                    threads[i] = new Thread(() -> {
                        try {
                            int apptId = 1000 + threadNum;
                            Appointment appt = new Appointment(apptId, targetPatient, targetDoctor, targetDate, targetTime);
                            
                            appendOutput("  [Thread-" + threadNum + "] Attempting to book slot...\n");
                            system.appointments.bookAppointment(appt);
                            appendOutput("  [Thread-" + threadNum + "] SUCCESS!\n");
                        } catch (HospitalException ex) {
                            appendOutput("  [Thread-" + threadNum + "] FAILED: " + ex.getMessage() + "\n");
                        }
                    }, "SimThread-" + threadNum);
                    threads[i].start();
                }
                
                // Wait for all threads to complete
                for (Thread t : threads) {
                    t.join();
                }
                
                appendOutput("\n════════════════════════════════════════════\n");
                appendOutput("Simulation completed!\n");
                appendOutput("\nResult: Only ONE thread should have successfully booked the slot.\n");
                appendOutput("This demonstrates thread-safe concurrent booking with ReentrantLock.\n");
                
            } catch (InterruptedException ex) {
                appendOutput("Simulation interrupted!\n");
                Thread.currentThread().interrupt();
            }
        }, "SimulationRunner");
        
        simThread.start();
    }
    
    private void appendOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(text);
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }
}
