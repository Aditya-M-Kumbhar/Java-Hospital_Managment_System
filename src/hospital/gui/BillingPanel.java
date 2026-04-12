package hospital.gui;

import hospital.model.Bill;
import hospital.model.Patient;
import hospital.service.HospitalSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Billing Management Panel
 */
public class BillingPanel extends JPanel {
    private JTable billTable;
    private JComboBox<Patient> patientCombo;
    private JTextField amountField;
    private DefaultTableModel tableModel;
    private HospitalSystem system = HospitalSystem.getInstance();
    private List<Bill> bills = new ArrayList<>();
    
    public BillingPanel() {
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
        panel.setBorder(BorderFactory.createTitledBorder("Generate Bill"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row1.add(new JLabel("Patient:"));
        patientCombo = new JComboBox<>();
        refreshPatientCombo();
        row1.add(patientCombo);
        panel.add(row1);
        
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row2.add(new JLabel("Amount (Rs):"));
        amountField = new JTextField(15);
        row2.add(amountField);
        panel.add(row2);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton generateBtn = new JButton("Generate Bill");
        generateBtn.addActionListener(e -> generateBill());
        buttonPanel.add(generateBtn);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            refreshTable();
            refreshPatientCombo();
        });
        buttonPanel.add(refreshBtn);
        
        JButton viewBtn = new JButton("View Bill");
        viewBtn.addActionListener(e -> viewBill());
        buttonPanel.add(viewBtn);
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Generated Bills"));
        
        String[] columns = {"ID", "Patient", "Amount (Rs)", "Generated At"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        billTable = new JTable(tableModel);
        billTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(billTable);
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
    
    private void generateBill() {
        try {
            if (patientCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please select a patient!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String amountStr = amountField.getText().trim();
            if (amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an amount!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Patient patient = (Patient) patientCombo.getSelectedItem();
            double amount = Double.parseDouble(amountStr);
            
            Bill bill = new Bill(patient, amount);
            bills.add(bill);
            
            JOptionPane.showMessageDialog(this, "Bill generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            amountField.setText("");
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount format!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewBill() {
        int selectedRow = billTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to view!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedRow < bills.size()) {
            Bill bill = bills.get(selectedRow);
            StringBuilder billDetails = new StringBuilder();
            billDetails.append("========== BILL ===========\n");
            billDetails.append("Patient  : ").append(bill.getPatient().getName()).append("\n");
            billDetails.append(String.format("Amount   : Rs.%.2f\n", bill.getAmount()));
            billDetails.append("Generated: ").append(bill.getGeneratedAt()).append("\n");
            billDetails.append("=============================");
            
            JOptionPane.showMessageDialog(this, billDetails.toString(), "Bill Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        
        for (int i = 0; i < bills.size(); i++) {
            Bill b = bills.get(i);
            Object[] row = {
                i + 1,
                b.getPatient().getName(),
                String.format("%.2f", b.getAmount()),
                b.getGeneratedAt()
            };
            tableModel.addRow(row);
        }
    }
}
