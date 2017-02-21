package bfui.test;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;

import java.util.ArrayList;

import org.junit.*;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import bfui.test.Importance.Level;


public class TestBrowseMap {
	private WebDriver driver;
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
	private static Point2D.Double NegDateLinePoint 	= 	new Point2D.Double(-180, -9.963129);
	private static Point2D.Double NorthPolePoint 	= 	new Point2D.Double(5, 90);
	private static Point2D.Double SouthPolePoint 	= 	new Point2D.Double(5, -90);

	private static String SriLankaUTM		=	"44N 389665,829148";
	private static String PosDateLineUTM	=	"60T 755632,4445899";
	private static String NegDateLineUTM	=	"1L 171067,8893499";
	private static String NorthPoleUTM		=	"Z 2000000,2000000";
	private static String SouthPoleUTM		=	"B 2000000,2000000";

	private static String SriLankaMGRS		=	"44N LP 8966529148";
	private static String PosDateLineMGRS	=	"60T YK 5563245899";
	private static String NegDateLineMGRS	=	"1L AJ 7103497173";
	private static String NorthPoleMGRS		=	"Z AH 0000000000";
	private static String SouthPoleMGRS		=	"B AN 0000000000";
	
	@Rule
	public ImportanceReporter reporter = new ImportanceReporter();
	
	@Before
	public void setUp() throws Exception {
		// Setup Browser:
		driver = Utils.createWebDriver(browserPath, driverPath);
		wait = new WebDriverWait(driver, 5);
		gxLogin = new GxLoginPage(driver);
		bfMain = new BfMainPage(driver);

		// Log in to GX:
		driver.get(gxUrl);
		gxLogin.loginToGeoAxis(username, password);
		bfMain.loginSongAndDance(baseUrl);
		driver.manage().window().maximize();
		
		// Verify Returned to BF:
		Utils.assertThatAfterWait("Should navigate back to BF", ExpectedConditions.urlMatches(baseUrl), wait);
	}
	
	@After 
	public void tearDown() throws Exception {
		driver.quit();
	}
	
	@Test @Importance(level = Level.HIGH)
	public void enter_coords() throws Exception {
		// Check that the "Jump To" window works with coordinates in lat, lon notation.
		
		// Jump to Sri Lanka
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(SriLankaPoint);
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
	
	@Test @Importance(level = Level.MEDIUM)
	public void enter_decimal_coords() throws InterruptedException {
		// These antimeridian jumps are converted to have digits after the decimal point.
		
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
	
	@Test @Importance(level = Level.MEDIUM)
	public void enter_DMS_Coords() throws Exception {
		// Check that the "Jump To" window works with coordinates in DMS notation.
		
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
	
	@Test @Importance(level = Level.MEDIUM)
	public void enter_UTM() throws InterruptedException {
		// Check that the "Jump To" window works with coordinates in UTM notation.
		
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
	
	@Test @Importance(level = Level.MEDIUM)
	public void enter_MGRS() throws InterruptedException {
		Utils.ignoreOnInt();
		// Check that the "Jump To" window works with coordinates in MGRS notation.
		
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
	
	@Test @Importance(level = Level.LOW)
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
	
	@Test @Importance(level = Level.MEDIUM)
	public void panning() throws InterruptedException {
		// Need to open "Jump To" search before the coordinate property is available.
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(SriLankaPoint);
		Thread.sleep(1000);
		
		// Pan North:
		Point2D.Double loc1 = bfMain.getCoords();
		bfMain.pan(0, 25);
		Point2D.Double loc2 = bfMain.getCoords();
		assertTrue("Should pan north", loc2.y > loc1.y);
		
		// Pan East:
		bfMain.pan(45, 0);
		Point2D.Double loc3 = bfMain.getCoords();
		assertTrue("Should pan east", loc3.x < loc2.x);
		
		// Pan South-West
		bfMain.pan(-30, -30);
		Point2D.Double loc4 = bfMain.getCoords();
		assertTrue("Should pan southwest", loc4.x > loc3.x && loc4.y < loc3.y);
		
	}
	
	@Test @Importance(level = Level.HIGH)
	public void validate_example_coords() throws Exception {
		// Open the search window to get a list of coordinate examples.
		bfMain.searchButton.click();
		ArrayList<String> examplesList = bfMain.searchWindow().getExamples();
		assertTrue("There should be at least two examples", examplesList.size() >= 2);
		
		// For each example, make sure that the search was successful.
		for (String example : examplesList) {
			bfMain.searchButton.click();
			bfMain.searchWindow().searchCoordinates(example);
			Utils.assertBecomesInvisible("The example coordinates " + example + "should work successfully", bfMain.searchWindow, wait);
			// Currently not checking coordinates.
		}
	}
	
	@Test @Importance(level = Level.HIGH)
	public void click_nav_buttons() throws InterruptedException {
		// Make sure that each button on the side can be clicked.
		
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
	
	@Test @Importance(level = Level.LOW)
	public void navbar_between_banners_in_500X500() throws InterruptedException {
		Utils.ignoreOnInt();
		// Make sure that the nav bar scales down to fit between the banners in a small window.
		driver.manage().window().setSize(new Dimension(500, 500));
		assertTrue("Home button should be between banners (Bug #11488)", bfMain.isBetweenBanners(bfMain.homeButton));
		assertTrue("Jobs button should be between banners (Bug #11488)", bfMain.isBetweenBanners(bfMain.jobsButton));
		assertTrue("Create Job button should be between banners (Bug #11488)", bfMain.isBetweenBanners(bfMain.createJobButton));
		assertTrue("Product Lines button should be between banner (Bug #11488)s", bfMain.isBetweenBanners(bfMain.productLinesButton));
		assertTrue("Create Product Line button should be between banners (Bug #11488)", bfMain.isBetweenBanners(bfMain.createProductLineButton));
		assertTrue("Help button should be between banners (Bug #11488)", bfMain.isBetweenBanners(bfMain.helpButton));
	}
}