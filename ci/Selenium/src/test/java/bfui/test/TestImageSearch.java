package bfui.test;

import static org.junit.Assert.*;

import java.awt.Robot;
import java.io.File;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestImageSearch {
	private WebDriver driver;
	private Actions actions;
	private WebDriverWait wait;
	private BfMainPage bfMain;
	private BfCreateJobWindowPage createJobWindow;
	private GxLoginPage gxLogin;
	
	// Strings used:
	private String baseUrl = System.getenv("bf_url");
	private String gxUrl = System.getenv("GX_url");
	private String username = System.getenv("bf_username");
	private String password = System.getenv("bf_password");
	private String driverPath = System.getenv("driver_path");
	private String browserPath = System.getenv("browser_path");
	private String apiKeyPlanet = System.getenv("API_Key_Planet");
	{
		if (apiKeyPlanet == null) {
			apiKeyPlanet = "garbage";
		}
	}
	
	private String fromDate = "2016-11-01";
	private String toDate = "2016-11-05";

	@Before
	public void setUp() throws Exception {
		// Setup Browser:
		driver = Utils.createWebDriver(browserPath, driverPath);
		wait = new WebDriverWait(driver, 5);
		actions = new Actions(driver);
		bfMain = new BfMainPage(driver);
		gxLogin = new GxLoginPage(driver);

		// Log in to GX:
		driver.get(gxUrl);
		gxLogin.loginToGeoAxis(username, password);
		
		// Verify Returned to BF:
		Utils.assertThatAfterWait("Should navigate back to BF", ExpectedConditions.urlMatches(baseUrl), wait);
		
		// Maximize so image search isn't covered.
		driver.manage().window().maximize();
		
		// Open Create Job Window:
		bfMain.createJobButton.click();
		createJobWindow = bfMain.createJobWindow();
		Utils.assertThatAfterWait("Instructions should become visible", ExpectedConditions.visibilityOf(createJobWindow.instructionText), wait);
		
		
	}

	@After
	public void tearDown() {
		driver.quit();
	}

	@Test
	public void image_search() throws Exception {
		// Verify Create Job Window Opens and has expected contents:
		assertTrue("Instructions should prompt user to draw a bounding box", createJobWindow.instructionText.getText().matches(".*[Dd]raw.*[Bb]ound.*"));
		
		// Navigate to South America:
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(-29,-49.5);
		
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions, 500, 100, 900, 600);
		Thread.sleep(1000);
		
		// Enter Options:
		createJobWindow.apiKeyEntry.clear();
		createJobWindow.apiKeyEntry.sendKeys(apiKeyPlanet);
		createJobWindow.fromDateEntry.clear();
		createJobWindow.fromDateEntry.sendKeys(fromDate);
		createJobWindow.toDateEntry.clear();
		createJobWindow.toDateEntry.sendKeys(toDate);
		createJobWindow.selectSource("rapideye");
		Utils.assertThatAfterWait("Search button should be clickable", ExpectedConditions.elementToBeClickable(createJobWindow.submitButton), wait);
		
		// Search for images:
		createJobWindow.submitButton.click();
		Thread.sleep(5000);
		
		// Zoom so a result fills the screen & click:
		for (int i = 0; i<10; i++) {
			bfMain.zoomInButton.click();
		}
		actions.moveToElement(bfMain.canvas).click().build().perform();
		
		// Run Algorithm:
		createJobWindow.algorithmButton("NDWI_PY").click();
		Utils.assertThatAfterWait("Navigated to jobs page", ExpectedConditions.urlMatches(baseUrl + "jobs\\?jobId=.*"), wait);
	}
	
	@Test
	public void exercise_cloud_slider() throws Exception {
		
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions, 500, 100, 700, 300);
		Thread.sleep(1000);
		
		WebElement cloudSlider = createJobWindow.cloudSlider;
		
		// Exercise Cloud Cover slider:
		assertEquals("Cloud cover slider should start at 10", 10, createJobWindow.cloudSliderValue());
		
		actions.moveToElement(cloudSlider).click().build().perform();
		assertEquals("Cloud cover slider should move to center", 50, createJobWindow.cloudSliderValue());
		
		actions.clickAndHold(cloudSlider).moveByOffset(5, 0).build().perform();
		assertTrue("Cloud cover slider value should increase", createJobWindow.cloudSliderValue() > 50);
		
		actions.clickAndHold(cloudSlider).moveByOffset(-15, 0).build().perform();
		assertTrue("Cloud cover slider value should decrease", createJobWindow.cloudSliderValue() < 50);
		assertTrue("Cloud cover display should match value", createJobWindow.cloudText.getText().contains(cloudSlider.getAttribute("value")));
		
	}
	
	@Test
	public void exercise_dates() throws Exception {
		
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions, 500, 100, 700, 300);
		Thread.sleep(1000);
		
		// Enter API Key:
		createJobWindow.apiKeyEntry.clear();
		createJobWindow.apiKeyEntry.sendKeys(apiKeyPlanet);
		
		// Good dates search:
		createJobWindow.enterDates(fromDate, toDate);
		Utils.assertBecomesVisible("Loading mask should appear", createJobWindow.loadingMask, wait);
		Utils.assertNotFound("Loading mask should disappear", createJobWindow.loadingMask, wait);
		
		// Try garbage fromDate search:
		createJobWindow.enterDates("garbage", toDate);
		Utils.assertBecomesVisible("Garbage 'From': Loading mask should appear", createJobWindow.loadingMask, wait);
		Utils.assertNotFound("Garbage 'From': Loading mask should disappear", createJobWindow.loadingMask, wait);
		
		// Try garbage toDate search:
		createJobWindow.enterDates(fromDate, "garbage");
		Utils.assertBecomesVisible("Garbage 'To': Loading mask should appear", createJobWindow.loadingMask, wait);
		Utils.assertNotFound("Garbage 'To': Loading mask should disappear", createJobWindow.loadingMask, wait);
	}
	
	@Test
	public void clear_bounding_box() throws Exception {
		
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions, 500, 100, 700, 300);
		Thread.sleep(1000);
		
		createJobWindow.clearButton.click();
		
		// Make sure prompt returns:
		Utils.assertBecomesVisible("Instructions should reappear", createJobWindow.instructionText, wait);
		Utils.assertNotFound("Clear button should disappear", createJobWindow.clearButton, wait);
		
		// Make sure a bounding box can be redrawn:
		bfMain.drawBoundingBox(actions, 500, 100, 700, 300);
		
		// Verify that a bounding box was redrawn by checking that the clear button returns:
		Utils.assertBecomesVisible("Instructions should reappear", createJobWindow.clearButton, wait);
	}
}
