package bfui.test;

import static org.junit.Assert.*;

import java.awt.Robot;
import java.io.File;

import static java.awt.event.InputEvent.BUTTON1_DOWN_MASK;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.*;

import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestBrowseMap {
	private WebDriver driver;
	private Actions actions;
	private Robot robot;
	private WebDriverWait wait;
	private GxLoginPage gxLogin;
	private BfMainPage bfMain;
	private BfSearchWindowPage bfSearchWindow;
	private Beachfront beachfront;
	
	
	
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

	private double lat;
	private double lon;
	private double OrigLat;
	private double OrigLon;
	
	// Elements used:
	
	private WebElement searchButton;
	private WebElement coordWindow;
	private WebElement coordEntry;
	private WebElement coordText;
	private WebElement invalidEntryText;
	private WebElement coordSubmitButton;
	private WebElement examplesText;
	private List<WebElement> examplesList;
	
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
	}
	
	@Test
	public void enter_coords() throws Exception {
		
		// Jump to Sri Lanka
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(Double.toString(SriLankaLat) + ", " + Double.toString(SriLankaLon));
		Utils.assertBecomesInvisible("Sri Lanka search should be successful", bfMain.searchWindow, wait);
		Utils.jostleMouse(actions, bfMain.canvas);
		Utils.assertLatInRange("Sri Lanka Search", bfMain.getLatitude(), SriLankaLat, 5);
		Utils.assertLonInRange("Sri Lanka Search", bfMain.getLongitude(), SriLankaLon, 5);
		
		// Jump to +AntiMeridian
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(Double.toString(PosDateLineLat) + ", " + Double.toString(PosDateLineLon));
		Utils.assertBecomesInvisible("+AntiMeridian search should be successful", bfMain.searchWindow, wait);
		Utils.jostleMouse(actions, bfMain.canvas);
		Utils.assertLatInRange("+AntiMeridian Search", bfMain.getLatitude(), PosDateLineLat, 5);
		Utils.assertLonInRange("+AntiMeridian Search", bfMain.getLongitude(), PosDateLineLon, 5);
		
		// Jump to -AntiMeridian
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(Double.toString(NegDateLineLat) + ", " + Double.toString(NegDateLineLon));
		Utils.assertBecomesInvisible("-AntiMeridian search should be successful", bfMain.searchWindow, wait);
		Utils.jostleMouse(actions, bfMain.canvas);
		Utils.assertLatInRange("-AntiMeridian Search", bfMain.getLatitude(), NegDateLineLat, 5);
		Utils.assertLonInRange("-AntiMeridian Search", bfMain.getLongitude(), NegDateLineLon, 5);
		
		// Jump to North Pole
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(Double.toString(NorthPoleLat) + ", " + Double.toString(NorthPoleLon));
		Utils.assertBecomesInvisible("North Pole search should be successful", bfMain.searchWindow, wait);
		Utils.jostleMouse(actions, bfMain.canvas);
		Utils.assertLatInRange("North Pole Search", bfMain.getLatitude(), NorthPoleLat, 5);
		Utils.assertLonInRange("North Pole Search", bfMain.getLongitude(), NorthPoleLon, 5);
		
		// Jump to South Pole
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(Double.toString(SouthPoleLat) + ", " + Double.toString(SouthPoleLon));
		Utils.assertBecomesInvisible("South Pole search should be successful", bfMain.searchWindow, wait);
		Utils.jostleMouse(actions, bfMain.canvas);
		Utils.assertLatInRange("South Pole Search", bfMain.getLatitude(), SouthPoleLat, 5);
		Utils.assertLonInRange("South Pole Search", bfMain.getLongitude(), SouthPoleLon, 5);
		
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
		bfSearchWindow.searchCoordinates("95, 10");
		Utils.assertBecomesVisible("Error message should appear for lat = 95", bfSearchWindow.errorMessage, wait);

		// Try to big longitude:
		bfSearchWindow.searchCoordinates("10, 185");
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
	
	@After 
	public void tearDown() throws Exception {
		driver.quit();
	}
}