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

import bfui.test.page.CoordinateSearchPage;
import bfui.test.page.GxLoginPage;
import bfui.test.page.MainPage;
import bfui.test.util.Info;
import bfui.test.util.Info.Importance;
import bfui.test.util.Reporter;
import bfui.test.util.Utils;

/**
 * Tests Map interactions
 */
public class TestMap {
	private String BASE_URL = System.getenv("bf_url");
	private String USERNAME = System.getenv("bf_username");
	private String PASSWORD = System.getenv("bf_password");

	private static Point2D.Double SriLankaPoint = new Point2D.Double(80, 7.5);
	private static Point2D.Double PosDateLinePoint = new Point2D.Double(180, 40.124578);
	private static Point2D.Double NegDateLinePoint = new Point2D.Double(-180, -9.963129);
	private static Point2D.Double NorthPolePoint = new Point2D.Double(5, 85);
	private static Point2D.Double SouthPolePoint = new Point2D.Double(-5, -85);

	private static String SriLankaUTM = "44N 389665,829148";
	private static String PosDateLineUTM = "60N 755632,4445899";
	private static String NegDateLineUTM = "1S 171035,8897173";

	private static String NorthPoleUPS = "Z 2000000,2000000";
	private static String SouthPoleUPS = "B 2000000,2000000";

	private static String SriLankaMGRS = "44N LP 8966529148";
	private static String PosDateLineMGRS = "60T YK 5563245899";
	private static String NegDateLineMGRS = "1L AJ 7103597173";

	private WebDriver driver;
	private MainPage mainPage;

	@Rule
	public Reporter reporter = new Reporter();
	@Rule
	public TestName name = new TestName();

	@Before
	public void setUp() throws Exception {
		driver = Utils.getChromeRemoteDriver();
		mainPage = new MainPage(driver);
		driver.get(BASE_URL);
		// Perform Login
		GxLoginPage loginPage = mainPage.beginLogin();
		mainPage = loginPage.loginDisadvantaged(USERNAME, PASSWORD, mainPage);
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
	}

	@Test
	@Info(importance = Importance.HIGH)
	public void enter_coords() throws Exception {
		// Check that the Coordinate Search window works with coordinates in lat, lon notation.

		// Jump to Sri Lanka
		CoordinateSearchPage searchPage = mainPage.openCoordinateSearchDialog();
		searchPage.searchCoordinates(SriLankaPoint);
		Utils.assertPointInRange("Sri Lanka Search", mainPage.getMapCenter(), SriLankaPoint, 5);

		// Jump to North Pole
		searchPage = mainPage.openCoordinateSearchDialog();
		searchPage.searchCoordinates(NorthPolePoint);
		Utils.assertPointInRange("North Pole Search", mainPage.getMapCenter(), NorthPolePoint, 5);

		// Jump to South Pole
		searchPage = mainPage.openCoordinateSearchDialog();
		searchPage.searchCoordinates(SouthPolePoint);
		Utils.assertPointInRange("South Pole Search", mainPage.getMapCenter(), SouthPolePoint, 5);
	}
//
//	@Test
//	@Info(importance = Importance.MEDIUM)
//	public void enter_decimal_coords() throws InterruptedException {
//		// These antimeridian jumps are converted to have digits after the decimal point.
//
//		// Jump to +AntiMeridian
//		mainPage.searchButton.click();
//		mainPage.searchWindow().searchCoordinates(PosDateLinePoint);
//		Utils.assertBecomesInvisible("+AntiMeridian search should be successful (Bug #14510)", mainPage.searchWindow, wait);
//		Thread.sleep(1000);
//		mainPage.pan(1, 1);
//		Utils.assertPointInRange("+AntiMeridian Search", mainPage.getCoords(), PosDateLinePoint, 5);
//
//		// Jump to -AntiMeridian
//		mainPage.searchButton.click();
//		mainPage.searchWindow().searchCoordinates(NegDateLinePoint);
//		Utils.assertBecomesInvisible("-AntiMeridian search should be successful", mainPage.searchWindow, wait);
//		Thread.sleep(1000);
//		mainPage.pan(1, 1);
//		Utils.assertPointInRange("-AntiMeridian Search", mainPage.getCoords(), NegDateLinePoint, 5);
//	}
//
//	@Test
//	@Info(importance = Importance.MEDIUM)
//	public void enter_DMS_Coords() throws Exception {
//		// Check that the "Jump To" window works with coordinates in DMS notation.
//
//		// Jump to Sri Lanka
//		mainPage.searchButton.click();
//		mainPage.searchWindow().searchCoordinates(Utils.pointToDMS(SriLankaPoint));
//		Utils.assertBecomesInvisible("Sri Lanka search should be successful", mainPage.searchWindow, wait);
//		Thread.sleep(1000);
//		mainPage.pan(1, 1);
//		Utils.assertPointInRange("Sri Lanka Search", mainPage.getCoords(), SriLankaPoint, 5);
//
//		// Jump to North Pole
//		mainPage.searchButton.click();
//		mainPage.searchWindow().searchCoordinates(Utils.pointToDMS(NorthPolePoint));
//		Utils.assertBecomesInvisible("North Pole search should be successful", mainPage.searchWindow, wait);
//		Thread.sleep(1000);
//		mainPage.pan(1, 1);
//		Utils.assertPointInRange("North Pole Search", mainPage.getCoords(), NorthPolePoint, 5);
//
//		// Jump to South Pole
//		mainPage.searchButton.click();
//		mainPage.searchWindow().searchCoordinates(Utils.pointToDMS(SouthPolePoint));
//		Utils.assertBecomesInvisible("South Pole search should be successful", mainPage.searchWindow, wait);
//		Thread.sleep(1000);
//		mainPage.pan(1, 1);
//		Utils.assertPointInRange("South Pole Search", mainPage.getCoords(), SouthPolePoint, 5);
//
//		// Jump to +AntiMeridian
//		mainPage.searchButton.click();
//		mainPage.searchWindow().searchCoordinates(Utils.pointToDMS(PosDateLinePoint));
//		Utils.assertBecomesInvisible("+AntiMeridian search should be successful", mainPage.searchWindow, wait);
//		Thread.sleep(1000);
//		mainPage.pan(1, 1);
//		Utils.assertPointInRange("+AntiMeridian Search", mainPage.getCoords(), PosDateLinePoint, 5);
//
//		// Jump to -AntiMeridian
//		mainPage.searchButton.click();
//		mainPage.searchWindow().searchCoordinates(Utils.pointToDMS(NegDateLinePoint));
//		Utils.assertBecomesInvisible("-AntiMeridian search should be successful", mainPage.searchWindow, wait);
//		Thread.sleep(1000);
//		mainPage.pan(1, 1);
//		Utils.assertPointInRange("-AntiMeridian Search", mainPage.getCoords(), NegDateLinePoint, 5);
//
//	}
//
//	@Test
//	@Info(importance = Importance.MEDIUM)
//	@Ignore
//	public void enter_UPS() throws InterruptedException {
//
//		// Jump to North Pole
//		mainPage.searchButton.click();
//		mainPage.searchWindow().searchCoordinates(NorthPoleUPS);
//		Utils.assertBecomesInvisible("North Pole search should be successful", mainPage.searchWindow, wait);
//		Thread.sleep(1000);
//		Utils.assertPointInRange("North Pole Search", mainPage.getCoords(), NorthPolePoint, 5);
//
//		// Jump to South Pole
//		mainPage.searchButton.click();
//		mainPage.searchWindow().searchCoordinates(SouthPoleUPS);
//		Utils.assertBecomesInvisible("South Pole search should be successful", mainPage.searchWindow, wait);
//		Thread.sleep(1000);
//		Utils.assertPointInRange("South Pole Search", mainPage.getCoords(), SouthPolePoint, 5);
//
//	}
//
//	@Test
//	@Info(importance = Importance.MEDIUM)
//	public void enter_UTM() throws InterruptedException {
//		// Check that the "Jump To" window works with coordinates in UTM notation.
//
//		// Jump to Sri Lanka
//		mainPage.searchButton.click();
//		mainPage.searchWindow().searchCoordinates(SriLankaUTM);
//		Utils.assertBecomesInvisible("Sri Lanka search should be successful", mainPage.searchWindow, wait);
//		Thread.sleep(1000);
//		Utils.assertPointInRange("Sri Lanka Search", mainPage.getCoords(), SriLankaPoint, 5);
//
//		// Jump to +AntiMeridian
//		mainPage.searchButton.click();
//		mainPage.searchWindow().searchCoordinates(PosDateLineUTM);
//		Utils.assertBecomesInvisible("+AntiMeridian search should be successful", mainPage.searchWindow, wait);
//		Thread.sleep(1000);
//		Utils.assertPointInRange("+AntiMeridian Search", mainPage.getCoords(), PosDateLinePoint, 5);
//
//		// Jump to -AntiMeridian
//		mainPage.searchButton.click();
//		mainPage.searchWindow().searchCoordinates(NegDateLineUTM);
//		Utils.assertBecomesInvisible("-AntiMeridian search should be successful", mainPage.searchWindow, wait);
//		Thread.sleep(1000);
//		Utils.assertPointInRange("-AntiMeridian Search", mainPage.getCoords(), NegDateLinePoint, 5);
//	}
//
//	@Test
//	@Info(importance = Importance.MEDIUM)
//	public void enter_MGRS() throws InterruptedException {
//		// Check that the "Jump To" window works with coordinates in MGRS notation.
//
//		// Jump to Sri Lanka
//		mainPage.searchButton.click();
//		mainPage.searchWindow().searchCoordinates(SriLankaMGRS);
//		Utils.assertBecomesInvisible("Sri Lanka search should be successful", mainPage.searchWindow, wait);
//		Thread.sleep(1000);
//		Utils.assertPointInRange("Sri Lanka Search", mainPage.getCoords(), SriLankaPoint, 5);
//
//		// Jump to +AntiMeridian
//		mainPage.searchButton.click();
//		mainPage.searchWindow().searchCoordinates(PosDateLineMGRS);
//		Utils.assertBecomesInvisible("+AntiMeridian search should be successful", mainPage.searchWindow, wait);
//		Thread.sleep(1000);
//		Utils.assertPointInRange("+AntiMeridian Search", mainPage.getCoords(), PosDateLinePoint, 5);
//
//		// Jump to -AntiMeridian
//		mainPage.searchButton.click();
//		mainPage.searchWindow().searchCoordinates(NegDateLineMGRS);
//		Utils.assertBecomesInvisible("-AntiMeridian search should be successful", mainPage.searchWindow, wait);
//		Thread.sleep(1000);
//		Utils.assertPointInRange("-AntiMeridian Search", mainPage.getCoords(), NegDateLinePoint, 5);
//	}
//
//	@Test
//	@Info(importance = Importance.LOW)
//	public void invalid_coords_entered() throws Exception {
//		// Open Search Window:
//		mainPage.searchButton.click();
//		bfSearchWindow = mainPage.searchWindow();
//
//		// Try garbage string:
//		bfSearchWindow.searchCoordinates("garbage");
//		Utils.assertBecomesVisible("Error message should appear for the string 'garbage'", bfSearchWindow.errorMessage, wait);
//
//		// Try single coordinate:
//		bfSearchWindow.searchCoordinates("50");
//		Utils.assertBecomesVisible("Error message should appear for only one coordinate", bfSearchWindow.errorMessage, wait);
//
//		// Try too big latitude:
//		bfSearchWindow.searchCoordinates(95, 10);
//		Utils.assertBecomesVisible("Error message should appear for lat = 95", bfSearchWindow.errorMessage, wait);
//
//		// Try to big longitude:
//		bfSearchWindow.searchCoordinates(10, 185);
//		Utils.assertBecomesVisible("Error message should appear for lon = 185", bfSearchWindow.errorMessage, wait);
//
//	}
//
//	@Test
//	@Info(importance = Importance.MEDIUM)
//	public void panning() throws InterruptedException {
//		// Need to open "Jump To" search before the coordinate property is available.
//		mainPage.searchButton.click();
//		mainPage.searchWindow().searchCoordinates(SriLankaPoint);
//		Thread.sleep(1000);
//
//		// Pan North:
//		Point2D.Double loc1 = mainPage.getCoords();
//		mainPage.pan(5, 45);
//		Point2D.Double loc2 = mainPage.getCoords();
//		assertTrue("Should pan north", loc2.y > loc1.y);
//
//		// Pan West:
//		mainPage.pan(45, 0);
//		Point2D.Double loc3 = mainPage.getCoords();
//		assertTrue("Should pan west", loc3.x < loc2.x);
//
//		// Pan South-East
//		mainPage.pan(-30, -30);
//		Point2D.Double loc4 = mainPage.getCoords();
//		assertTrue("Should pan southeast", loc4.x > loc3.x && loc4.y < loc3.y);
//
//	}
//
//	@Test
//	@Info(importance = Importance.LOW)
//	public void zoom_buttons() throws InterruptedException {
//		double origZoom;
//		double newZoom;
//		Utils.scrollInToView(driver, mainPage.zoomSlider);
//		actions.moveToElement(mainPage.zoomSlider).click().build().perform(); // Get value in the middle
//		Thread.sleep(2000);
//		origZoom = mainPage.zoomSliderValue();
//		mainPage.zoomInButton.click();
//		Thread.sleep(2000);
//		newZoom = mainPage.zoomSliderValue();
//		Assert.assertTrue("Zoom-in should increase zoom level", newZoom > origZoom);
//		mainPage.zoomOutButton.click();
//		Thread.sleep(2000);
//		newZoom = mainPage.zoomSliderValue();
//		Assert.assertTrue("Zoom-out should return to original zoom level", newZoom == origZoom);
//		mainPage.zoomOutButton.click();
//		Thread.sleep(2000);
//		newZoom = mainPage.zoomSliderValue();
//		Assert.assertTrue("Zoom-out should decrease zoom level", newZoom < origZoom);
//	}
//
//	@Test
//	@Info(importance = Importance.LOW)
//	public void zoom_slider() throws InterruptedException {
//		double origZoom;
//		double newZoom;
//		Utils.scrollInToView(driver, mainPage.zoomSlider);
//		actions.moveToElement(mainPage.zoomSlider).click().build().perform(); // Get value in the middle
//		Thread.sleep(2000);
//		origZoom = mainPage.zoomSliderValue();
//		System.out.println(origZoom);
//		actions.clickAndHold(mainPage.zoomSliderButton).moveByOffset(0, -5).release().build().perform();
//		Thread.sleep(2000);
//		newZoom = mainPage.zoomSliderValue();
//		System.out.println(newZoom);
//		Assert.assertTrue("Sliding up should increase zoom level", newZoom > origZoom);
//		actions.clickAndHold(mainPage.zoomSliderButton).moveByOffset(0, 10).release().build().perform();
//		Thread.sleep(2000);
//		newZoom = mainPage.zoomSliderValue();
//		System.out.println(newZoom);
//		Assert.assertTrue("Sliding down should decrease zoom level", newZoom < origZoom);
//	}
//
//	@Test
//	@Info(importance = Importance.HIGH)
//	public void validate_example_coords() throws Exception {
//		// Open the search window to get a list of coordinate examples.
//		mainPage.searchButton.click();
//		ArrayList<String> examplesList = mainPage.searchWindow().getExamples();
//		assertTrue("There should be at least two examples", examplesList.size() >= 2);
//
//		// For each example, make sure that the search was successful.
//		for (String example : examplesList) {
//			mainPage.searchButton.click();
//			mainPage.searchWindow().searchCoordinates(example);
//			Utils.assertBecomesInvisible("The example coordinates " + example + "should work successfully", mainPage.searchWindow, wait);
//			// Currently not checking coordinates.
//		}
//	}
//
//	@Test
//	@Info(importance = Importance.HIGH)
//	public void click_nav_buttons() throws InterruptedException {
//		// Make sure that each button on the side can be clicked.
//		assertTrue("Should be able to click home button", Utils.tryToClick(mainPage.homeButton));
//		Thread.sleep(1000);
//		assertTrue("Should be able to click jobs button", Utils.tryToClick(mainPage.jobsButton));
//		Thread.sleep(1000);
//		assertTrue("Should be able to click create job button", Utils.tryToClick(mainPage.createJobButton));
//		Thread.sleep(1000);
//	}
//
//	@Test
//	@Info(importance = Importance.MEDIUM)
//	public void banner_positions() {
//		assertEquals("There should be 2 banners", 2, mainPage.banners.size());
//		assertEquals("Top banner should be the top of the window", 0, mainPage.topBannerPos());
//		assertEquals("Bottom banner should be the bottom of the window", Utils.getWindowInnerHeight(driver), mainPage.bottomBannerPos());
//	}
//
//	@Test
//	@Info(importance = Importance.LOW)
//	@Ignore
//	public void navbar_between_banners_in_500X500() throws InterruptedException {
//		// Make sure that the nav bar scales down to fit between the banners in a small window.
//		driver.manage().window().setSize(new Dimension(500, 500));
//		assertTrue("Home button should be between banners", mainPage.isBetweenBanners(mainPage.homeButton));
//		assertTrue("Jobs button should be between banners", mainPage.isBetweenBanners(mainPage.jobsButton));
//		assertTrue("Create Job button should be between banners", mainPage.isBetweenBanners(mainPage.createJobButton));
//		assertTrue("Product Lines button should be between banner", mainPage.isBetweenBanners(mainPage.productLinesButton));
//		assertTrue("Create Product Line button should be between banners", mainPage.isBetweenBanners(mainPage.createProductLineButton));
//		assertTrue("Help button should be between banners", mainPage.isBetweenBanners(mainPage.helpButton));
//	}
//
//	@Test
//	@Info(importance = Importance.LOW)
//	public void measure_tool() throws InterruptedException {
//		mainPage.measureButton.click();
//		mainPage.measureWindow().selectUnits("meters");
//		mainPage.drawBoundingBox(actions, 250, 250, 275, 275);
//		double measurement = mainPage.measureWindow().getMeasurement();
//		System.out.println(measurement);
//		assertTrue("Measured distance should be within expected range (actual = " + measurement + ")",
//				measurement > 750000 && measurement < 850000);
//
//		mainPage.measureWindow().selectUnits("kilometers");
//		double kms = mainPage.measureWindow().getMeasurement();
//		assertTrue("Converting to km should divide displayed value by 1000", measurement > kms * 999 && measurement < kms * 1001);
//	}
//
//	@Test
//	@Info(importance = Importance.LOW)
//	public void open_close_measure_tool() {
//		mainPage.measureButton.click();
//		mainPage.measureWindow().closeButton.click();
//		Utils.assertBecomesInvisible("The measure window should be removed", mainPage.measureWindow, wait);
//	}
}