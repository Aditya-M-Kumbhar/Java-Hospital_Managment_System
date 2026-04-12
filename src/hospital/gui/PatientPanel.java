package hospital.gui;

import hospital.model.Patient;
import hospital.service.HospitalSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Patient Management Panel
 */
public class PatientPanel extends JPanel {
    private JTable patientTable;
    private JTextField idField, nameField, phoneField;
    private DefaultTableModel tableModel;
    private HospitalSystem system = HospitalSystem.getInstance();
    
    public PatientPanel() {
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
        panel.setBorder(BorderFactory.createTitledBorder("Add Patient"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row1.add(new JLabel("Patient ID:"));
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
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addBtn = new JButton("Add Patient");
        addBtn.addActionListener(e -> addPatient());
        buttonPanel.add(addBtn);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Patient List"));
        
        String[] columns = {"ID", "Name", "Phone"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(patientTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void addPatient() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Patient patient = new Patient(id, name, phone);
            system.patients.add(patient);
            
            JOptionPane.showMessageDialog(this, "Patient added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            idField.setText("");
            nameField.setText("");
            phoneField.setText("");
            
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid ID format!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        
        List<Patient> patients = system.patients.getAll();
        for (Patient p : patients) {
            Object[] row = {
                p.getId(),
                p.getName(),
                p.getPhone()
            };
            tableModel.addRow(row);
        }
    }
}
