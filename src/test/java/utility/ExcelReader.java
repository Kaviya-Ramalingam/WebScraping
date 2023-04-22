package utility;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {
	
	 public void writeToExcel(List<Object[]> data) throws IOException {
	        XSSFWorkbook workbook = new XSSFWorkbook();
	        XSSFSheet sheet = workbook.createSheet("Recipes");
	        
	        // Create header row
	        String[] headers = {"RecipeID" ,"Recipe Name","ingredients","method","NutrientValue","preparationTime","cookingTime"};
	        XSSFRow headerRow = sheet.createRow(0);
	        for (int i = 0; i < headers.length; i++) {
	            XSSFCell cell = headerRow.createCell(i);
	            cell.setCellValue(headers[i]);
	            XSSFCellStyle style = workbook.createCellStyle();
	            style.setWrapText(true);
	            cell.setCellStyle(style);
	        }
	        
	        // Add data to rows
	        int rowNumber = 1;
	        for (Object[] rowData : data) {
	            XSSFRow row = sheet.createRow(rowNumber++);
	            int cellNumber = 0;
	            for (Object cellData : rowData) {
	                XSSFCell cell = row.createCell(cellNumber++);
	               if (cellData instanceof String )
	                cell.setCellValue((String)cellData);
	               if (cellData instanceof Integer)
	            	   cell.setCellValue((Integer)cellData);
	               if (cellData instanceof Boolean)
	            	   cell.setCellValue((Boolean)cellData);
	                XSSFCellStyle style = workbook.createCellStyle();
	                style.setWrapText(true);
	                cell.setCellStyle(style);
	            }
	        }
	        
	        // Save the workbook to a file
	        String filename = "recipes.xlsx";
	        FileOutputStream outputStream = new FileOutputStream(filename);
	        workbook.write(outputStream);
	        workbook.close();
	        System.out.println("Data saved to " + filename);
	    }
	

}
