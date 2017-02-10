package bfui.test;

import static org.junit.Assert.*;

import java.awt.Robot;
import java.io.File;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Point;
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
	private String apiKeyPlanet = System.getenv("PL_API_KEY");
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
		driver.manage().window().maximize();
		
		// Verify Returned to BF:
		Utils.assertThatAfterWait("Should navigate back to BF", ExpectedConditions.urlMatches(baseUrl), wait);
		
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

		Point start = new Point(500, 100);
		Point end = new Point(900, 500);
		
		// Navigate to South America:
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(-29,-49.5);
		
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions, start, end);
		Thread.sleep(1000);
		
		// Enter Options:
		createJobWindow.apiKeyEntry.clear();
		createJobWindow.apiKeyEntry.sendKeys(apiKeyPlanet);
		createJobWindow.selectSource("rapideye");
		createJobWindow.enterDates(fromDate, toDate);
		Utils.assertThatAfterWait("Search button should be clickable", ExpectedConditions.elementToBeClickable(createJobWindow.submitButton), wait);
		
		// Search for images:
		createJobWindow.submitButton.click();
		Thread.sleep(5000);
		
		// Click until an image is found:
		bfMain.clickUntilResultFound(start, end, new Point(5, 5), actions);
		
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
		Utils.assertBecomesVisible("Garbage 'From': Warning should appear", createJobWindow.invalidDateText, wait);
		assertTrue("Warning mentions 'From' field", createJobWindow.invalidDateText.getText().contains("From"));
		
		// Try garbage toDate search:
		createJobWindow.enterDates(fromDate, "garbage");
		Utils.assertBecomesVisible("Garbage 'To': Warning should appear", createJobWindow.invalidDateText, wait);
		assertTrue("Warning mentions 'To' field", createJobWindow.invalidDateText.getText().contains("To"));
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
	
	@Test
	public void no_cloud_cover() throws Exception {
		
		// Navigate to African Islands:
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(16, -24);
		
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions, 500, 100, 800, 400);
		Thread.sleep(1000);
		
		// Enter Options:
		actions.clickAndHold(createJobWindow.cloudSlider).moveByOffset(-100, 0).release().build().perform();
		createJobWindow.apiKeyEntry.clear();
		createJobWindow.apiKeyEntry.sendKeys(apiKeyPlanet);
		createJobWindow.selectSource("rapideye");
		createJobWindow.enterDates("2015-01-01", "2017-02-01");
		Thread.sleep(5000);
		
		actions.moveToElement(bfMain.canvas, 800, 400).build().perform();
		
		boolean examined = false;
		for (int i = 0; i<30; i++) {
			actions.moveByOffset(-10, -10).click().build().perform();
			Thread.sleep(1000);
			if (Utils.checkExists(bfMain.featureDetails)) {
				assertEquals("Cloud Cover should be zero", 0, bfMain.getFeatureCloudCover());
				examined = true;
			}
		}
		assertTrue("At least one image should be examined", examined);
	}
	
	@Test
	public void bad_planet_key() throws Exception {
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions, 500, 100, 900, 600);
		Thread.sleep(1000);
		
		// Enter Options:
		createJobWindow.selectSource("rapideye");
		createJobWindow.apiKeyEntry.clear();
		createJobWindow.apiKeyEntry.sendKeys("garbage");
		createJobWindow.enterDates(fromDate, toDate);
		createJobWindow.selectSource("rapideye");
		Thread.sleep(5000);
		
		Utils.assertBecomesVisible("Error Message should appear", createJobWindow.errorMessage, wait);
		assertTrue("Error message should say that the problem is with the API Key", createJobWindow.errorMessageDescription.getText().matches("(?i).*API.*KEY.*"));
	}
	
	@Test
	public void clear_error() throws Exception {
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions, 500, 100, 900, 600);
		Thread.sleep(1000);
		createJobWindow.apiKeyEntry.clear();
		createJobWindow.selectSource("rapideye");
		createJobWindow.apiKeyEntry.sendKeys("garbage");
		createJobWindow.enterDates(fromDate, toDate);
		Thread.sleep(5000);
		
		Utils.assertBecomesVisible("Error Message should appear", createJobWindow.errorMessage, wait);

		createJobWindow.apiKeyEntry.clear();
		createJobWindow.apiKeyEntry.sendKeys(apiKeyPlanet);
		createJobWindow.submitButton.click();
		Thread.sleep(5000);
		Utils.assertNotFound("Error Message should disappear", createJobWindow.errorMessage, wait);
	}
	
	@Test
	public void clear_image_detail() throws InterruptedException {
		Point start = new Point(500, 100);
		Point end = new Point(800, 400);
		
		// Navigate to Ireland:
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(52, -9);
		
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions, start, end);
		Thread.sleep(1000);
		
		// Enter Options:
		createJobWindow.apiKeyEntry.clear();
		createJobWindow.apiKeyEntry.sendKeys(apiKeyPlanet);
		createJobWindow.selectSource("rapideye");
		createJobWindow.enterDates("2016-12-01", "2017-01-01");
		Thread.sleep(5000);
		
		// Click from top left to bottom right of box:
		assertTrue("A result should appear", bfMain.clickUntilResultFound(start, end, new Point(5, 5), actions));
		
		// Search for images again:
		createJobWindow.submitButton.click();
		Thread.sleep(5000);
		Utils.assertNotFound("Image detail should be removed after searching again", bfMain.featureDetails, wait);
	
		// click to get image detail again:
		assertTrue("A result should appear", bfMain.clickUntilResultFound(start, end, new Point(5, 5), actions));
		
		// Clear bounding box:
		createJobWindow.clearButton.click();
		Utils.assertNotFound("Image detail should be removed after clearing the bounding box", bfMain.featureDetails, wait);
	}
}
