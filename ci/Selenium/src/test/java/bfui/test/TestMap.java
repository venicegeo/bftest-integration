package bfui.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.WebDriver;

import bfui.test.page.CoordinateSearchPage;
import bfui.test.page.GxLoginPage;
import bfui.test.page.MainPage;
import bfui.test.page.MeasureToolPage;
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
		searchPage.search(SriLankaPoint);
		Utils.assertPointInRange("Sri Lanka Search", mainPage.getMapCenter(), SriLankaPoint, 5);

		// Jump to North Pole
		searchPage = mainPage.openCoordinateSearchDialog();
		searchPage.search(NorthPolePoint);
		Utils.assertPointInRange("North Pole Search", mainPage.getMapCenter(), NorthPolePoint, 5);

		// Jump to South Pole
		searchPage = mainPage.openCoordinateSearchDialog();
		searchPage.search(SouthPolePoint);
		Utils.assertPointInRange("South Pole Search", mainPage.getMapCenter(), SouthPolePoint, 5);
	}

	@Test
	@Info(importance = Importance.MEDIUM)
	public void enter_decimal_coords() throws InterruptedException {
		// These antimeridian jumps are converted to have digits after the decimal point.

		// Jump to +AntiMeridian
		CoordinateSearchPage searchPage = mainPage.openCoordinateSearchDialog();
		searchPage.search(PosDateLinePoint);
		Utils.assertPointInRange("+AntiMeridian Search", mainPage.getMapCenter(), PosDateLinePoint, 5);

		// Jump to -AntiMeridian
		searchPage = mainPage.openCoordinateSearchDialog();
		searchPage.search(NegDateLinePoint);
		Utils.assertPointInRange("-AntiMeridian Search", mainPage.getMapCenter(), NegDateLinePoint, 5);
	}

	@Test
	@Info(importance = Importance.MEDIUM)
	public void enter_DMS_Coords() throws Exception {
		// Check that the "Jump To" window works with coordinates in DMS notation.

		// Jump to Sri Lanka
		CoordinateSearchPage searchPage = mainPage.openCoordinateSearchDialog();
		searchPage.search(Utils.pointToDMS(SriLankaPoint));
		Utils.assertPointInRange("Sri Lanka Search", mainPage.getMapCenter(), SriLankaPoint, 5);

		// Jump to North Pole
		searchPage = mainPage.openCoordinateSearchDialog();
		searchPage.search(Utils.pointToDMS(NorthPolePoint));
		Utils.assertPointInRange("North Pole Search", mainPage.getMapCenter(), NorthPolePoint, 5);

		// Jump to South Pole
		searchPage = mainPage.openCoordinateSearchDialog();
		searchPage.search(Utils.pointToDMS(SouthPolePoint));
		Utils.assertPointInRange("South Pole Search", mainPage.getMapCenter(), SouthPolePoint, 5);

		// Jump to +AntiMeridian
		searchPage = mainPage.openCoordinateSearchDialog();
		searchPage.search(Utils.pointToDMS(PosDateLinePoint));
		Utils.assertPointInRange("+AntiMeridian Search", mainPage.getMapCenter(), PosDateLinePoint, 5);

		// Jump to -AntiMeridian
		searchPage = mainPage.openCoordinateSearchDialog();
		searchPage.search(Utils.pointToDMS(NegDateLinePoint));
		Utils.assertPointInRange("-AntiMeridian Search", mainPage.getMapCenter(), NegDateLinePoint, 5);
	}

	@Test
	@Info(importance = Importance.MEDIUM)
	public void enter_UTM() throws InterruptedException {
		// Check that the "Jump To" window works with coordinates in UTM notation.

		// Jump to Sri Lanka
		CoordinateSearchPage searchPage = mainPage.openCoordinateSearchDialog();
		searchPage.search(SriLankaUTM);
		Utils.assertPointInRange("Sri Lanka Search", mainPage.getMapCenter(), SriLankaPoint, 5);

		// Jump to +AntiMeridian
		searchPage = mainPage.openCoordinateSearchDialog();
		searchPage.search(PosDateLineUTM);
		Utils.assertPointInRange("+AntiMeridian Search", mainPage.getMapCenter(), PosDateLinePoint, 5);

		// Jump to -AntiMeridian
		searchPage = mainPage.openCoordinateSearchDialog();
		searchPage.search(NegDateLineUTM);
		Utils.assertPointInRange("-AntiMeridian Search", mainPage.getMapCenter(), NegDateLinePoint, 5);
	}

	@Test
	@Info(importance = Importance.MEDIUM)
	public void enter_MGRS() throws InterruptedException {
		// Check that the "Jump To" window works with coordinates in MGRS notation.

		// Jump to Sri Lanka
		CoordinateSearchPage searchPage = mainPage.openCoordinateSearchDialog();
		searchPage.search(SriLankaMGRS);
		Utils.assertPointInRange("Sri Lanka Search", mainPage.getMapCenter(), SriLankaPoint, 5);

		// Jump to +AntiMeridian
		searchPage = mainPage.openCoordinateSearchDialog();
		searchPage.search(PosDateLineMGRS);
		Utils.assertPointInRange("+AntiMeridian Search", mainPage.getMapCenter(), PosDateLinePoint, 5);

		// Jump to -AntiMeridian
		searchPage = mainPage.openCoordinateSearchDialog();
		searchPage.search(NegDateLineMGRS);
		Utils.assertPointInRange("-AntiMeridian Search", mainPage.getMapCenter(), NegDateLinePoint, 5);
	}

	@Test
	@Info(importance = Importance.LOW)
	public void invalid_coords_entered() throws Exception {
		// Open Search Window:
		CoordinateSearchPage searchPage = mainPage.openCoordinateSearchDialog();

		// Try garbage string:
		searchPage.search("garbage");
		assertTrue("Error message should appear for the string 'garbage'", searchPage.isErrorVisible());

		// Try single coordinate:
		searchPage.search("50");
		assertTrue("Error message should appear for only one coordinate", searchPage.isErrorVisible());

		// Try too big latitude:
		searchPage.search(95, 10);
		assertTrue("Error message should appear for lat = 95", searchPage.isErrorVisible());

		// Try to big longitude:
		searchPage.search(10, 185);
		assertTrue("Error message should appear for lon = 185", searchPage.isErrorVisible());
	}

	@Test
	@Info(importance = Importance.MEDIUM)
	public void panning() throws InterruptedException {
		// Pan North:
		Point2D.Double loc1 = mainPage.getMapCenter();
		mainPage.pan(5, 45);
		Point2D.Double loc2 = mainPage.getMapCenter();
		assertTrue("Should pan north", loc2.y > loc1.y);

		// Pan West:
		mainPage.pan(45, 0);
		Point2D.Double loc3 = mainPage.getMapCenter();
		assertTrue("Should pan west", loc3.x < loc2.x);

		// Pan South-East
		mainPage.pan(-30, -30);
		Point2D.Double loc4 = mainPage.getMapCenter();
		assertTrue("Should pan southeast", loc4.x > loc3.x && loc4.y < loc3.y);
	}

	@Test
	@Info(importance = Importance.LOW)
	public void zoom_buttons() throws InterruptedException {
		double originalZoom, newZoom;
		// Reset the zoom to the middle and get the initial value
		mainPage.setZoomSliderMiddle();
		originalZoom = mainPage.getMapScale();

		// Zoom In
		mainPage.clickZoomIn();
		newZoom = mainPage.getMapScale();
		assertTrue(String.format("Zoom-in should increase zoom level. %s < %s?", newZoom, originalZoom), newZoom < originalZoom);

		// Zoom Out
		mainPage.clickZoomOut();
		newZoom = mainPage.getMapScale();
		assertTrue(String.format("Zoom-out should return to original zoom level. %s = %s?", newZoom, originalZoom),
				newZoom == originalZoom);

		// Zoom out Again
		mainPage.clickZoomOut();
		newZoom = mainPage.getMapScale();
		assertTrue(String.format("Zoom-out should decrease zoom level. %s > %s?", newZoom, originalZoom), newZoom > originalZoom);
	}

	@Test
	@Info(importance = Importance.LOW)
	public void zoom_slider() throws InterruptedException {
		double originalZoom, newZoom;
		// Set zoom slider to the middle
		mainPage.setZoomSliderMiddle();
		originalZoom = mainPage.getMapScale();

		// Slide the zoom slider upwards to zoom in
		mainPage.dragZoomSliderUp();
		newZoom = mainPage.getMapScale();
		assertTrue("Sliding up should increase zoom level", newZoom < originalZoom);

		// Slide the zoom slider downwards to zoom out
		mainPage.dragZoomSliderDown();
		newZoom = mainPage.getMapScale();
		assertTrue("Sliding down should decrease zoom level", newZoom == originalZoom);
	}

	@Test
	@Info(importance = Importance.MEDIUM)
	public void banner_positions() {
		assertEquals("There should be 2 banners", 2, mainPage.getBannerCount());
		assertEquals("Top banner should be the top of the window", 0, mainPage.topBannerPos());
		assertEquals("Bottom banner should be the bottom of the window", Utils.getWindowInnerHeight(driver), mainPage.bottomBannerPos());
	}

	@Test
	@Info(importance = Importance.LOW)
	public void measure_tool() throws InterruptedException {
		// Activate the tool and set to meters
		MeasureToolPage measurePage = mainPage.activateMeasureTool();
		measurePage.selectUnits("meters");

		// Draw the line
		mainPage.drawBoundingBox(250, 250, 275, 275);

		// Get the measurement
		double metersMeasurement = measurePage.getMeasurement();
		assertTrue("Measured distance should be within expected range (actual = " + metersMeasurement + ")",
				metersMeasurement > 750000 && metersMeasurement < 850000);

		// Set to Kilometers and retest
		measurePage.selectUnits("kilometers");
		double kilometersMeasurement = measurePage.getMeasurement();
		assertTrue("Converting to km should divide displayed value by 1000",
				metersMeasurement > kilometersMeasurement * 999 && metersMeasurement < kilometersMeasurement * 1001);
	}

	@Test
	@Info(importance = Importance.LOW)
	public void open_close_measure_tool() {
		// Open and close the Measuring tool
		MeasureToolPage measurePage = mainPage.activateMeasureTool();
		measurePage.close();
		assertFalse("Measure tool closed after opening", mainPage.isMeasureToolActive());
	}
}