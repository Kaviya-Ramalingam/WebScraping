package Scrapingdata;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import utility.ExcelWriter;
import utility.LoggerLoad;
import utility.SegregateData;

import org.bouncycastle.asn1.ASN1Exception;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;

public class RecipeScraping {

	ExcelWriter Writer = new ExcelWriter();
	public static WebDriver driver;
	public static Properties props;
	
	SegregateData Data= new SegregateData();

	@Test(priority = 0)
	public void launchBrowser() throws InterruptedException, IOException {

		ChromeOptions co = new ChromeOptions();
		co.addArguments("--remote-allow-origins=*");
		driver = new ChromeDriver(co);
		props = new Properties();
		String filePath = "/Users/uvaraj/git/WebsSraping/WebScraping/src/test/resources/config.properties";
		FileInputStream inputstream = new FileInputStream(filePath);
		props.load(inputstream);
		driver.get((String) props.get("URL"));
		driver.manage().window().maximize();
	}

	@Test(priority = 1)
	public void enterRecipe() {

		WebElement RecipeSearch = driver.findElement(By.name("ctl00$txtsearch"));
		RecipeSearch.sendKeys(props.getProperty("Recipe"));
		RecipeSearch.sendKeys(Keys.ENTER);
	}

	@Test(priority = 2)

	public void pagination() throws InterruptedException, IOException {

		List<WebElement> pagination = driver
				.findElements(By.xpath("//div[@id='maincontent']/div/div[@id='cardholder']/div[3]//a"));
		int paginationsize = pagination.size();
		int totalpage = 0;

		if (pagination.size() > 0) {
			System.out.println("pagination exists");
			totalpage = Integer.parseInt(pagination.get(paginationsize - 1).getText());
		}

		else {
			System.out.println("pagination not exists");
		}

		System.out.println(totalpage);

		List<Object[]> scrapedData = new ArrayList<>();

		for (int i = 1; i <= paginationsize; i++) {
			System.out.println("currentPge" + i);

			List<WebElement> recipelist = driver.findElements(By.xpath("//div[@class='rcc_recipecard']"));
			int pagesize = recipelist.size();
			System.out.println("total recipes in page:" + pagesize);

			Thread.sleep(2000);

		       WebElement page =driver.findElement(By.xpath("//div[@id='maincontent']/div/div[@id='cardholder']/div[3]//a[" + i + "]"));
			     page.click();

			for (int p = 1; p <= pagesize; p++) {
				try {


					List<WebElement> recipeID = driver
							.findElements(By.xpath("//div[@class='rcc_recipecard'][" + p + "]/div[2]/span[1]"));
					String receipeId = null;

					if (recipeID.isEmpty() || recipeID.size() == 0 || recipeID == null) {

						List<WebElement> altRecipeID = driver.findElements(
								By.xpath("//div[@id='maincontent']/div/div[2]/div[" + p + "]/div[2]/span"));
						if (!altRecipeID.isEmpty()) {
							receipeId = altRecipeID.get(0).getText();
							System.out.println("Recipe id:" + altRecipeID.get(0).getText());
						}
					} else {
						receipeId = recipeID.get(0).getText();
						System.out.println("receipeID" + recipeID.get(0).getText());

					}
					List<WebElement> recipetitle = driver
							.findElements(By.xpath("//div[@class='rcc_recipecard'][" + p + "]/div[3]/span[1]/a"));
					String reciepename = null;

					if (recipetitle.isEmpty() || recipetitle.size() == 0 || recipetitle == null) {

						List<WebElement> altRecipetitle = driver.findElements(
								By.xpath("//div[@id='maincontent']/div/div[2]/div[" + p + "]/div[3]/span[1]/a"));
						if (!altRecipetitle.isEmpty()) {
							reciepename = altRecipetitle.get(0).getText();
							System.out.println("Recipe Title:" + altRecipetitle.get(0).getText());
							altRecipetitle.get(0).click();
						}
					} else {
					reciepename = recipetitle.get(0).getText();
						System.out.println("receipe Title" + recipetitle.get(0).getText());
						recipetitle.get(0).click();

					}
					

					String url = driver.getCurrentUrl();
					System.out.println("Recipe Url :" + url);
					
					String ingredientsName = null;
					String methodName = null;
					String categoryOfFood = null;
					String categoryOfRecipe=null;
					
					List<WebElement> foodCategory = driver.findElements(By.xpath("//div[@id='show_breadcrumb']/div/span[5]/a/span"));
					if(!foodCategory.isEmpty()) {
						categoryOfFood = foodCategory.get(0).getText();
					System.out.println("food category:"+foodCategory.get(0).getText());
					}
					
					

					List<WebElement> ingredients = driver.findElements(By.xpath("//div[@id='rcpinglist']"));
					if (!ingredients.isEmpty()) {

						System.out.println("ingredients name " + ingredients.get(0).getText());
						ingredientsName = ingredients.get(0).getText();
					}

					List<WebElement> method = driver.findElements(By.xpath("//div[@id='recipe_small_steps']"));
					if (!method.isEmpty()) {
						System.out.println("ingredientmethod " + method.get(0).getText());
						methodName = method.get(0).getText();
					}
					
					String nutrientValue = null;
					String preparationTimeSt = null;
					String cookTime = null;
					try {
						List<WebElement> nutrientValues = driver.findElements(By.xpath("//div[@id='recipe_nutrients']"));
						if (!nutrientValues.isEmpty()) {
							System.out.println("NutrientValue " + nutrientValues.get(0).getText());

							nutrientValue = nutrientValues.get(0).getText();
						}

						List<WebElement> preparationTime = driver
								.findElements(By.xpath("//time[@itemprop='prepTime']"));
						if (!preparationTime.isEmpty()) {
							System.out.println("preparationTime " + preparationTime.get(0).getText());
							preparationTimeSt = preparationTime.get(0).getText();
						}

						List<WebElement> cookingTime = driver.findElements(By.xpath("//time[@itemprop='cookTime']"));
						if (!cookingTime.isEmpty()) {
							System.out.println("cookingTime " + cookingTime.get(0).getText());
							cookTime = cookingTime.get(0).getText();
							
						}
                     	

					} catch (Exception e) {
						try {
							System.out.print("Alert Check");
							driver.switchTo().alert().dismiss();
						} catch (Exception e1) {

							System.out.print("No Alerts");
							System.out.print(reciepename + " had exceptions");
						}
						
					}finally {
						driver.navigate().back();	
					}
					
					Object[] recipeData = { receipeId, reciepename, ingredientsName, methodName, nutrientValue,
							preparationTimeSt, cookTime, url ,categoryOfFood};
					scrapedData.add(recipeData);
					Writer.writeToExcel(scrapedData);
					
			

				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Exception occurred ::" + e);
				}
			}
		}
	}
	@Test(priority =4)
	
	public void eliminatingRecipes() throws Exception {
		
		Data.segregateData();
		LoggerLoad.info("elimination completed");
	}

	
}
