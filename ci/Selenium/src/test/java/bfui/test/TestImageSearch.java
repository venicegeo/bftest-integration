package bfui.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import bfui.test.page.BfCreateJobWindowPage;
import bfui.test.page.BfMainPage;
import bfui.test.page.GxLoginPage;
import bfui.test.util.Info;
import bfui.test.util.Reporter;
import bfui.test.util.SauceResultReporter;
import bfui.test.util.Utils;
import bfui.test.util.Info.Importance;

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
	private String apiKeyPlanet = System.getenv("PL_API_KEY");
	private String space = System.getenv("space");
	{
		if (apiKeyPlanet == null) {
			apiKeyPlanet = "garbage";
			System.out.println("  ~~~  USING GARBAGE PL_API KEY  ~~~  ");
		} else {
			System.out.println("  ~~~  REAL PL_API KEY FOUND  ~~~  ");
		}
	}
	
	private String fromDate = "2016-11-01";
	private String toDate = "2016-11-05";

	@Rule
	public Reporter reporter = new Reporter("http://dashboard.venicegeo.io/cgi-bin/bf/" + space + "/load.pl");
	@Rule
	public TestName name = new TestName();
	@Rule
	public SauceResultReporter sauce = new SauceResultReporter();

	@Before
	public void setUp() throws Exception {
		// Setup Browser:
		driver = Utils.createSauceDriver(name.getMethodName());
		wait = new WebDriverWait(driver, 5);
		actions = new Actions(driver);
		bfMain = new BfMainPage(driver);
		gxLogin = new GxLoginPage(driver);

		// Log in to GX:
		driver.get(gxUrl);
		gxLogin.loginToGeoAxis(username, password);
		bfMain.loginSongAndDance(baseUrl);
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

	@Test @Info(importance = Importance.HIGH)
	public void image_search() throws Exception {
		// Verify Create Job Window Opens and has expected contents:
		assertTrue("Instructions should prompt user to draw a bounding box", createJobWindow.instructionText.getText().matches(".*[Dd]raw.*[Bb]ound.*"));

		Point start = new Point(50, 100);
		Point end = new Point(450, 500);
		
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
		bfMain.clickUntilResultFound(start, end, new Point(10, 10), actions);
		
		// Run Algorithm:
		createJobWindow.algorithmButton("NDWI_PY").click();
		Utils.assertThatAfterWait("Navigated to jobs page", ExpectedConditions.urlMatches(baseUrl + "jobs\\?jobId=.*"), wait);
	}
	
	@Test @Info(importance = Importance.MEDIUM)
	public void exercise_cloud_slider() throws Exception {
		
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions, 50, 100, 250, 300);
		Thread.sleep(1000);
		
		WebElement cloudSlider = createJobWindow.cloudSlider;
		
		// Exercise Cloud Cover slider:
		assertEquals("Cloud cover slider should start at 10", 10, createJobWindow.cloudSliderValue());
		
		actions.moveToElement(cloudSlider).click().build().perform();
		assertTrue("Cloud cover slider should move to center", createJobWindow.cloudSliderValue() > 45 && createJobWindow.cloudSliderValue() < 55);
		
		actions.clickAndHold(cloudSlider).moveByOffset(5, 0).release().build().perform();
		assertTrue("Cloud cover slider value should increase", createJobWindow.cloudSliderValue() > 50);
		
		actions.clickAndHold(cloudSlider).moveByOffset(-15, 0).release().build().perform();
		assertTrue("Cloud cover slider value should decrease", createJobWindow.cloudSliderValue() < 50);
		assertTrue("Cloud cover display should match value", createJobWindow.cloudText.getText().contains(cloudSlider.getAttribute("value")));
		
	}
	
	@Test @Info(importance = Importance.MEDIUM)
	public void exercise_dates() throws Exception {
		
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions, 50, 100, 250, 300);
		Thread.sleep(1000);
		
		// Enter API Key:
		createJobWindow.apiKeyEntry.clear();
		createJobWindow.apiKeyEntry.sendKeys(apiKeyPlanet);
		
		// Good dates search:
		createJobWindow.enterDates(fromDate, toDate);
		Utils.assertBecomesVisible("Loading mask should appear", createJobWindow.loadingMask, wait);
		Thread.sleep(10000); // Give extra time to find images
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
	
	@Test @Info(importance = Importance.LOW)
	public void clear_bounding_box() throws Exception {
		
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions, 50, 100, 250, 300);
		Thread.sleep(1000);
		
		createJobWindow.clearButton.click();
		
		// Make sure prompt returns:
		Utils.assertBecomesVisible("Instructions should reappear", createJobWindow.instructionText, wait);
		Utils.assertNotFound("Clear button should disappear", createJobWindow.clearButton, wait);
		
		// Make sure a bounding box can be redrawn:
		bfMain.drawBoundingBox(actions, 50, 100, 250, 300);
		
		// Verify that a bounding box was redrawn by checking that the clear button returns:
		Utils.assertBecomesVisible("Instructions should reappear", createJobWindow.clearButton, wait);
	}
	
	@Test @Info(importance = Importance.LOW)
	public void no_cloud_cover() throws Exception {
		
		// Navigate to African Islands:
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(16, -24);
		
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions, 50, 100, 350, 400);
		Thread.sleep(1000);
		
		// Enter Options:
		actions.clickAndHold(createJobWindow.cloudSlider).moveByOffset(-100, 0).release().build().perform();
		assertTrue("Cloud cover slider value should decrease", createJobWindow.cloudSliderValue() == 0);
		createJobWindow.apiKeyEntry.clear();
		createJobWindow.apiKeyEntry.sendKeys(apiKeyPlanet);
		createJobWindow.selectSource("rapideye");
		createJobWindow.enterDates("2015-01-01", "2017-02-01");
		Thread.sleep(5000);
		
		actions.moveToElement(bfMain.canvas, 350, 400).build().perform();
		
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
	
	@Test @Info(importance = Importance.LOW, bugs = {"15178"})
	public void bad_planet_key() throws Exception {
		Utils.ignoreOnInt();
		
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions, 50, 100, 450, 600);
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
	
	@Test @Info(importance = Importance.LOW, bugs = {"14668"})
	public void clear_error() throws Exception {
		Utils.ignoreOnInt();
		
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions, 50, 100, 450, 600);
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
	
	@Test @Info(importance = Importance.LOW)
	public void clear_image_detail() throws InterruptedException {
		Point start = new Point(50, 100);
		Point end = new Point(350, 400);
		
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
