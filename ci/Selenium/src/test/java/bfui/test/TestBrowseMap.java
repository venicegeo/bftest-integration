package bfui.test;

import static org.junit.Assert.*;

import java.awt.Robot;
import java.awt.geom.Point2D;
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
	
	
														//	lon, lat or x, y
	private static Point2D.Double SriLankaPoint 	= 	new Point2D.Double(80, 7.5);
	private static Point2D.Double PosDateLinePoint 	= 	new Point2D.Double(180, 40.124578);
	private static Point2D.Double NegDateLinePoint 	= 	new Point2D.Double(-180, -3.963122);
	private static Point2D.Double NorthPolePoint 	= 	new Point2D.Double(5, 90);
	private static Point2D.Double SouthPolePoint 	= 	new Point2D.Double(5, -90);

	private static String SriLankaUTM		=	"44N 389665,829148";
	private static String PosDateLineUTM	=	"60N 755632,4445899";
	private static String NegDateLineUTM	=	"1S 166817,9561345";
	private static String NorthPoleUTM		=	"31N 500000,9997964";
	private static String SouthPoleUTM		=	"31S 500000,2036";

	private static String SriLankaMGRS		=	"44N LP 8966529148";
	private static String PosDateLineMGRS	=	"1T BE 4436645899";
	private static String NegDateLineMGRS	=	"1L AJ 7103497173";
	private static String NorthPoleMGRS		=	"Z AH 0000000000";
	private static String SouthPoleMGRS		=	"B AN 0000000000";
	
	@Before
	public void setUp() throws Exception {
		System.out.println("Starting setUp - BrowseMap");
		// Setup Browser:
		driver = Utils.createWebDriver(browserPath, driverPath);
		wait = new WebDriverWait(driver, 5);
		bfMain = new BfMainPage(driver);
		gxLogin = new GxLoginPage(driver);
		robot = new Robot();
		System.out.println("driver created and variables initialized");

		// Log in to GX:
		driver.get(gxUrl);
		System.out.println("navigated to GX");
		gxLogin.loginToGeoAxis(username, password);
		System.out.println("logged in to GX");
		driver.manage().window().maximize();
		System.out.println("maximized window");
		
		// Verify Returned to BF:
		Utils.assertThatAfterWait("Should navigate back to BF", ExpectedConditions.urlMatches(baseUrl), wait);
		System.out.println("SetUp complete");
	}
	
	@Test
	public void enter_coords() throws Exception {
		
		// Jump to Sri Lanka
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(SriLankaPoint);
		Utils.pointToDMS(SriLankaPoint);
		Utils.assertBecomesInvisible("Sri Lanka search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("Sri Lanka Search", bfMain.getCoords(), SriLankaPoint, 5);
		
		// Jump to North Pole
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(NorthPolePoint);
		Utils.assertBecomesInvisible("North Pole search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("North Pole Search", bfMain.getCoords(), NorthPolePoint, 5);
		
		// Jump to South Pole
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(SouthPolePoint);
		Utils.assertBecomesInvisible("South Pole search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("South Pole Search", bfMain.getCoords(), SouthPolePoint, 5);
	}
	
	@Test @Ignore
	public void enter_decimal_coords() throws InterruptedException {
		// Jump to +AntiMeridian
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(PosDateLinePoint);
		Utils.assertBecomesInvisible("+AntiMeridian search should be successful (Bug #14510)", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("+AntiMeridian Search", bfMain.getCoords(), PosDateLinePoint, 5);
		
		// Jump to -AntiMeridian
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(NegDateLinePoint);
		Utils.assertBecomesInvisible("-AntiMeridian search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("-AntiMeridian Search", bfMain.getCoords(), NegDateLinePoint, 5);
	}
	
	@Test
	public void enter_DMS_Coords() throws Exception {
		
		// Jump to Sri Lanka
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(Utils.pointToDMS(SriLankaPoint));
		Utils.assertBecomesInvisible("Sri Lanka search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("Sri Lanka Search", bfMain.getCoords(), SriLankaPoint, 5);
		
		// Jump to North Pole
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(Utils.pointToDMS(NorthPolePoint));
		Utils.assertBecomesInvisible("North Pole search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("North Pole Search", bfMain.getCoords(), NorthPolePoint, 5);
		
		// Jump to South Pole
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(Utils.pointToDMS(SouthPolePoint));
		Utils.assertBecomesInvisible("South Pole search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("South Pole Search", bfMain.getCoords(), SouthPolePoint, 5);
		
		// Jump to +AntiMeridian
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(Utils.pointToDMS(PosDateLinePoint));
		Utils.assertBecomesInvisible("+AntiMeridian search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("+AntiMeridian Search", bfMain.getCoords(), PosDateLinePoint, 5);
		
		// Jump to -AntiMeridian
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(Utils.pointToDMS(NegDateLinePoint));
		Utils.assertBecomesInvisible("-AntiMeridian search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("-AntiMeridian Search", bfMain.getCoords(), NegDateLinePoint, 5);
		
	}
	
	@Test
	public void enter_UTM() throws InterruptedException {
		
		// Jump to Sri Lanka
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(SriLankaUTM);
		Utils.assertBecomesInvisible("Sri Lanka search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("Sri Lanka Search", bfMain.getCoords(), SriLankaPoint, 5);
		
		// Jump to North Pole
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(NorthPoleUTM);
		Utils.assertBecomesInvisible("North Pole search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("North Pole Search", bfMain.getCoords(), NorthPolePoint, 5);
		
		// Jump to South Pole
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(SouthPoleUTM);
		Utils.assertBecomesInvisible("South Pole search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("South Pole Search", bfMain.getCoords(), SouthPolePoint, 5);
		
		// Jump to +AntiMeridian
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(PosDateLineUTM);
		Utils.assertBecomesInvisible("+AntiMeridian search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("+AntiMeridian Search", bfMain.getCoords(), PosDateLinePoint, 5);
		
		// Jump to -AntiMeridian
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(NegDateLineUTM);
		Utils.assertBecomesInvisible("-AntiMeridian search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("-AntiMeridian Search", bfMain.getCoords(), NegDateLinePoint, 5);	
	}
	
	@Test @Ignore
	public void enter_MGRS() throws InterruptedException {
		
		// Jump to Sri Lanka
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(SriLankaMGRS);
		Utils.assertBecomesInvisible("Sri Lanka search should be successful (Bug #6433)", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("Sri Lanka Search", bfMain.getCoords(), SriLankaPoint, 5);
		
		// Jump to North Pole
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(NorthPoleMGRS);
		Utils.assertBecomesInvisible("North Pole search should be successful (Bug #6433)", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("North Pole Search", bfMain.getCoords(), NorthPolePoint, 5);
		
		// Jump to South Pole
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(SouthPoleMGRS);
		Utils.assertBecomesInvisible("South Pole search should be successful (Bug #6433)", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("South Pole Search", bfMain.getCoords(), SouthPolePoint, 5);
		
		// Jump to +AntiMeridian
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(PosDateLineMGRS);
		Utils.assertBecomesInvisible("+AntiMeridian search should be successful (Bug #6433)", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("+AntiMeridian Search", bfMain.getCoords(), PosDateLinePoint, 5);
		
		// Jump to -AntiMeridian
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(NegDateLineMGRS);
		Utils.assertBecomesInvisible("-AntiMeridian search should be successful (Bug #6433)", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("-AntiMeridian Search", bfMain.getCoords(), NegDateLinePoint, 5);	
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
		Thread.sleep(1000);
		assertTrue("Should be able to click jobs button", Utils.tryToClick(bfMain.jobsButton));
		Thread.sleep(1000);
		assertTrue("Should be able to click create job button", Utils.tryToClick(bfMain.createJobButton));
		Thread.sleep(1000);
		assertTrue("Should be able to click product lines button", Utils.tryToClick(bfMain.productLinesButton));
		Thread.sleep(1000);
		assertTrue("Should be able to click create product line button", Utils.tryToClick(bfMain.createProductLineButton));
		Thread.sleep(1000);
		assertTrue("Should be able to click help button", Utils.tryToClick(bfMain.helpButton));
	}
	
	@Test @Ignore
	public void navbar_between_banners_in_500X500() throws InterruptedException {
		driver.manage().window().setSize(new Dimension(500, 500));
		assertTrue("Home button should be between banners (Bug #11488)", bfMain.isBetweenBanners(bfMain.homeButton));
		assertTrue("Jobs button should be between banners (Bug #11488)", bfMain.isBetweenBanners(bfMain.jobsButton));
		assertTrue("Create Job button should be between banners (Bug #11488)", bfMain.isBetweenBanners(bfMain.createJobButton));
		assertTrue("Product Lines button should be between banner (Bug #11488)s", bfMain.isBetweenBanners(bfMain.productLinesButton));
		assertTrue("Create Product Line button should be between banners (Bug #11488)", bfMain.isBetweenBanners(bfMain.createProductLineButton));
		assertTrue("Help button should be between banners (Bug #11488)", bfMain.isBetweenBanners(bfMain.helpButton));
	}
	
	@After 
	public void tearDown() throws Exception {
		System.out.println("Starting tearDown");
		driver.quit();
		System.out.println("TearDown complete");
	}
}