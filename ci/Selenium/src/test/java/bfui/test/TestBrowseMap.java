package bfui.test;

import static org.junit.Assert.*;

import java.awt.Robot;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestBrowseMap {
	private WebDriver driver;
	private Robot robot;
	private WebDriverWait wait;
	private GxLoginPage gxLogin;
	private BfMainPage bfMain;
	private BfSearchWindowPage bfSearchWindow;
	
	
	
	// Strings used:
	private String baseUrl = System.getenv("bf_url");
	private String gxUrl = System.getenv("GX_url");
	private String username = System.getenv("bf_username");
	private String password = System.getenv("bf_password");
	private String driverPath = System.getenv("driver_path");
	private String browserPath = System.getenv("browser_path");
	
	
	
	private static double SriLankaLat = 7.5;
	private static double SriLankaLon = 80;
	private static double PosDateLineLat = 20;
	private static double PosDateLineLon = 180;
	private static double NegDateLineLat = 20;
	private static double NegDateLineLon = -180;
	private static double NorthPoleLat = 90;
	private static double NorthPoleLon = 5;
	private static double SouthPoleLat = -90;
	private static double SouthPoleLon = 5;
	
	@Before
	public void setUp() throws Exception {
		// Setup Browser:
		driver = Utils.createWebDriver(browserPath, driverPath);
		wait = new WebDriverWait(driver, 5);
		bfMain = new BfMainPage(driver);
		gxLogin = new GxLoginPage(driver);
		robot = new Robot();

		// Log in to GX:
		driver.get(gxUrl);
		gxLogin.loginToGeoAxis(username, password);
		driver.manage().window().maximize();
		
		// Verify Returned to BF:
		Utils.assertThatAfterWait("Should navigate back to BF", ExpectedConditions.urlMatches(baseUrl), wait);
	}
	
	@Test
	public void enter_coords() throws Exception {
		
		// Jump to Sri Lanka
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(SriLankaLat, SriLankaLon);
		Utils.assertBecomesInvisible("Sri Lanka search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertLatInRange("Sri Lanka Search", bfMain.getLatitude(), SriLankaLat, 5);
		Utils.assertLonInRange("Sri Lanka Search", bfMain.getLongitude(), SriLankaLon, 5);
		
		// Jump to North Pole
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(NorthPoleLat, NorthPoleLon);
		Utils.assertBecomesInvisible("North Pole search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertLatInRange("North Pole Search", bfMain.getLatitude(), NorthPoleLat, 5);
		Utils.assertLonInRange("North Pole Search", bfMain.getLongitude(), NorthPoleLon, 5);
		
		// Jump to South Pole
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(SouthPoleLat, SouthPoleLon);
		Utils.assertBecomesInvisible("South Pole search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertLatInRange("South Pole Search", bfMain.getLatitude(), SouthPoleLat, 5);
		Utils.assertLonInRange("South Pole Search", bfMain.getLongitude(), SouthPoleLon, 5);
		
		// Jump to +AntiMeridian
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(PosDateLineLat, PosDateLineLon);
		Utils.assertBecomesInvisible("+AntiMeridian search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertLatInRange("+AntiMeridian Search", bfMain.getLatitude(), PosDateLineLat, 5);
		Utils.assertLonInRange("+AntiMeridian Search", bfMain.getLongitude(), PosDateLineLon, 5);
		
		// Jump to -AntiMeridian
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(NegDateLineLat, NegDateLineLon);
		Utils.assertBecomesInvisible("-AntiMeridian search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertLatInRange("-AntiMeridian Search", bfMain.getLatitude(), NegDateLineLat, 5);
		Utils.assertLonInRange("-AntiMeridian Search", bfMain.getLongitude(), NegDateLineLon, 5);
		
	}
	
	@Test
	public void invalid_coords_entered() throws Exception {
		// Open Search Window:
		bfMain.searchButton.click();
		bfSearchWindow = bfMain.searchWindow();
		
		// Try garbage string:
		bfSearchWindow.searchCoordinates("garbage");
		Utils.assertBecomesVisible("Error message should appear for the string 'garbage'", bfSearchWindow.errorMessage, wait);
		
		// Try single coordinate:
		bfSearchWindow.searchCoordinates("50");
		Utils.assertBecomesVisible("Error message should appear for only one coordinate", bfSearchWindow.errorMessage, wait);

		// Try too big latitude:
		bfSearchWindow.searchCoordinates(95, 10);
		Utils.assertBecomesVisible("Error message should appear for lat = 95", bfSearchWindow.errorMessage, wait);

		// Try to big longitude:
		bfSearchWindow.searchCoordinates(10, 185);
		Utils.assertBecomesVisible("Error message should appear for lon = 185", bfSearchWindow.errorMessage, wait);
		
	}
	
	@Test @Ignore
	public void panning() throws Exception {
		// click down mouse
		// move mouse
		// release mouse
		// move 1 pixel
		// read coords (samish)
		// move to original space
		// read coords (different)
	}
	
	@Test
	public void validate_example_coords() throws Exception {
		// Open the coordinate window
		bfMain.searchButton.click();
		ArrayList<String> examplesList = bfMain.searchWindow().getExamples();
		assertTrue("There should be at least two examples", examplesList.size() >= 2);
		for (String example : examplesList) {
			bfMain.searchButton.click();
			bfMain.searchWindow().searchCoordinates(example);
			Utils.assertBecomesInvisible("The example coordinates " + example + "should work successfully", bfMain.searchWindow, wait);
		}
	}
	
	@Test
	public void click_nav_buttons() throws InterruptedException {
		
		//Maximized:
		
		assertTrue("Should be able to click home button", Utils.tryToClick(bfMain.homeButton));
		assertTrue("Should be able to click jobs button", Utils.tryToClick(bfMain.jobsButton));
		assertTrue("Should be able to click create job button", Utils.tryToClick(bfMain.createJobButton));
		assertTrue("Should be able to click product lines button", Utils.tryToClick(bfMain.productLinesButton));
		assertTrue("Should be able to click create product line button", Utils.tryToClick(bfMain.createProductLineButton));
		assertTrue("Should be able to click help button", Utils.tryToClick(bfMain.helpButton));
		
		// Small window:
		driver.manage().window().setSize(new Dimension(300, 300));
		
		assertTrue("Should be able to click home button in a smaller window", Utils.tryToClick(bfMain.homeButton));
		assertTrue("Should be able to click jobs button in a smaller window", Utils.tryToClick(bfMain.jobsButton));
		assertTrue("Should be able to click create job button in a smaller window", Utils.tryToClick(bfMain.createJobButton));
		assertTrue("Should be able to click product lines button in a smaller window", Utils.tryToClick(bfMain.productLinesButton));
		assertTrue("Should be able to click create product line button in a smaller window", Utils.tryToClick(bfMain.createProductLineButton));
		assertTrue("Should be able to click help button in a smaller window", Utils.tryToClick(bfMain.helpButton));
		
	}
	
	@After 
	public void tearDown() throws Exception {
		driver.quit();
	}
}