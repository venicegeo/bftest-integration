package bfui.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import bfui.test.page.BfMainPage;
import bfui.test.page.BfSearchWindowPage;
import bfui.test.page.CoastlineLoginPage;
import bfui.test.page.GxLoginPage;
import bfui.test.page.LoginPage;
import bfui.test.util.Info;
import bfui.test.util.Info.Importance;
import bfui.test.util.Reporter;
//import bfui.test.util.SauceResultReporter;
import bfui.test.util.Utils;


public class TestBrowseMap {
	private WebDriver driver;
	private WebDriverWait wait;
	private Actions actions;
	private GxLoginPage gxLogin;
	private CoastlineLoginPage clLogin;
	private LoginPage login;
	private BfMainPage bfMain;
	private BfSearchWindowPage bfSearchWindow;
	
	
	
	// Strings used:
	private String baseUrl = System.getenv("bf_url");
	private String gxUrl = System.getenv("GX_url");
	private String username = System.getenv("bf_username");
	private String password = System.getenv("bf_password");
	private String space = System.getenv("space");
	private String browser = System.getenv("browser");
	
	
														//	lon, lat or x, y
	private static Point2D.Double SriLankaPoint 	= 	new Point2D.Double(80, 7.5);
	private static Point2D.Double PosDateLinePoint 	= 	new Point2D.Double(180, 40.124578);
	private static Point2D.Double NegDateLinePoint 	= 	new Point2D.Double(-180, -9.963129);
	private static Point2D.Double NorthPolePoint 	= 	new Point2D.Double(5, 85);
	private static Point2D.Double SouthPolePoint 	= 	new Point2D.Double(-5, -85);

	private static String SriLankaUTM		=	"44N 389665,829148";
	private static String PosDateLineUTM	=	"60N 755632,4445899";
	private static String NegDateLineUTM	=	"1S 171035,8897173";
	
	private static String NorthPoleUPS		=	"Z 2000000,2000000";
	private static String SouthPoleUPS		=	"B 2000000,2000000";

	private static String SriLankaMGRS		=	"44N LP 8966529148";
	private static String PosDateLineMGRS	=	"60T YK 5563245899";
	private static String NegDateLineMGRS	=	"1L AJ 7103597173";

	@Rule
	public Reporter reporter = new Reporter("http://dashboard.venicegeo.io/cgi-bin/bf_ui_" + browser + "/" + space + "/load.pl");
	@Rule
	public TestName name = new TestName();
	//@Rule
	//public SauceResultReporter sauce = new SauceResultReporter();
	
	@Before
	public void setUp() throws Exception {
		// Setup Browser:
		driver = Utils.getChromeRemoteDriver();
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
	}
	
	@After 
	public void tearDown() throws Exception {
		driver.quit();
	}
	
	@Test @Info(importance = Importance.HIGH)
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
	
	@Test @Info(importance = Importance.MEDIUM)
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
	
	@Test @Info(importance = Importance.MEDIUM)
	public void enter_DMS_Coords() throws Exception {
		// Check that the "Jump To" window works with coordinates in DMS notation.
		
		// Jump to Sri Lanka
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(Utils.pointToDMS(SriLankaPoint));
		Utils.assertBecomesInvisible("Sri Lanka search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		bfMain.pan(1, 1);
		Utils.assertPointInRange("Sri Lanka Search", bfMain.getCoords(), SriLankaPoint, 5);
		
		// Jump to North Pole
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(Utils.pointToDMS(NorthPolePoint));
		Utils.assertBecomesInvisible("North Pole search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		bfMain.pan(1, 1);
		Utils.assertPointInRange("North Pole Search", bfMain.getCoords(), NorthPolePoint, 5);
		
		// Jump to South Pole
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(Utils.pointToDMS(SouthPolePoint));
		Utils.assertBecomesInvisible("South Pole search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		bfMain.pan(1, 1);
		Utils.assertPointInRange("South Pole Search", bfMain.getCoords(), SouthPolePoint, 5);
		
		// Jump to +AntiMeridian
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(Utils.pointToDMS(PosDateLinePoint));
		Utils.assertBecomesInvisible("+AntiMeridian search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		bfMain.pan(1, 1);
		Utils.assertPointInRange("+AntiMeridian Search", bfMain.getCoords(), PosDateLinePoint, 5);
		
		// Jump to -AntiMeridian
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(Utils.pointToDMS(NegDateLinePoint));
		Utils.assertBecomesInvisible("-AntiMeridian search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		bfMain.pan(1, 1);
		Utils.assertPointInRange("-AntiMeridian Search", bfMain.getCoords(), NegDateLinePoint, 5);
		
	}
	
	@Test @Info(importance = Importance.MEDIUM) @Ignore
	public void enter_UPS() throws InterruptedException {
		
		// Jump to North Pole
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(NorthPoleUPS);
		Utils.assertBecomesInvisible("North Pole search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("North Pole Search", bfMain.getCoords(), NorthPolePoint, 5);
		
		// Jump to South Pole
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(SouthPoleUPS);
		Utils.assertBecomesInvisible("South Pole search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("South Pole Search", bfMain.getCoords(), SouthPolePoint, 5);
		
	}
	
	@Test @Info(importance = Importance.MEDIUM, bugs = {"6433"})
	public void enter_UTM() throws InterruptedException {
		// Check that the "Jump To" window works with coordinates in UTM notation.
		
		// Jump to Sri Lanka
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(SriLankaUTM);
		Utils.assertBecomesInvisible("Sri Lanka search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("Sri Lanka Search", bfMain.getCoords(), SriLankaPoint, 5);
		
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
	
	@Test @Info(importance = Importance.MEDIUM, bugs = {"6433"})
	public void enter_MGRS() throws InterruptedException {
		// Check that the "Jump To" window works with coordinates in MGRS notation.
		
		// Jump to Sri Lanka
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(SriLankaMGRS);
		Utils.assertBecomesInvisible("Sri Lanka search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("Sri Lanka Search", bfMain.getCoords(), SriLankaPoint, 5);
		
		// Jump to +AntiMeridian
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(PosDateLineMGRS);
		Utils.assertBecomesInvisible("+AntiMeridian search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("+AntiMeridian Search", bfMain.getCoords(), PosDateLinePoint, 5);
		
		// Jump to -AntiMeridian
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(NegDateLineMGRS);
		Utils.assertBecomesInvisible("-AntiMeridian search should be successful", bfMain.searchWindow, wait);
		Thread.sleep(1000);
		Utils.assertPointInRange("-AntiMeridian Search", bfMain.getCoords(), NegDateLinePoint, 5);	
	}
	
	@Test @Info(importance = Importance.LOW)
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
	
	@Test @Info(importance = Importance.MEDIUM)
	public void panning() throws InterruptedException {
		// Need to open "Jump To" search before the coordinate property is available.
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(SriLankaPoint);
		Thread.sleep(1000);
		
		// Pan North:
		Point2D.Double loc1 = bfMain.getCoords();
		bfMain.pan(5, 45);
		Point2D.Double loc2 = bfMain.getCoords();
		assertTrue("Should pan north", loc2.y > loc1.y);
		
		// Pan West:
		bfMain.pan(45, 0);
		Point2D.Double loc3 = bfMain.getCoords();
		assertTrue("Should pan west", loc3.x < loc2.x);
		
		// Pan South-East
		bfMain.pan(-30, -30);
		Point2D.Double loc4 = bfMain.getCoords();
		assertTrue("Should pan southeast", loc4.x > loc3.x && loc4.y < loc3.y);
		
	}
	
	@Test @Info(importance = Importance.LOW, bugs = {"18550"})
	public void zoom_buttons() throws InterruptedException {
		double origZoom;
		double newZoom;
		Utils.scrollInToView(driver, bfMain.zoomSlider);
		actions.moveToElement(bfMain.zoomSlider).click().build().perform();  // Get value in the middle
		Thread.sleep(1000);
		origZoom = bfMain.zoomSliderValue();
		bfMain.zoomInButton.click();
		Thread.sleep(1000);
		newZoom = bfMain.zoomSliderValue();
		Assert.assertTrue("Zoom-in should increase zoom level", newZoom > origZoom);
		bfMain.zoomOutButton.click();
		Thread.sleep(1000);
		newZoom = bfMain.zoomSliderValue();
		Assert.assertTrue("Zoom-out should return to original zoom level", newZoom == origZoom);
		bfMain.zoomOutButton.click();
		Thread.sleep(1000);
		newZoom = bfMain.zoomSliderValue();
		Assert.assertTrue("Zoom-out should decrease zoom level", newZoom < origZoom);
	}
	
	@Test @Info(importance = Importance.LOW)
	public void zoom_slider() throws InterruptedException {
		double origZoom;
		double newZoom;
		Utils.scrollInToView(driver, bfMain.zoomSlider);
		actions.moveToElement(bfMain.zoomSlider).click().build().perform();  // Get value in the middle
		Thread.sleep(1000);
		origZoom = bfMain.zoomSliderValue();
		actions.clickAndHold(bfMain.zoomSliderButton).moveByOffset(0, -5).release().build().perform();
		Thread.sleep(1000);
		newZoom = bfMain.zoomSliderValue();
		bfMain.pan(1, 1);
		Assert.assertTrue("Sliding up should increase zoom level", newZoom > origZoom);
		actions.clickAndHold(bfMain.zoomSliderButton).moveByOffset(0, 10).release().build().perform();
		Thread.sleep(1000);
		newZoom = bfMain.zoomSliderValue();
		bfMain.pan(1, 1);
		Assert.assertTrue("Sliding down should decrease zoom level", newZoom < origZoom);
	}
	
	@Test @Info(importance = Importance.HIGH)
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
	
	@Test @Info(importance = Importance.HIGH)
	public void click_nav_buttons() throws InterruptedException {
		// Make sure that each button on the side can be clicked.
		
		assertTrue("Should be able to click home button", Utils.tryToClick(bfMain.homeButton));
		Thread.sleep(1000);
		assertTrue("Should be able to click jobs button", Utils.tryToClick(bfMain.jobsButton));
		Thread.sleep(1000);
		assertTrue("Should be able to click create job button", Utils.tryToClick(bfMain.createJobButton));
		Thread.sleep(1000);
//		assertTrue("Should be able to click product lines button", Utils.tryToClick(bfMain.productLinesButton));
//		Thread.sleep(1000);
//		assertTrue("Should be able to click create product line button", Utils.tryToClick(bfMain.createProductLineButton));
//		Thread.sleep(1000);
//		Utils.assertBecomesVisible("Mouse position coordinates should be visible (Bug #11287)", bfMain.mouseoverCoordinates, wait);
//		assertTrue("Should be able to click help button", Utils.tryToClick(bfMain.helpButton));
	}
	
	@Test @Info(importance = Importance.MEDIUM)
	public void banner_positions() {
		assertEquals("There should be 2 banners", 2, bfMain.banners.size());
		assertEquals("Top banner should be the top of the window", 0, bfMain.topBannerPos());
		assertEquals("Bottom banner should be the bottom of the window", Utils.getWindowInnerHeight(driver), bfMain.bottomBannerPos());
	}
	
	
	@Test @Info(importance = Importance.LOW, bugs = {"11488"}) @Ignore
	public void navbar_between_banners_in_500X500() throws InterruptedException {
		// Make sure that the nav bar scales down to fit between the banners in a small window.
		driver.manage().window().setSize(new Dimension(500, 500));
		assertTrue("Home button should be between banners", bfMain.isBetweenBanners(bfMain.homeButton));
		assertTrue("Jobs button should be between banners", bfMain.isBetweenBanners(bfMain.jobsButton));
		assertTrue("Create Job button should be between banners", bfMain.isBetweenBanners(bfMain.createJobButton));
		assertTrue("Product Lines button should be between banner", bfMain.isBetweenBanners(bfMain.productLinesButton));
		assertTrue("Create Product Line button should be between banners", bfMain.isBetweenBanners(bfMain.createProductLineButton));
		assertTrue("Help button should be between banners", bfMain.isBetweenBanners(bfMain.helpButton));
	}
	
	@Test @Info(importance = Importance.LOW)
	public void measure_tool() throws InterruptedException {
		bfMain.measureButton.click();
		bfMain.measureWindow().selectUnits("meters");
		bfMain.drawBoundingBox(actions, 250, 250, 275, 275);
		double measurement = bfMain.measureWindow().getMeasurement();
		System.out.println(measurement);
		assertTrue("Measured distance should be within expected range (actual = " + measurement + ")", measurement > 750000 && measurement < 850000);
		
		bfMain.measureWindow().selectUnits("kilometers");
		double kms = bfMain.measureWindow().getMeasurement();
		assertTrue("Converting to km should divide displayed value by 1000", measurement > kms*999 && measurement < kms*1001);		
	}
	
	@Test @Info(importance = Importance.LOW)
	public void open_close_measure_tool() {
		bfMain.measureButton.click();
		bfMain.measureWindow().closeButton.click();
		Utils.assertBecomesInvisible("The measure window should be removed", bfMain.measureWindow, wait);
	}
}