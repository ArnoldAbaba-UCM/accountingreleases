/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mavenproject3;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ababa
 */
public class Mavenproject3 {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        new resulta().setVisible(true);
        
    }
    
    public static Connection conn() {
        Connection conn = null;

        try {
            String path = "Database251.accdb";
            String url = "jdbc:ucanaccess://" + path;
            conn = DriverManager.getConnection(url);
            //System.out.println("connection successful");
        } catch (SQLException e) {
            System.out.println(e);
        }
        return conn;
    }
    
    public static int getMax(){
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        int maxValue = 0;
        try {
                conn = conn();
                stmt = conn.createStatement();
                String sql = "SELECT MAX(ID) AS MaxValue FROM Table1";
                rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    // Suppose MyColumn is numeric
                    maxValue = rs.getInt("MaxValue");
                    if (rs.wasNull()) {
                        //System.out.println("gagi way sud");
                    } else {
                        //System.out.println("Max data sa table = " + maxValue);
                    }
                } else {
                    //System.out.println("no rows\n\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return maxValue;
    }
    
    public static void getExcelNigga(JFrame frame){
        Connection conn = conn();
        PreparedStatement pst = null;
        ResultSet rs = null;
        JFileChooser fileChooser = new JFileChooser();
            // Show open dialog
            int result = fileChooser.showOpenDialog(frame);
                
            if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
                    
            try {
                // Read the selected Excel file
                FileInputStream file = new FileInputStream(selectedFile);
                Workbook workbook = new XSSFWorkbook(file);
                Sheet sheet = workbook.getSheetAt(0);
                        
                // Display in console
                System.out.println("=== IMPORTED DATA ===");
                        
                // Loop through all rows
                for (Row row : sheet) {
                    // Skip empty rows
                    if (row == null) continue;

                    // Get cell values
                    Cell DateCell = row.getCell(0);
                    Cell DescriptionCell = row.getCell(1);
                    Cell DebitCell = row.getCell(3);
                    Cell CreditCell = row.getCell(4);
                    Cell AmountCell = row.getCell(5);

                    // Skip if both cells are empty
                    if (DateCell == null && DescriptionCell == null && DebitCell == null &&  CreditCell == null && AmountCell == null) continue;

//                    String Date = (DateCell != null) ? DateCell.toString() : "";
//                    String Description = (DescriptionCell != null) ? DescriptionCell.toString() : "";
//                    String Debit = (DebitCell != null) ? DebitCell.toString() : "";
//                    String Credit = (CreditCell != null) ? CreditCell.toString() : "";
//                    String Amount = (AmountCell != null) ? AmountCell.toString() : "";
//
//                    System.out.println("Date: " + Date + ", Description: " + Description+
//                                        ", Debit: "+ Debit + ", Credit: "+ Credit+", Amount: "+Amount);
                }
                        
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                        if (row != null) {
                            String Date = (row.getCell(0) != null) ? row.getCell(0).toString() : "";
                            String Description = (row.getCell(1) != null) ? row.getCell(1).toString() : "";
                            String Debit = (row.getCell(2) != null) ? row.getCell(2).toString() : "";
                            String Credit = (row.getCell(3) != null) ? row.getCell(3).toString() : "";
                            String Amount = (row.getCell(4) != null) ? row.getCell(4).toString() : "";
                            System.out.println("Date: " + Date + ", Description: " + Description+
                                        ", Debit: "+ Debit + ", Credit: "+ Credit+", Amount: "+Amount);
                            try {
                                String sql = "INSERT INTO Table1 "
                               + "([Date], [Description], [Debit Account], [Credit Account], [Amount]) "
                               + "VALUES (?, ?, ?, ?, ?)";

                                pst = conn.prepareStatement(sql);
                                pst.setString(1, Date);
                                pst.setString(2, Description);
                                pst.setString(3, Debit);
                                pst.setString(4, Credit);
                                pst.setString(5, Amount);
                                pst.executeUpdate();
                                System.out.println("Success");

                            } catch (SQLException e) {
                                System.out.println(e);

                            }
                        }
                }
                        
                        
                        workbook.close();
                        file.close();   
                    } catch (Exception ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                }
    }
    
    public static void insertData(Connection conn, PreparedStatement pst, ResultSet rs,
            String date, String desc, String debit, String credit, String amount){
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
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
}

//updated
//update3