import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelImporter {
    
    public static class Person {
        private String name;
        private String job;
        
        // Constructor, getters, and setters
        public Person(String name, String job) {
            this.name = name;
            this.job = job;
        }
        
        public String getName() { return name; }
        public String getJob() { return job; }
        public void setName(String name) { this.name = name; }
        public void setJob(String job) { this.job = job; }
    }

    public static List<Person> importExcelData(String filePath) {
        List<Person> people = new ArrayList<>();
        
        try (FileInputStream file = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(file)) {
            
            Sheet sheet = workbook.getSheetAt(0); // Get first sheet
            
            // Skip header row and read next two rows
            for (int i = 1; i <= 2; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell nameCell = row.getCell(0);
                    Cell jobCell = row.getCell(1);
                    
                    String name = getCellValueAsString(nameCell);
                    String job = getCellValueAsString(jobCell);
                    
                    people.add(new Person(name, job));
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return people;
    }
    
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    // Example usage
    public static void main(String[] args) {
        List<Person> people = importExcelData("data.xlsx");
        
        // Print imported data
        for (Person person : people) {
            System.out.println("Name: " + person.getName() + 
                             ", Job: " + person.getJob());
        }
        
        // Store in variables
        if (people.size() >= 2) {
            String name1 = people.get(0).getName();
            String job1 = people.get(0).getJob();
            String name2 = people.get(1).getName();
            String job2 = people.get(1).getJob();
            
            // Now you can use these variables as needed
        }
    }
}