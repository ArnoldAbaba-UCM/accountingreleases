/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.mavenproject3;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author ababa
 */
public class resulta extends javax.swing.JFrame {

    /**
     * Creates new form resulta
     */
    
    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    double CashBalance=0;
    
    public resulta() {
        initComponents();
        conn = Mavenproject3.conn();
        loadTableData();
        for (int year = 2025; year >= 0; year--) Year.addItem(String.valueOf(year));
        for (int day = 31; day >= 0; day--) Day.addItem(String.valueOf(day));
        for (int month = 12; month >= 0; month--) Month.addItem(String.valueOf(month));
        
    }
    
    private void loadTableData() {
        loadGeneralJournal();
        loadGeneralLedger();
        try {
            String sqlquery = "SELECT * FROM Table1";
            pst = conn.prepareStatement(sqlquery);
            rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0); // Clear old data
            DefaultTableModel model2 = (DefaultTableModel) jTable3.getModel();
            model2.setRowCount(0);

            while (rs.next()) {
                String Date = rs.getString("Date");
                String Description = rs.getString("Description");
                String DebitAcc = rs.getString("Debit Account");
                String CreditAcc = rs.getString("Credit Account");
                String Amount = rs.getString("Amount");
                
                

                // Add kag row
                model.addRow(new Object[]{Date, Description, DebitAcc, CreditAcc, Amount});
            }
            loadAccountsTable();
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
    
    private void loadAccountsTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
            DefaultTableModel model2 = (DefaultTableModel) jTable6.getModel();
            DefaultTableModel model3 = (DefaultTableModel) jTable7.getModel();
            DefaultTableModel model4 = (DefaultTableModel) jTable8.getModel();
            DefaultTableModel model5 = (DefaultTableModel) jTable9.getModel();
            model.setRowCount(0);
            model2.setRowCount(0);
            model3.setRowCount(0);
            model4.setRowCount(0);
            model5.setRowCount(0);

            // Define all accounts with display names and types - UPDATED TO MATCH DATABASE
            String[][] allAccounts = {
                // ASSET accounts
                {"Cash", "ASSET"},
                {"Accounts Receivable", "ASSET"},
                {"Inventory", "ASSET"},
                {"Prepaid Expenses", "ASSET"},
                {"Equipment", "ASSET"},  // Changed from "Property, Plant & Equipment (PP&E)"
                {"Intangible Assets", "ASSET"},
                {"Investments", "ASSET"},
                {"Supplies", "ASSET"},
                {"Land", "ASSET"},

                // LIABILITY accounts
                {"Accounts Payable", "LIABILITY"},
                {"Notes Payable", "LIABILITY"},
                {"Accrued Expenses Payable", "LIABILITY"},
                {"Unearned Revenue", "LIABILITY"},
                {"Long-Term Debt", "LIABILITY"},
                {"Loans Payable", "LIABILITY"},
                {"Tax Payable", "LIABILITY"},
                {"Wages Payable", "LIABILITY"},
                {"Interest Payable", "LIABILITY"},

                // EQUITY accounts
                {"Common Stock", "EQUITY"},
                {"Paid-in Capital in Excess of Par", "EQUITY"},
                {"Retained Earnings", "EQUITY"},
                {"Treasury Stock (contra-equity)", "EQUITY"},
                {"Additional Paid-in Capital", "EQUITY"},
                {"Owner's Capital", "EQUITY"},  // Note: Changed to standard apostrophe
                {"Drawings", "EQUITY"}
            };

            // Get all debit transactions from database
            String debitQuery = "SELECT [Debit Account] as Account, SUM(CAST([Amount] as DECIMAL(10,2))) as Total " +
                               "FROM Table1 GROUP BY [Debit Account]";
            pst = conn.prepareStatement(debitQuery);
            rs = pst.executeQuery();

            java.util.Map<String, Double> debitTotals = new java.util.HashMap<>();
            while (rs.next()) {
                String accountName = rs.getString("Account");
                // Remove any leading/trailing whitespace and normalize apostrophes
                accountName = accountName.trim();
                // Normalize apostrophe characters
                accountName = accountName.replace("’", "'"); // Replace curly apostrophe with standard
                debitTotals.put(accountName, rs.getDouble("Total"));
            }

            // Get all credit transactions from database  
            String creditQuery = "SELECT [Credit Account] as Account, SUM(CAST([Amount] as DECIMAL(10,2))) as Total " +
                                "FROM Table1 GROUP BY [Credit Account]";
            pst = conn.prepareStatement(creditQuery);
            rs = pst.executeQuery();

            java.util.Map<String, Double> creditTotals = new java.util.HashMap<>();
            while (rs.next()) {
                String accountName = rs.getString("Account");
                // Remove any leading/trailing whitespace and normalize apostrophes
                accountName = accountName.trim();
                // Normalize apostrophe characters
                accountName = accountName.replace("’", "'"); // Replace curly apostrophe with standard
                creditTotals.put(accountName, rs.getDouble("Total"));
            }

            // Variables to store final balances
            double cashFinalBalance = 0.0;
            double accountsReceivableFinalBalance = 0.0;
            double inventoryFinalBalance = 0.0;
            double prepaidExpensesFinalBalance = 0.0;
            double equipmentFinalBalance = 0.0;
            double intangibleAssetsFinalBalance = 0.0;
            double investmentsFinalBalance = 0.0;
            double suppliesFinalBalance = 0.0;
            double landFinalBalance = 0.0;
            double accountsPayableFinalBalance = 0.0;
            double notesPayableFinalBalance = 0.0;
            double accruedExpensesPayableFinalBalance = 0.0;
            double unearnedRevenueFinalBalance = 0.0;
            double longTermDebtFinalBalance = 0.0;
            double loansPayableFinalBalance = 0.0;
            double taxPayableFinalBalance = 0.0;
            double wagesPayableFinalBalance = 0.0;
            double interestPayableFinalBalance = 0.0;
            double commonStockFinalBalance = 0.0;
            double paidInCapitalFinalBalance = 0.0;
            double retainedEarningsFinalBalance = 0.0;
            double treasuryStockFinalBalance = 0.0;
            double additionalPaidInCapitalFinalBalance = 0.0;
            double ownersCapitalFinalBalance = 0.0;
            double dividendDrawingsFinalBalance = 0.0;

            // Calculate balance for each account based on proper accounting rules
            for (String[] account : allAccounts) {
                String displayName = account[0];
                String accountType = account[1];

                // Create two possible formats for database lookup
                String fullName1 = displayName + " [" + accountType + "]";
                String fullName2 = displayName.replace("'", "’") + " [" + accountType + "]"; // With curly apostrophe

                double debitTotal = 0.0;
                double creditTotal = 0.0;

                // Try both formats for database lookup
                if (debitTotals.containsKey(fullName1)) {
                    debitTotal = debitTotals.get(fullName1);
                } else if (debitTotals.containsKey(fullName2)) {
                    debitTotal = debitTotals.get(fullName2);
                }

                if (creditTotals.containsKey(fullName1)) {
                    creditTotal = creditTotals.get(fullName1);
                } else if (creditTotals.containsKey(fullName2)) {
                    creditTotal = creditTotals.get(fullName2);
                }

                double balance = 0.0;

                // Apply proper accounting rules:
                if (accountType.equals("ASSET")) {
                    // Assets: Debit increases, Credit decreases (Normal Debit Balance)
                    balance = debitTotal - creditTotal;
                } else if (accountType.equals("LIABILITY") || accountType.equals("EQUITY")) {
                    // Liabilities & Equity: Credit increases, Debit decreases (Normal Credit Balance)  
                    balance = creditTotal - debitTotal;
                }

                // Add to table model
                model.addRow(new Object[]{displayName, accountType, balance});

                // Assign to individual balance variables
                switch(displayName) {
                    case "Cash":
                        cashFinalBalance = balance;
                        break;
                    case "Accounts Receivable":
                        accountsReceivableFinalBalance = balance;
                        break;
                    case "Inventory":
                        inventoryFinalBalance = balance;
                        break;
                    case "Prepaid Expenses":
                        prepaidExpensesFinalBalance = balance;
                        break;
                    case "Equipment":
                        equipmentFinalBalance = balance;
                        break;
                    case "Intangible Assets":
                        intangibleAssetsFinalBalance = balance;
                        break;
                    case "Investments":
                        investmentsFinalBalance = balance;
                        break;
                    case "Supplies":
                        suppliesFinalBalance = balance;
                        break;
                    case "Land":
                        landFinalBalance = balance;
                        break;
                    case "Accounts Payable":
                        accountsPayableFinalBalance = balance;
                        break;
                    case "Notes Payable":
                        notesPayableFinalBalance = balance;
                        break;
                    case "Accrued Expenses Payable":
                        accruedExpensesPayableFinalBalance = balance;
                        break;
                    case "Unearned Revenue":
                        unearnedRevenueFinalBalance = balance;
                        break;
                    case "Long-Term Debt":
                        longTermDebtFinalBalance = balance;
                        break;
                    case "Loans Payable":
                        loansPayableFinalBalance = balance;
                        break;
                    case "Tax Payable":
                        taxPayableFinalBalance = balance;
                        break;
                    case "Wages Payable":
                        wagesPayableFinalBalance = balance;
                        break;
                    case "Interest Payable":
                        interestPayableFinalBalance = balance;
                        break;
                    case "Common Stock":
                        commonStockFinalBalance = balance;
                        break;
                    case "Paid-in Capital in Excess of Par":
                        paidInCapitalFinalBalance = balance;
                        break;
                    case "Retained Earnings":
                        retainedEarningsFinalBalance = balance;
                        break;
                    case "Treasury Stock (contra-equity)":
                        treasuryStockFinalBalance = balance;
                        break;
                    case "Additional Paid-in Capital":
                        additionalPaidInCapitalFinalBalance = balance;
                        break;
                    case "Owner's Capital":
                        ownersCapitalFinalBalance = balance;
                        break;
                    case "Drawings":
                        dividendDrawingsFinalBalance = balance;
                        break;
                }
            }

            
            if(cashFinalBalance!=0){
                model2.addRow(new Object[]{ "Cash", cashFinalBalance});
            }
            if(accountsReceivableFinalBalance!=0){
                model2.addRow(new Object[]{ "Accounts Receivable", accountsReceivableFinalBalance});
            }
            if(inventoryFinalBalance!=0){
                model2.addRow(new Object[]{ "Inventory", inventoryFinalBalance});
            }
            if(prepaidExpensesFinalBalance!=0){
                model2.addRow(new Object[]{ "Prepaid Expenses", prepaidExpensesFinalBalance});
            }
            if(equipmentFinalBalance!=0){
                model2.addRow(new Object[]{ "Equipment", equipmentFinalBalance});
            }
            if(intangibleAssetsFinalBalance!=0){
                model2.addRow(new Object[]{ "Intangible Assets", intangibleAssetsFinalBalance});
            }
            if(investmentsFinalBalance!=0){
                model2.addRow(new Object[]{ "Investments", investmentsFinalBalance});
            }
            if(suppliesFinalBalance!=0){
                model2.addRow(new Object[]{ "Supplies", suppliesFinalBalance});
            }
            if(landFinalBalance!=0){
                model2.addRow(new Object[]{ "Land", landFinalBalance});
            }
            if(accountsPayableFinalBalance!=0){
                model3.addRow(new Object[]{ "Accounts Payable", accountsPayableFinalBalance});
            }
            if(notesPayableFinalBalance!=0){
                model3.addRow(new Object[]{ "Notes Payable", notesPayableFinalBalance});
            }
            if(accruedExpensesPayableFinalBalance!=0){
                model3.addRow(new Object[]{ "Accrued Expenses Payable", accruedExpensesPayableFinalBalance});
            }
            if(unearnedRevenueFinalBalance!=0){
                model3.addRow(new Object[]{ "Unearned Revenue", unearnedRevenueFinalBalance});
            }
            if(longTermDebtFinalBalance!=0){
                model3.addRow(new Object[]{ "Long-Term Debt", longTermDebtFinalBalance});
            }
            if(loansPayableFinalBalance!=0){
                model3.addRow(new Object[]{ "Loans Payable", loansPayableFinalBalance});
            }
            if(taxPayableFinalBalance!=0){
                model3.addRow(new Object[]{ "Tax Payable", taxPayableFinalBalance});
            }
            if(wagesPayableFinalBalance!=0){
                model3.addRow(new Object[]{ "Wages Payable", wagesPayableFinalBalance});
            }
            if(interestPayableFinalBalance>0){
                model3.addRow(new Object[]{ "Interest Payable", interestPayableFinalBalance});
            }
            if(commonStockFinalBalance!=0){
                model3.addRow(new Object[]{ "Common Stock", commonStockFinalBalance});
            }
            if(paidInCapitalFinalBalance!=0){
                model3.addRow(new Object[]{ "Paid-in Capital in Excess of Par", paidInCapitalFinalBalance});
            }
            if(retainedEarningsFinalBalance!=0){
                model3.addRow(new Object[]{ "Retained Earnings", retainedEarningsFinalBalance});
            }
            if(treasuryStockFinalBalance!=0){
                model3.addRow(new Object[]{ "Treasury Stock (contra-equity)", treasuryStockFinalBalance});
            }
            if(additionalPaidInCapitalFinalBalance!=0){
                model3.addRow(new Object[]{ "Additional Paid-in Capital", additionalPaidInCapitalFinalBalance});
            }
            if(ownersCapitalFinalBalance!=0){
                model3.addRow(new Object[]{ "Owner's Capital", ownersCapitalFinalBalance});
            }
            if(dividendDrawingsFinalBalance!=0){
                model3.addRow(new Object[]{ "Drawings", dividendDrawingsFinalBalance});
            }
            double assettotalnigs = cashFinalBalance + 
                           accountsReceivableFinalBalance + 
                           inventoryFinalBalance + 
                           prepaidExpensesFinalBalance + 
                           equipmentFinalBalance + 
                           intangibleAssetsFinalBalance + 
                           investmentsFinalBalance + 
                           suppliesFinalBalance + 
                           landFinalBalance;
            
            double liabilityEquitynigs = (commonStockFinalBalance + 
                           paidInCapitalFinalBalance + 
                           retainedEarningsFinalBalance + 
                           treasuryStockFinalBalance + 
                           additionalPaidInCapitalFinalBalance + 
                           ownersCapitalFinalBalance + 
                           dividendDrawingsFinalBalance)-(accountsPayableFinalBalance + 
                           notesPayableFinalBalance + 
                           accruedExpensesPayableFinalBalance + 
                           unearnedRevenueFinalBalance + 
                           longTermDebtFinalBalance + 
                           loansPayableFinalBalance + 
                           taxPayableFinalBalance + 
                           wagesPayableFinalBalance + 
                           interestPayableFinalBalance);
            
            model4.addRow(new Object[]{ "Total Assets: ", assettotalnigs});
            model5.addRow(new Object[]{ "Total Liabilities & Equity: ", liabilityEquitynigs});
            

        } catch (Exception e) {
            System.out.println("Error loading accounts data: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading accounts: " + e.getMessage());
        }
        
    }
    
    private void loadGeneralJournal() {
        try {
            String sqlquery = "SELECT * FROM Table1";
            pst = conn.prepareStatement(sqlquery);
            rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) jTable4.getModel();
            model.setRowCount(0); // Clear old data
            DefaultTableModel model2 = (DefaultTableModel) jTable4.getModel();
            model2.setRowCount(0);

            while (rs.next()) {
                String Date = rs.getString("Date");
                String Description = rs.getString("Description");
                String DebitAcc = rs.getString("Debit Account");
                String DebAccountName = DebitAcc.split("\\[")[0];
                String CreditAcc = rs.getString("Credit Account");
                String CredAccountName = CreditAcc.split("\\[")[0];
                String Amount = rs.getString("Amount");
                
                

                // Add kag row
                model.addRow(new Object[]{Date, Description, DebAccountName, Amount, " "});
                model.addRow(new Object[] {" ", " ", CredAccountName, " ", Amount});
            }
            loadAccountsTable();
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
    
    private void loadGeneralLedger() {
        
        try {
            String sqlquery = "SELECT * FROM Table1";
            pst = conn.prepareStatement(sqlquery);
            rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) jTable5.getModel();
            model.setRowCount(0); // Clear old data
            
            while (rs.next()) {
                String Date = rs.getString("Date");
                String Description = rs.getString("Description");
                String DebitAcc = rs.getString("Debit Account");
                String CreditAcc = rs.getString("Credit Account");
                String Amount = rs.getString("Amount");
                
                

                // Add kag row
                model.addRow(new Object[]{Date, Description, DebitAcc, CreditAcc, Amount, Amount});
            }
            loadAccountsTable();
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        DescriptionField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        DebitField = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        CreditField = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        AmountField = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        Year = new javax.swing.JComboBox<>();
        Day = new javax.swing.JComboBox<>();
        Month = new javax.swing.JComboBox<>();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable6 = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable7 = new javax.swing.JTable();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable8 = new javax.swing.JTable();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTable9 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        deleteAll = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Date (YYYY-DD-MM)");

        jLabel2.setText("Description");

        jLabel3.setText("Debit Account");

        DebitField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cash [ASSET]", "Accounts Receivable [ASSET]", "Inventory [ASSET]", "Prepaid Expenses [ASSET]", "Equipment [ASSET]", "Intangible Assets [ASSET]", "Investments [ASSET]", "Supplies [ASSET]", "Land [ASSET]", "Equipment [ASSET]", "Accounts Payable [LIABILITY]", "Notes Payable [LIABILITY]", "Accrued Expenses Payable [LIABILITY]", "Unearned Revenue [LIABILITY]", "Long-Term Debt [LIABILITY]", "Loans Payable [LIABILITY]", "Tax Payable [LIABILITY]", "Wages Payable [LIABILITY]", "Interest Payable [LIABILITY]", "Common Stock [EQUITY]", "Paid-in Capital in Excess of Par [EQUITY]", "Retained Earnings [EQUITY]", "Additional Paid-in Capital [EQUITY]", "Owner’s Capital [EQUITY]", "Drawings [EQUITY]" }));
        DebitField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DebitFieldActionPerformed(evt);
            }
        });

        jLabel4.setText("Credit Account");

        CreditField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cash [ASSET]", "Accounts Receivable [ASSET]", "Inventory [ASSET]", "Prepaid Expenses [ASSET]", "Equipment [ASSET]", "Intangible Assets [ASSET]", "Investments [ASSET]", "Supplies [ASSET]", "Land [ASSET]", "Equipment [ASSET]", "Accounts Payable [LIABILITY]", "Notes Payable [LIABILITY]", "Accrued Expenses Payable [LIABILITY]", "Unearned Revenue [LIABILITY]", "Long-Term Debt [LIABILITY]", "Loans Payable [LIABILITY]", "Tax Payable [LIABILITY]", "Wages Payable [LIABILITY]", "Interest Payable [LIABILITY]", "Common Stock [EQUITY]", "Paid-in Capital in Excess of Par [EQUITY]", "Retained Earnings [EQUITY]", "Additional Paid-in Capital [EQUITY]", "Owner’s Capital [EQUITY]", "Drawings [EQUITY]" }));

        jLabel5.setText("Amount");

        AmountField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AmountFieldActionPerformed(evt);
            }
        });

        jButton2.setText("Submit");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(DescriptionField)
                            .addComponent(DebitField, 0, 772, Short.MAX_VALUE)
                            .addComponent(CreditField, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                                .addComponent(Year, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Day, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Month, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(74, 74, 74)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(AmountField))))
                .addGap(44, 44, 44))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(Year, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Day, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Month, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DescriptionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(DebitField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CreditField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(AmountField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(423, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("New Transactions", jPanel6);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Date", "Description", "Debit", "Credit", "Amount"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jLabel6.setText("Search");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(655, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 567, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(59, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Transactions", jPanel7);

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Account", "Type", "Balance"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 943, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 659, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Accounts", jPanel1);

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Date", "Description", "Account", "Debit", "Credit"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTable4);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 943, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 679, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("General Journal", jPanel2);

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Date", "Description", "Debit Account", "Credit Account", "Amount", "balance"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane4.setViewportView(jTable5);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cash [ASSET]", "Accounts Receivable [ASSET]", "Inventory [ASSET]", "Prepaid Expenses [ASSET]", "Plant & Equipment (PP&E) [ASSET]", "Intangible Assets [ASSET]", "Investments [ASSET]", "Supplies [ASSET]", "Land [ASSET]", "Equipment [ASSET]", "Accounts Payable [LIABILITY]", "Notes Payable [LIABILITY]", "Accrued Expenses Payable [LIABILITY]", "Unearned Revenue [LIABILITY]", "Long-Term Debt [LIABILITY]", "Loans Payable [LIABILITY]", "Tax Payable [LIABILITY]", "Wages Payable [LIABILITY]", "Interest Payable [LIABILITY]", "Common Stock [EQUITY]", "Paid-in Capital in Excess of Par [EQUITY]", "Retained Earnings [EQUITY]", "Treasury Stock (contra-equity) [EQUITY]", "Additional Paid-in Capital [EQUITY]", "Owner’s Capital (for sole proprietorship) [EQUITY]", "Dividend/Drawings (owner’s withdrawals) [EQUITY]" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 943, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("General Ledger", jPanel3);

        jTable6.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Asset", "Amount"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(jTable6);

        jTable7.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Liabilities & Equity", "Amount"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(jTable7);

        jTable8.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(jTable8);
        if (jTable8.getColumnModel().getColumnCount() > 0) {
            jTable8.getColumnModel().getColumn(0).setResizable(false);
            jTable8.getColumnModel().getColumn(1).setResizable(false);
        }

        jTable9.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane8.setViewportView(jTable9);
        if (jTable9.getColumnModel().getColumnCount() > 0) {
            jTable9.getColumnModel().getColumn(0).setResizable(false);
            jTable9.getColumnModel().getColumn(1).setResizable(false);
        }

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(47, 47, 47))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(204, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Balance Sheet", jPanel4);

        jButton1.setBackground(new java.awt.Color(51, 153, 255));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("ADD");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel7.setText("Accounting System");

        deleteAll.setBackground(new java.awt.Color(255, 0, 51));
        deleteAll.setText("Clear Datas");
        deleteAll.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                deleteAllMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                deleteAllMouseEntered(evt);
            }
        });
        deleteAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAllActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(26, 26, 26)
                .addComponent(deleteAll)
                .addGap(66, 66, 66))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jLabel7)
                    .addComponent(deleteAll))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 714, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        // TODO add your handling code here:
        Mavenproject3.getExcelNigga(this);
    }//GEN-LAST:event_jButton1MouseClicked

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        // TODO add your handling code here:
        DefaultTableModel ob = (DefaultTableModel) jTable2.getModel();
        TableRowSorter<DefaultTableModel> obj = new TableRowSorter<>(ob);
        jTable2.setRowSorter(obj);

        String text = jTextField1.getText();
        if (text == null || text.trim().length() == 0) {
            obj.setRowFilter(null);  // no filter → show all rows
        } else {
            obj.setRowFilter(RowFilter.regexFilter(text));
        }
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        // TODO add your handling code here:
        String date, desc, debit, credit, amount;
        date = Year.getSelectedItem()+"-"+Day.getSelectedItem()+"-"+Month.getSelectedItem();
        desc = DescriptionField.getText();
        debit = (String) DebitField.getSelectedItem();
        credit = (String) CreditField.getSelectedItem();
        amount = AmountField.getText();

        try {
            String sql = "INSERT INTO Table1 "
           + "([Date], [Description], [Debit Account], [Credit Account], [Amount]) "
           + "VALUES (?, ?, ?, ?, ?)";

            pst = conn.prepareStatement(sql);
            pst.setString(1, date);
            pst.setString(2, desc);
            pst.setString(3, debit);
            pst.setString(4, credit);
            pst.setString(5, amount);
            pst.executeUpdate();
            System.out.println("Success");
            loadTableData();
            
            Day.setSelectedIndex(0);
            Year.setSelectedIndex(0);
            Month.setSelectedIndex(0);
            DescriptionField.setText("");
            AmountField.setText("");

            DebitField.setSelectedIndex(0);
            CreditField.setSelectedIndex(0);
            AmountField.setBackground(Color.WHITE);
        } catch (SQLException e) {
            System.out.println(e);
            
            AmountField.setBackground(Color.red);
        }
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void AmountFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AmountFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AmountFieldActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel ob = (DefaultTableModel) jTable5.getModel();
        TableRowSorter<DefaultTableModel> obj = new TableRowSorter<>(ob);
        jTable5.setRowSorter(obj);
        
        String input = (String) jComboBox1.getSelectedItem();
        String text = input.split("\\[")[0];
        
        if (text == null || text.trim().length() == 0) {
            obj.setRowFilter(null);  // no filter → show all rows
        } else {
            obj.setRowFilter(RowFilter.regexFilter(text));
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void DebitFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DebitFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DebitFieldActionPerformed

    private void deleteAllMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteAllMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_deleteAllMouseEntered

    private void deleteAllMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteAllMouseClicked
        // TODO add your handling code here:
       int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to clear ALL data from Table1?\nThis action cannot be undone.", 
            "Confirm Clear Database", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return; // User cancelled
        }

        try {
            String sql = "DELETE FROM Table1";
            pst = conn.prepareStatement(sql);
            int rowsDeleted = pst.executeUpdate();

            JOptionPane.showMessageDialog(this, 
                rowsDeleted + " rows cleared from Table1 (structure preserved)", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);

            loadTableData();

        } catch (SQLException e) {
            System.out.println("Error clearing table: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error clearing table: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_deleteAllMouseClicked

    private void deleteAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAllActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_deleteAllActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(resulta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(resulta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(resulta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(resulta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new resulta().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField AmountField;
    private javax.swing.JComboBox<String> CreditField;
    private javax.swing.JComboBox<String> Day;
    private javax.swing.JComboBox<String> DebitField;
    private javax.swing.JTextField DescriptionField;
    private javax.swing.JComboBox<String> Month;
    private javax.swing.JComboBox<String> Year;
    private javax.swing.JButton deleteAll;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTable jTable6;
    private javax.swing.JTable jTable7;
    private javax.swing.JTable jTable8;
    private javax.swing.JTable jTable9;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}

//update3