package hospital.gui;

import hospital.exception.HospitalException;
import hospital.model.*;
import hospital.service.HospitalSystem;
import hospital.service.SlotManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Appointment Management Panel
 */
public class AppointmentPanel extends JPanel {
    private JTable appointmentTable;
    private JComboBox<Patient> patientCombo;
    private JComboBox<Doctor> doctorCombo;
    private JSpinner dateSpinner, timeSpinner;
    private JComboBox<Object> statusFilterCombo;
    private DefaultTableModel tableModel;
    private HospitalSystem system = HospitalSystem.getInstance();
    private int nextAppointmentId = 1;
    
    public AppointmentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel for input fields
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);
        
        // Center panel for table
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Refresh table
        refreshTable();
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Appointment Management"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row1.add(new JLabel("Patient:"));
        patientCombo = new JComboBox<>();
        refreshPatientCombo();
        row1.add(patientCombo);
        panel.add(row1);
        
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row2.add(new JLabel("Doctor:"));
        doctorCombo = new JComboBox<>();
        refreshDoctorCombo();
        row2.add(doctorCombo);
        panel.add(row2);
        
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row3.add(new JLabel("Date:"));
        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        row3.add(dateSpinner);
        panel.add(row3);
        
        JPanel row4 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row4.add(new JLabel("Time Slot:"));
        timeSpinner = new JSpinner(new SpinnerListModel(SlotManager.getAllSlots()));
        row4.add(timeSpinner);
        panel.add(row4);
        
        JPanel buttonPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton bookBtn = new JButton("Book Appointment");
        bookBtn.addActionListener(e -> bookAppointment());
        buttonPanel1.add(bookBtn);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            refreshTable();
            refreshPatientCombo();
            refreshDoctorCombo();
        });
        buttonPanel1.add(refreshBtn);
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> cancelAppointment());
        buttonPanel1.add(cancelBtn);
        
        JButton completeBtn = new JButton("Mark Completed");
        completeBtn.addActionListener(e -> markCompleted());
        buttonPanel1.add(completeBtn);
        panel.add(buttonPanel1);
        
        JPanel row5 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row5.add(new JLabel("Filter by Status:"));
        statusFilterCombo = new JComboBox<>();
        statusFilterCombo.addItem("ALL");
        for (AppointmentStatus status : AppointmentStatus.values()) {
            statusFilterCombo.addItem(status);
        }
        statusFilterCombo.addActionListener(e -> refreshTable());
        row5.add(statusFilterCombo);
        panel.add(row5);
        
        JPanel buttonPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton slotsBtn = new JButton("View Available Slots");
        slotsBtn.addActionListener(e -> viewAvailableSlots());
        buttonPanel2.add(slotsBtn);
        panel.add(buttonPanel2);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Appointment List"));
        
        String[] columns = {"ID", "Patient", "Doctor", "Date", "Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        appointmentTable = new JTable(tableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void refreshPatientCombo() {
        patientCombo.removeAllItems();
        List<Patient> patients = system.patients.getAll();
        for (Patient p : patients) {
            patientCombo.addItem(p);
        }
    }
    
    private void refreshDoctorCombo() {
        doctorCombo.removeAllItems();
        List<Doctor> doctors = system.doctors.getAll();
        for (Doctor d : doctors) {
            doctorCombo.addItem(d);
        }
    }
    
    private void bookAppointment() {
        refreshPatientCombo();
        refreshDoctorCombo();
        
        try {
            if (patientCombo.getSelectedItem() == null || doctorCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please select both patient and doctor!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Patient patient = (Patient) patientCombo.getSelectedItem();
            Doctor doctor = (Doctor) doctorCombo.getSelectedItem();
            
            // Validate patient and doctor still exist
            if (system.patients.findById(patient.getId()).isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selected patient no longer exists! Refreshing list...", "Error", JOptionPane.ERROR_MESSAGE);
                refreshPatientCombo();
                return;
            }
            
            if (system.doctors.findById(doctor.getId()).isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selected doctor no longer exists! Refreshing list...", "Error", JOptionPane.ERROR_MESSAGE);
                refreshDoctorCombo();
                return;
            }
            
            java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
            LocalDate date = new java.sql.Date(selectedDate.getTime()).toLocalDate();
            
            LocalTime time = LocalTime.parse(timeSpinner.getValue().toString());
            
            Appointment appt = new Appointment(nextAppointmentId++, patient, doctor, date, time);
            system.appointments.bookAppointment(appt);
            
            JOptionPane.showMessageDialog(this, "Appointment booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } catch (HospitalException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Booking Failed", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to cancel!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
            system.appointments.cancelAppointment(appointmentId);
            JOptionPane.showMessageDialog(this, "Appointment cancelled!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } catch (HospitalException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markCompleted() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to mark as completed!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
            system.appointments.markCompleted(appointmentId);
            JOptionPane.showMessageDialog(this, "Appointment marked as COMPLETED!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } catch (HospitalException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewAvailableSlots() {
        if (doctorCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a doctor!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Doctor doctor = (Doctor) doctorCombo.getSelectedItem();
        LocalDate today = LocalDate.now();
        List<LocalTime> allSlots = SlotManager.getAllSlots();
        StringBuilder slotsInfo = new StringBuilder("Available Slots for " + doctor.getName() + "\n");
        slotsInfo.append("(Next 3 days)\n\n");
        
        for (int i = 1; i <= 3; i++) {
            LocalDate date = today.plusDays(i);
            slotsInfo.append(date);
            
            if (doctor.isBlockedOn(date)) {
                slotsInfo.append(" — [DOCTOR UNAVAILABLE]\n");
                continue;
            }
            
            slotsInfo.append("\n");
            List<LocalTime> booked = system.appointments.getBookedSlots(doctor.getId(), date);
            for (LocalTime slot : allSlots) {
                if (booked.contains(slot)) {
                    slotsInfo.append("  [BOOKED] ").append(slot).append("\n");
                } else {
                    slotsInfo.append("  [FREE] ").append(slot).append("\n");
                }
            }
            slotsInfo.append("\n");
        }
        
        JTextArea textArea = new JTextArea(slotsInfo.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Available Slots", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        
        List<Appointment> appointments = system.appointments.getAll();
        Object selectedFilter = statusFilterCombo.getSelectedItem();
        
        if (selectedFilter != null && !selectedFilter.equals("ALL")) {
            AppointmentStatus filterStatus = (AppointmentStatus) selectedFilter;
            appointments = appointments.stream()
                .filter(a -> a.getStatus() == filterStatus)
                .collect(Collectors.toList());
        }
        
        for (Appointment appt : appointments) {
            Object[] row = {
                appt.getId(),
                appt.getPatient().getName(),
                appt.getDoctor().getName(),
                appt.getDate(),
                appt.getTime(),
                appt.getStatus()
            };
            tableModel.addRow(row);
        }
    }
}
