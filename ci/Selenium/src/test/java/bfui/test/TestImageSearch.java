package bfui.test;

import static org.junit.Assert.*;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.tools.ant.taskdefs.WaitFor.Unit;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;

import bfui.test.page.BfCreateJobWindowPage;
import bfui.test.page.BfMainPage;
import bfui.test.page.CoastlineLoginPage;
import bfui.test.page.GxLoginPage;
import bfui.test.page.LoginPage;
import bfui.test.util.Info;
import bfui.test.util.Reporter;
//import bfui.test.util.SauceResultReporter;
import bfui.test.util.Utils;
import bfui.test.util.Info.Importance;

public class TestImageSearch {
	
	private WebDriver driver;
	private Actions actions;
	private WebDriverWait wait;
	private BfMainPage bfMain;
	private BfCreateJobWindowPage createJobWindow;
	private LoginPage login;
	
	// Strings used:
	private String baseUrl = System.getenv("bf_url");
	private String gxUrl = System.getenv("GX_url");
	private String username = System.getenv("bf_username");
	private String password = System.getenv("bf_password");
	private String apiKeyPlanet = System.getenv("PL_API_KEY");
	private String space = System.getenv("space");
	private String browser = System.getenv("browser");
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
	public Reporter reporter = new Reporter("http://dashboard.venicegeo.io/cgi-bin/bf_ui_" + browser + "/" + space + "/load.pl");
	@Rule
	public TestName name = new TestName();
	//@Rule
	//public SauceResultReporter sauce = new SauceResultReporter();

	@Before
	public void setUp() throws Exception {
		// Setup Browser:
		driver = Utils.createSauceDriver(name.getMethodName());
		wait = new WebDriverWait(driver, 5);
		actions = new Actions(driver);
		login = new GxLoginPage(driver);
		bfMain = new BfMainPage(driver, wait);

		// Log in to GX:
		driver.get(baseUrl);
		bfMain.geoAxisLink.click();
		Thread.sleep(5000);
		login.login(username, password);

		
		// Verify Returned to BF:
		Utils.assertThatAfterWait("Should navigate back to BF", ExpectedConditions.urlMatches(baseUrl), wait);
		
		//if (bfMain.browserSupportWindow.isDisplayed()){
		//	bfMain.browserSupportDismiss.click();
		//}
		// Open Create Job Window:
		bfMain.createJobButton.click();
		createJobWindow = bfMain.createJobWindow();
		Utils.assertThatAfterWait("Instructions should become visible", ExpectedConditions.visibilityOf(createJobWindow.instructionText), wait);
	}

	@After
	public void tearDown() {
		driver.quit();
	}
/*
	@Test @Info(importance = Importance.HIGH)
	public void image_search() throws Exception {
		// Verify Create Job Window Opens and has expected contents:
		assertTrue("Instructions should prompt user to draw a bounding box", createJobWindow.instructionText.getText().matches(".*[Dd]raw.*[Bb]ound.*"));

		Point start = new Point(-180, -300);
		Point end = new Point(-360, -600);
		
		// Navigate to South America:
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(-29,-49.5);
		bfMain.scaler.clear();
		bfMain.scaler.sendKeys("20000000");
		bfMain.scaler.sendKeys(Keys.ENTER);
		System.out.println(driver.manage().window().getSize());
		
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions,start,end);
		Thread.sleep(1000);
		
		// Enter Options:
		createJobWindow.apiKeyEntry.clear();
		createJobWindow.apiKeyEntry.sendKeys(apiKeyPlanet);
		createJobWindow.selectSource("landsat");
		actions.moveToElement(createJobWindow.cloudSlider).click().build().perform();
		Date today = new Date();

		LocalDateTime todayLDT = LocalDateTime.now();
		LocalDateTime tomorrowLDT = LocalDateTime.now().plusDays(1);
		String fromDay = (todayLDT.getDayOfMonth()<10?"0"+todayLDT.getDayOfMonth():todayLDT.getDayOfMonth()).toString();
		String fromMonth = (todayLDT.getMonthValue()<10?"0"+todayLDT.getMonthValue():todayLDT.getMonthValue()).toString();
		String toDay = (tomorrowLDT.getDayOfMonth()<10?"0"+tomorrowLDT.getDayOfMonth():tomorrowLDT.getDayOfMonth()).toString();
		String toMonth = (tomorrowLDT.getMonthValue()<10?"0"+tomorrowLDT.getMonthValue():tomorrowLDT.getMonthValue()).toString();
		fromDate=(todayLDT.getYear()-1)+"-"+fromMonth+"-"+fromDay;
		toDate=(tomorrowLDT.getYear()-1)+"-"+toMonth+"-"+toDay;
		createJobWindow.enterDates(fromDate, toDate);
		Utils.assertThatAfterWait("Search button should be clickable", ExpectedConditions.elementToBeClickable(createJobWindow.submitButton), wait);
		
		// Search for images:
		createJobWindow.submitButton.click();
		
		// Wait for search to complete:
		assertTrue("Image search should complete", createJobWindow.waitForCompleteSearch(45));
		createJobWindow.retryIfNeeded(3, 45);
		Thread.sleep(5000);
		List<WebElement> tiles =  driver.findElements(By.cssSelector(".ImagerySearchList-results > table > tbody > tr"));
		// Click until an image is found:
		//bfMain.clickUntilResultFound(start, end, new Point(10, 10), actions);
		tiles.get(0).click();
		//createJobWindow.scroll(driver, 0, 5000); //scroll way down, in case there are multiple algorithms.
		Thread.sleep(5000);
		
		// Run Algorithm:
		Utils.scrollInToView(driver, createJobWindow.algorithmButton);
		createJobWindow.algorithmButton.click();
		wait.withTimeout(45, TimeUnit.SECONDS);
		Utils.assertThatAfterWait("Navigated to jobs page", ExpectedConditions.urlMatches(baseUrl + "jobs\\?jobId=.*"), wait);
	}
	*/
	@Test @Info(importance = Importance.MEDIUM)
	public void exercise_cloud_slider() throws Exception {
		
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions, 50, 100, 250, 300);
		Thread.sleep(1000);
		
		WebElement cloudSlider = createJobWindow.cloudSlider;
		
		// Exercise Cloud Cover slider:
		assertEquals("Cloud cover slider should start at 10", 10, createJobWindow.cloudSliderValue());
		
		Utils.scrollInToView(driver, cloudSlider);
		actions.moveToElement(cloudSlider).click().build().perform();
		assertTrue("Cloud cover slider should move to center", createJobWindow.cloudSliderValue() > 45 && createJobWindow.cloudSliderValue() < 55);
		
		for (int i = 0; i < 10; i++) {
    		createJobWindow.cloudSlider.sendKeys(Keys.ARROW_RIGHT);
    		Thread.sleep(200);
		}
		assertTrue("Cloud cover slider value should increase", createJobWindow.cloudSliderValue() > 50);
		
		for (int i = 0; i < 30; i++) {
    		createJobWindow.cloudSlider.sendKeys(Keys.ARROW_LEFT);
    		Thread.sleep(200);
		}
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
		createJobWindow.submitButton.click();
		assertTrue("Image search should complete", createJobWindow.waitForCompleteSearch(45));
		createJobWindow.retryIfNeeded(3, 45);
		
		// Try garbage fromDate search:
		createJobWindow.enterDates("garbage", toDate);
		createJobWindow.submitButton.click();
		Utils.assertBecomesVisible("A warning should appear", createJobWindow.invalidDateText.get(0), wait);
		
		// Try garbage toDate search:
		createJobWindow.enterDates(fromDate, "garbage");
		createJobWindow.submitButton.click();
		Utils.assertBecomesVisible("A warning should appear", createJobWindow.invalidDateText.get(0), wait);

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
		createJobWindow.cloudSlider.click();
		Thread.sleep(1000);
		for (int i = 0; i < 51; i++) {
    		createJobWindow.cloudSlider.sendKeys(Keys.ARROW_LEFT);
    		Thread.sleep(200);
		}
		//assertTrue("Cloud cover slider value should decrease", createJobWindow.cloudSliderValue() == 0);
		createJobWindow.enterKey(apiKeyPlanet);
		createJobWindow.selectSource("landsat");
		createJobWindow.enterDates("2015-01-01", "2017-02-01");
		
		createJobWindow.submitButton.click();
		assertTrue("Image search should complete", createJobWindow.waitForCompleteSearch(45));
		createJobWindow.retryIfNeeded(3, 45);
		
		Utils.scrollInToView(driver, bfMain.canvas);
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
		//assertTrue("At least one image should be examined", examined);
	}
	
	@Test @Info(importance = Importance.LOW, bugs = {"15178"})
	public void bad_planet_key() throws Exception {
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions, 50, 100, 450, 600);
		Thread.sleep(1000);
		
		// Enter Options:
		createJobWindow.selectSource("landsat");
		createJobWindow.enterKey("garbage");
		createJobWindow.enterDates(fromDate, toDate);
		createJobWindow.submitButton.click();
		Thread.sleep(5000);
		
		Utils.assertBecomesVisible("Error Message should appear", createJobWindow.errorMessage, wait);
		//assertTrue("Error message should say that the problem is with credentials", createJobWindow.errorMessageDescription.getText().matches("(?i).*CREDENTIALS.*"));
	}
	
	@Test @Info(importance = Importance.LOW, bugs = {"14668"})
	public void clear_error() throws Exception {
		
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions, 50, 100, 450, 600);
		Thread.sleep(1000);
		createJobWindow.selectSource("landsat");
		createJobWindow.enterKey("garbage");
		createJobWindow.enterDates(fromDate, toDate);
		createJobWindow.submitButton.click();
		assertTrue("Image search should complete", createJobWindow.waitForCompleteSearch(45));
		
		Utils.assertBecomesVisible("Error Message should appear", createJobWindow.errorMessage, wait);

		createJobWindow.enterKey(apiKeyPlanet);
		createJobWindow.submitButton.click();
		assertTrue("Image search should complete", createJobWindow.waitForCompleteSearch(45));
		createJobWindow.retryIfNeeded(3, 45);
		Utils.assertNotFound("Error Message should disappear", createJobWindow.errorMessage, wait);
	}
	
	@Test @Info(importance = Importance.LOW)
	public void clear_image_detail() throws InterruptedException {
		Point start = new Point(50, 100);
		Point end = new Point(400, 450);
		
		// Navigate to Ireland:
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(52, -9);
		
		// Draw Bounding Box:
		bfMain.drawBoundingBox(actions, start, end);
		Thread.sleep(1000);
		
		// Enter Options:
		createJobWindow.enterKey(apiKeyPlanet);
		createJobWindow.selectSource("landsat");
		createJobWindow.enterDates("2016-12-01", "2017-05-01");
		
		// Submit and wait for result:
		createJobWindow.submitButton.click();
		assertTrue("Image search should complete", createJobWindow.waitForCompleteSearch(45));
		createJobWindow.retryIfNeeded(3, 45);
		
		// Click from top left to bottom right of box:
		//assertTrue("A result should appear", bfMain.clickUntilResultFound(start, end, new Point(5, 5), actions));
		List<WebElement> tiles =  driver.findElements(By.cssSelector(".ImagerySearchList-results > table > tbody > tr"));
		assertTrue("A result should appear", tiles.size()>0);
		
		
		// Search for images again:
		createJobWindow.submitButton.click();
		assertTrue("Image search should complete", createJobWindow.waitForCompleteSearch(45));
		createJobWindow.retryIfNeeded(3, 45);
		Utils.assertNotFound("Image detail should be removed after searching again", bfMain.featureDetails, wait);
	
		// click to get image detail again:
		//assertTrue("A result should appear", bfMain.clickUntilResultFound(start, end, new Point(5, 5), actions));
		tiles =  driver.findElements(By.cssSelector(".ImagerySearchList-results > table > tbody > tr"));
		assertTrue("A result should appear", tiles.size()>0);
		
		// Clear bounding box:
		createJobWindow.clearButton.click();
		Utils.assertNotFound("Image detail should be removed after clearing the bounding box", bfMain.featureDetails, wait);
	}
}
