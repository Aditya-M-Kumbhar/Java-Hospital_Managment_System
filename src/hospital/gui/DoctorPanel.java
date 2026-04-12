package hospital.gui;

import hospital.model.Doctor;
import hospital.model.DoctorStatus;
import hospital.service.HospitalSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Doctor Management Panel
 */
public class DoctorPanel extends JPanel {
    private JTable doctorTable;
    private JTextField idField, nameField, phoneField, specField;
    private JComboBox<DoctorStatus> statusCombo;
    private DefaultTableModel tableModel;
    private HospitalSystem system = HospitalSystem.getInstance();
    
    public DoctorPanel() {
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
        panel.setBorder(BorderFactory.createTitledBorder("Doctor Management"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row1.add(new JLabel("Doctor ID:"));
        idField = new JTextField(15);
        row1.add(idField);
        panel.add(row1);
        
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row2.add(new JLabel("Name:"));
        nameField = new JTextField(15);
        row2.add(nameField);
        panel.add(row2);
        
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row3.add(new JLabel("Phone:"));
        phoneField = new JTextField(15);
        row3.add(phoneField);
        panel.add(row3);
        
        JPanel row4 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row4.add(new JLabel("Specialization:"));
        specField = new JTextField(15);
        row4.add(specField);
        panel.add(row4);
        
        JPanel row5 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row5.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(DoctorStatus.values());
        row5.add(statusCombo);
        panel.add(row5);
        
        JPanel buttonPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addBtn = new JButton("Add Doctor");
        addBtn.addActionListener(e -> addDoctor());
        buttonPanel1.add(addBtn);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());
        buttonPanel1.add(refreshBtn);
        panel.add(buttonPanel1);
        
        JPanel buttonPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton updateStatusBtn = new JButton("Update Status");
        updateStatusBtn.addActionListener(e -> updateStatus());
        buttonPanel2.add(updateStatusBtn);
        
        JButton blockDateBtn = new JButton("Block Date");
        blockDateBtn.addActionListener(e -> blockDate());
        buttonPanel2.add(blockDateBtn);
        
        JButton viewBlockedBtn = new JButton("View Blocked");
        viewBlockedBtn.addActionListener(e -> viewBlockedDates());
        buttonPanel2.add(viewBlockedBtn);
        panel.add(buttonPanel2);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Doctor List"));
        
        String[] columns = {"ID", "Name", "Phone", "Specialization", "Status", "Blocked Dates"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        doctorTable = new JTable(tableModel);
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        doctorTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(doctorTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void addDoctor() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String specialization = specField.getText().trim();
            
            if (name.isEmpty() || phone.isEmpty() || specialization.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Doctor doctor = new Doctor(id, name, phone, specialization);
            doctor.setStatus((DoctorStatus) statusCombo.getSelectedItem());
            system.doctors.add(doctor);
            
            JOptionPane.showMessageDialog(this, "Doctor added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            idField.setText("");
            nameField.setText("");
            phoneField.setText("");
            specField.setText("");
            statusCombo.setSelectedIndex(0);
            
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid ID format!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStatus() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);
        Doctor doctor = system.doctors.findById(doctorId).orElse(null);
        
        if (doctor != null) {
            DoctorStatus[] statusOptions = DoctorStatus.values();
            DoctorStatus newStatus = (DoctorStatus) JOptionPane.showInputDialog(this,
                    "Select new status for " + doctor.getName() + ":",
                    "Update Status",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    statusOptions,
                    doctor.getStatus());
            
            if (newStatus != null) {
                doctor.setStatus(newStatus);
                JOptionPane.showMessageDialog(this, "Status updated to " + newStatus, "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            }
        }
    }

    private void blockDate() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);
        Doctor doctor = system.doctors.findById(doctorId).orElse(null);
        
        if (doctor != null) {
            LocalDate today = LocalDate.now();
            LocalDate d1 = today.plusDays(2);
            LocalDate d2 = today.plusDays(3);
            LocalDate d3 = today.plusDays(4);
            
            Object[] options = {d1.toString(), d2.toString(), d3.toString()};
            Object choice = JOptionPane.showInputDialog(this,
                    "Select date to block for " + doctor.getName() + ":",
                    "Block Date",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            
            if (choice != null) {
                LocalDate dateToBlock = LocalDate.parse(choice.toString());
                doctor.blockDate(dateToBlock);
                JOptionPane.showMessageDialog(this, "Date " + dateToBlock + " blocked!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            }
        }
    }

    private void viewBlockedDates() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);
        Doctor doctor = system.doctors.findById(doctorId).orElse(null);
        
        if (doctor != null) {
            if (doctor.getBlockedDates().isEmpty()) {
                JOptionPane.showMessageDialog(this, "No blocked dates for " + doctor.getName(), "Blocked Dates", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder blockedDates = new StringBuilder("Blocked Dates for " + doctor.getName() + ":\n\n");
                doctor.getBlockedDates().stream().sorted().forEach(d -> blockedDates.append(d).append("\n"));
                JOptionPane.showMessageDialog(this, blockedDates.toString(), "Blocked Dates", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        
        List<Doctor> doctors = system.doctors.getAll();
        for (Doctor d : doctors) {
            Object[] row = {
                d.getId(),
                d.getName(),
                d.getPhone(),
                d.getSpecialization(),
                d.getStatus(),
                d.getBlockedDates().size()
            };
            tableModel.addRow(row);
        }
    }
}
