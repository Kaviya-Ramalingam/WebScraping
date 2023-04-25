package utility;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SegregateData {
	
	public void segregateData() throws Exception
	{
		String filename ="pcos recipe.xlsx";
		
		String filepath = "/Users/uvaraj/git/WebsSraping/WebScraping/src/test/resources/dataFile/pcos recipe.xlsx";
		
		ZipSecureFile.setMinInflateRatio(0);
		XSSFWorkbook workbook = new XSSFWorkbook(new File(filepath));
		
		XSSFSheet eliminateInSheet = workbook.getSheet("eliminate");
		XSSFSheet inclusionInSheet = workbook.getSheet("Addon");
		XSSFSheet allergiesInSheet = workbook.getSheet("allergies");
		
		ArrayList<String> inclusion=new ArrayList<>();
		ArrayList<String> eliminate=new ArrayList<>();
		ArrayList<String> allergies=new ArrayList<>();
		
		for(int i=0;i<eliminateInSheet.getLastRowNum();i++)
		{
			eliminate.add(eliminateInSheet.getRow(i).getCell(0).getStringCellValue().toLowerCase());
		}
		
		for(int i=0;i<inclusionInSheet.getLastRowNum();i++)
		{
			inclusion.add(inclusionInSheet.getRow(i).getCell(0).getStringCellValue().toLowerCase());
		}
		for(int i=0;i<allergiesInSheet.getLastRowNum();i++)
		{
			allergies.add(allergiesInSheet.getRow(i).getCell(0).getStringCellValue().toLowerCase());
		}
		
		LoggerLoad.info("Ingredient to be included"+inclusion.toString().toLowerCase());
		LoggerLoad.info("Ingredient to be excluded"+eliminate.toString().toLowerCase());
		LoggerLoad.info("Allergic Ingredient to be excluded"+allergies.toString().toLowerCase());
		
		XSSFSheet eliminatedsheet = workbook.getSheet("Eliminated");
		if (Objects.isNull(eliminatedsheet)) {
			eliminatedsheet = workbook.createSheet("Eliminated");
		}
		XSSFSheet includedSheet = workbook.getSheet("Included");
		if (Objects.isNull(includedSheet)) {
			includedSheet = workbook.createSheet("Included");
		}
		XSSFSheet recipiesAfterEliminatedSheet = workbook.getSheet("RecipiesAfterEliminated");
		if (Objects.isNull(recipiesAfterEliminatedSheet)) {
			recipiesAfterEliminatedSheet = workbook.createSheet("RecipiesAfterEliminated");
		}
		XSSFSheet nonallergicrecipes = workbook.getSheet("nonallergicrecipes");
		if (Objects.isNull(nonallergicrecipes)) {
			nonallergicrecipes = workbook.createSheet("nonallergicrecipes");
		}
		XSSFSheet fullSheet = workbook.getSheet("Recipes");
		
		String[] headers = { "ReceipeID", "Recipe Name", "Ingredients", "Method", "Nutrient Values", "Preparation Time",
		"Cooking Time" };
		System.out.println("Elimated last row::"+eliminatedsheet.getLastRowNum());
		System.out.println("Included last row::"+includedSheet.getLastRowNum());
		XSSFRow eliminatedSheetHeaderRow = eliminatedsheet.getLastRowNum()==0 ? eliminatedsheet.createRow(0):null;
		XSSFRow includedSheetHeaderRow = includedSheet.getLastRowNum()==0 ? includedSheet.createRow(0):null;
		XSSFRow recipiesAfterEliminatedHeaderRow= recipiesAfterEliminatedSheet.getLastRowNum()==0 ? recipiesAfterEliminatedSheet.createRow(0):null;
		XSSFRow nonallergicrecipesSheetHeaderRow = nonallergicrecipes.getLastRowNum()==0 ? nonallergicrecipes.createRow(0):null;
		try
		{
		 for (int i = 0; i < headers.length; i++) 
		  {
			if (Objects.nonNull(eliminatedSheetHeaderRow))
					 eliminatedSheetHeaderRow.createCell(i).setCellValue(headers[i]);
			if (Objects.nonNull(includedSheetHeaderRow))
				includedSheetHeaderRow.createCell(i).setCellValue(headers[i]);
			if (Objects.nonNull(recipiesAfterEliminatedHeaderRow))
				recipiesAfterEliminatedHeaderRow.createCell(i).setCellValue(headers[i]);
			if (Objects.isNull(nonallergicrecipesSheetHeaderRow))
				nonallergicrecipesSheetHeaderRow.createCell(i).setCellValue(headers[i]);
			
				
		  }
		 }
		 catch(Exception e)
		 {
			System.out.print("Exception while creating headers");
					
		 }
		int eliminatedRowNumber = 1;
		int includededRowNumber = 1;
		int missedRowNumber = 1;
		int nonallergicrecipesRowNumber = 1;
		
		for(int i=1; i<fullSheet.getLastRowNum();i++)
		{
			//System.out.print(fullSheet.getRow(i).getCell(2).getStringCellValue().toLowerCase());
			String elimString =getMatchingSubstring(fullSheet.getRow(i).getCell(2).getStringCellValue().toLowerCase(), eliminate);
			String inclString = getMatchingSubstring(fullSheet.getRow(i).getCell(2).getStringCellValue().toLowerCase(),inclusion); 
			String alString = getMatchingSubstring(fullSheet.getRow(i).getCell(2).getStringCellValue().toLowerCase(), allergies);
			
			if (  elimString!=null)
			{
				LoggerLoad.info(fullSheet.getRow(i).getCell(1).getStringCellValue() + " contains "+ elimString + " ingredients to be eliminated");
				eliminatedsheet.createRow(eliminatedRowNumber++).copyRowFrom(fullSheet.getRow(i), new CellCopyPolicy());
			}
			else if ( inclString!=null)
			{
			    
				System.out.printf(fullSheet.getRow(i).getCell(1).getStringCellValue() + " contains "+ inclString +" ingredients to be included");
				includedSheet.createRow(includededRowNumber++).copyRowFrom(fullSheet.getRow(i), new CellCopyPolicy());
			}
			else if ( alString==null)
			{
			    
				System.out.println(fullSheet.getRow(i).getCell(1).getStringCellValue() + " contains "+ alString +" allergic ingredients to be excluded");
				nonallergicrecipes.createRow(nonallergicrecipesRowNumber++).copyRowFrom(fullSheet.getRow(i), new CellCopyPolicy());
			}
			else
			{
				recipiesAfterEliminatedSheet.createRow(missedRowNumber++).copyRowFrom(fullSheet.getRow(i), new CellCopyPolicy());
			}
		}
	
		
		
		LoggerLoad.info("Total Records processed : " + fullSheet.getLastRowNum());
		LoggerLoad.info("Eliminated Records : " + eliminatedsheet.getLastRowNum());
		LoggerLoad.info("Addon Records : " + includedSheet.getLastRowNum());
		LoggerLoad.info("RecipiesAfterEliminated Records : " + recipiesAfterEliminatedSheet.getLastRowNum());
;		LoggerLoad.info("Nonallergicrecipes : " + nonallergicrecipes.getLastRowNum());
		
		FileOutputStream outputStream = new FileOutputStream(filename,true);
		workbook.write(outputStream);
		workbook.close();
		System.out.println("Data saved to " + filename);
		

	}
private static String getMatchingSubstring(String str, List<String> substrings) {
		
        for (String substring : substrings) {
       
            if (str.contains(substring)) {
                return substring;
            }
        }
        return null;
}
}
