package Scrapingdata;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import utility.ExcelReader;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.Test;

public class RecipeScraping {

	ExcelReader Reader = new ExcelReader();
	public static WebDriver driver;
	public static Properties props;

	@Test(priority = 0)
	public void launchBrowser() throws InterruptedException, IOException {

		ChromeOptions co = new ChromeOptions();
		co.addArguments("--remote-allow-origins=*");
		driver = new ChromeDriver(co);
		props = new Properties();
		String filePath = "/Users/uvaraj/eclipse-workspace/WebScraping/src/test/resources/config.properties";
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

		for (int i = 1; i <= totalpage; i++) {
			System.out.println("currentPge" + i);

			List<WebElement> recipelist = driver.findElements(By.xpath("//div[@class='rcc_recipecard']"));
			int pagesize = recipelist.size();
			System.out.println("total recipes in page:" + pagesize);

			Thread.sleep(2000);

			driver.findElement(By.xpath("//div[@id='maincontent']/div/div[@id='cardholder']/div[3]//a[" + i + "]"))
					.click();

			for (int p = 1; p <= pagesize; p++) {
				try {

					// JavascriptExecutor javascriptExecutor =(JavascriptExecutor)driver;
					// javascriptExecutor.executeScript("scroll(0,200);");

					List<WebElement> RecipeID = driver
							.findElements(By.xpath("//div[@class='rcc_recipecard'][" + p + "]/div[2]/span[1]"));
					String receipeId = null;

					if (RecipeID.isEmpty() || RecipeID.size() == 0 || RecipeID == null) {

						List<WebElement> altRecipeID = driver.findElements(
								By.xpath("//div[@id='maincontent']/div/div[2]/div[" + p + "]/div[2]/span"));
						if (!altRecipeID.isEmpty()) {
							receipeId = altRecipeID.get(0).getText();
							System.out.println("Recipe id:" + altRecipeID.get(0).getText());
						}
					} else {
						receipeId = RecipeID.get(0).getText();
						System.out.println("receipeID" + RecipeID.get(0).getText());

					}

					List<WebElement> Recipename = driver
							.findElements(By.xpath("//div[@class='rcc_recipecard'][" + p + "]/div[3]/span[1]/a"));
					String reciepename = null;

					if (Recipename.isEmpty() || Recipename.size() == 0 || Recipename == null) {

						List<WebElement> altRecipename = driver.findElements(
								By.xpath("//div[@id='maincontent']/div/div[2]/div[" + p + "]/div[3]/span[1]/a"));
						if (!altRecipename.isEmpty()) {
							reciepename = altRecipename.get(0).getText();
							System.out.println("Recipe Name:" + altRecipename.get(0).getText());
							altRecipename.get(0).click();
						}
					} else {
						reciepename = Recipename.get(0).getText();
						System.out.println("receipe Name" + Recipename.get(0).getText());
						Recipename.get(0).click();

					}

					/**
					 * WebElement Recipename = driver.findElement(By.xpath(
					 * "//div[@class='rcc_recipecard']["+p+"]/div[3]/span[1]/a"));
					 * System.out.println("Receipe name "+Recipename.getText()); String receipeName
					 * = Recipename.getText();
					 **/

					// reciepename.click();

					WebElement ingredients = driver.findElement(By.xpath("//div[@id='rcpinglist']"));
					System.out.println("ingredients name " + ingredients.getText());
					String ingredientsName = ingredients.getText();

					WebElement method = driver.findElement(By.xpath("//div[@id='recipe_small_steps']"));
					System.out.println("ingredientmethod " + method.getText());
					String methodName = method.getText();

					WebElement NutrientValue = driver.findElement(By.xpath("//div[@id='accompaniments']"));
					if (NutrientValue != null) {
						System.out.println("NutrientValue " + NutrientValue.getText());
					}
					String nutrientValue = NutrientValue.getText();

					WebElement preparationTime = driver.findElement(By.xpath("//time[@itemprop='prepTime']"));
					if (preparationTime != null) {
						System.out.println("preparationTime " + preparationTime.getText());
					}
					String preparationTimeSt = preparationTime.getText();

					WebElement cookingTime = driver.findElement(By.xpath("//time[@itemprop='cookTime']"));
					System.out.println("cookingTime " + cookingTime.getText());
					String cookTime = cookingTime.getText();

					driver.navigate().back();

					Object[] recipeData = { receipeId, reciepename, ingredientsName, methodName, nutrientValue,
							preparationTimeSt, cookTime };
					scrapedData.add(recipeData);
					Reader.writeToExcel(scrapedData);
					
				} catch (Exception e) {
					System.out.println("Exception occurred ::" + e);
				}
			}
		}
	}
}
