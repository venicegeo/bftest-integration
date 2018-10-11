package bfui.test.page;

import java.awt.geom.Point2D;
import java.util.List;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import bfui.test.page.core.PageObject;
import bfui.test.util.Utils;

/**
 * The main Beachfront landing page wrapper, linking to the other panels and tools.
 */
public class MainPage extends PageObject {
	/* @formatter:off */
	@FindBy(className = "Login-button")						private WebElement geoAxisLink;
	@FindBy(className = "Login-warning")					private WebElement consentBanner;
	@FindBy(className = "PrimaryMap-logout")				private WebElement logoutButton;
	@FindBy(className = "Navigation-linkJobs")				private WebElement jobsButton;
	@FindBy(className = "Navigation-linkCreateJob")			private WebElement createJobButton;
	@FindBy(className = "PrimaryMap-search")				private WebElement searchButton;
	@FindBy(className = "PrimaryMap-measure")				private WebElement measureButton;
	@FindBy(className = "ol-zoom-in")						private WebElement zoomInButton;
	@FindBy(className = "ol-zoom-out")						private WebElement zoomOutButton;
	@FindBy(className = "ol-zoomslider-thumb")				private WebElement zoomSliderButton;
	@FindBy(className = "ol-zoomslider")					private WebElement zoomSlider;
	@FindBy(className = "ol-mouse-position")				private WebElement mouseoverCoordinates;
	@FindBy(className = "ol-viewport")						private WebElement viewport;
	@FindBy(className = "ol-unselectable")					private WebElement canvas;
	@FindBy(className = "measure-dialog")					private WebElement measureWindow;
	@FindBy(className = "ClassificationBanner-root")		private List<WebElement> banners;
	@FindBy(xpath = "//div[contains(@class,'PrimaryMap-scale')]/div/span")	
															private WebElement mapScale;
	/* @formatter:on */

	private Actions actions;

	public MainPage(WebDriver driver) {
		super(driver);
		actions = new Actions(driver);
	}

	/**
	 * Clicks the Login button at the bottom of the splash page
	 * 
	 * @return The Login Page, where credentials can be entered
	 */
	public GxLoginPage beginLogin() {
		geoAxisLink.click();
		return new GxLoginPage(driver);
	}

	/**
	 * Logs the user out of Beachfront
	 */
	public LogoutPage logout() {
		logoutButton.click();
		driver.switchTo().alert().accept();
		// OAuth redirect behavior is not constant. Sometimes a redirect occurs, sometimes it does not. Handle either
		// condition. If the redirect occurs, then the URL will change to the logout page. If it does not, then the
		// Login screen will become visible again in the client.
		WebDriverWait wait = new WebDriverWait(driver, 2);
		try {
			wait.until(ExpectedConditions.urlContains("logout"));
		} catch (TimeoutException timeout) {
			wait.until(ExpectedConditions.visibilityOf(geoAxisLink));
		}
		return new LogoutPage(driver);
	}

	/**
	 * Determines if the user is logged in currently or not
	 * 
	 * @return True if the Login overlay is displayed
	 */
	public boolean isLoggedOut() {
		// Click in the center of the screen to ensure the "expired session" is cleared
		actions.pause(1000).click(canvas).pause(250).build().perform();
		return geoAxisLink.isDisplayed();
	}

	/**
	 * Gets the API Key cookie, used for local authentication token storage.
	 * 
	 * @return The API Key cookie
	 */
	public Cookie getApiKeyCookie() {
		return driver.manage().getCookieNamed("api_key");
	}

	/**
	 * Gets the Text of the Consent Banner
	 * 
	 * @return Consent banner text
	 */
	public String getConsentBannerText() {
		return consentBanner.getText();
	}

	/**
	 * Gets the current URL
	 * 
	 * @return The current URL
	 */
	public String getCurrentURL() {
		return driver.getCurrentUrl();
	}

	/**
	 * Navigate to the Jobs panel
	 * 
	 * @return The Jobs page
	 */
	public JobsPage navigateJobsPage() {
		jobsButton.click();
		return new JobsPage(driver);
	}

	/**
	 * Clicks the Search button on the map that allows the user to navigate to coordinates
	 * 
	 * @return Image Search Page
	 */
	public CoordinateSearchPage openCoordinateSearchDialog() {
		searchButton.click();
		return new CoordinateSearchPage(driver);
	}

	/**
	 * Gets the Lat/Lon Coordinates of the map. The coordinates are read via the mouse position control in the
	 * lower-left portion of the map.
	 * <p>
	 * Before the center is pulled, we first pan the map slightly. This ensures that the primary map has its coordinates
	 * updated. This is a work-around because sometimes the internal map coordinates do not update when a pan complete
	 * until the cursor is moved.
	 * 
	 * @return The coordinates of the map
	 */
	public Point2D.Double getMapCenter() {
		// Move cursor to center, pan slightly to ensure coordinates are displayed
		new Actions(driver).moveToElement(viewport);
		pan(1, 1);
		// Convert displayed HDMS coordinates to decimal degrees
		return Utils.HdmsToPoint(mouseoverCoordinates.getText());
	}

	/**
	 * Pans the map
	 * 
	 * @param x
	 *            The X distance to pan
	 * @param y
	 *            The Y distance to pan
	 */
	public void pan(int x, int y) {
		actions.moveToElement(canvas, 500, 200).click().clickAndHold().moveByOffset(1, 1).moveByOffset(x, y).release().build().perform();
	}

	/**
	 * Gets the scale of the map as displayed in the upper-right map scale control. Useful for determining the relative
	 * zoom level of the map.
	 * 
	 * @return The map scale value.
	 */
	public double getMapScale() {
		return Double.parseDouble(mapScale.getText().replaceAll(",", ""));
	}

	/**
	 * Clicks the zoom slider control directly in the middle.
	 */
	public void setZoomSliderMiddle() {
		actions.moveToElement(zoomSlider).click().pause(500).build().perform();
	}

	/**
	 * Drags the zoom slider upward slightly, zooming in
	 */
	public void dragZoomSliderUp() {
		actions.clickAndHold(zoomSliderButton).moveByOffset(0, -5).release().pause(500).build().perform();
	}

	/**
	 * Drags the zoom slider downward slightly, zooming out
	 */
	public void dragZoomSliderDown() {
		actions.clickAndHold(zoomSliderButton).moveByOffset(0, 5).release().pause(500).build().perform();
	}

	/**
	 * Zooms in one tick on the map
	 */
	public void clickZoomIn() {
		actions.click(zoomInButton).pause(500).build().perform();
	}

	/**
	 * Zooms out one tick on the map
	 */
	public void clickZoomOut() {
		actions.click(zoomOutButton).pause(500).build().perform();
	}

	/**
	 * Returns the number of banners currently displayed on the page.
	 * 
	 * @return Number of banners
	 */
	public int getBannerCount() {
		return banners.size();
	}

	/**
	 * Opens a reference to the Measure Toolset. This will activate the Measuring tool.
	 * 
	 * @return The Measure Window Page
	 */
	public MeasureToolPage activateMeasureTool() {
		measureButton.click();
		return new MeasureToolPage(measureWindow);
	}

	/**
	 * Determines if the Measuring tool is active
	 * 
	 * @return True if the measure tool is displayed, false if not
	 */
	public boolean isMeasureToolActive() {
		return measureWindow.isDisplayed();
	}

	/**
	 * Clicks the button to navigate to the Creat Job Panel
	 * 
	 * @return The Create Job Page reference
	 */
	public CreateJobPage navigateCreateJobPage() {
		createJobButton.click();
		return new CreateJobPage(driver);
	}

	/**
	 * Draws a bounding box between two points
	 * <p>
	 * Wraps {@link #drawBoundingBox(int, int, int, int)}
	 * 
	 * @param start
	 *            Starting point
	 * @param end
	 *            End point
	 */
	public void drawBoundingBox(Point start, Point end) throws InterruptedException {
		drawBoundingBox(start.x, start.y, end.x, end.y);
	}

	/**
	 * Draws a bounding box by clicking the start position, and then moving the cursor and clicking again at the end
	 * position.
	 * 
	 * @param x1
	 *            Start X
	 * @param y1
	 *            Start Y
	 * @param x2
	 *            End X
	 * @param y2
	 *            End Y
	 */
	public void drawBoundingBox(int x1, int y1, int x2, int y2) {
		actions.moveToElement(canvas, x1, y1).click().pause(100).build().perform();
		actions.moveByOffset(x2 - x1, y2 - y1).click().pause(100).build().perform();
	}

	/**
	 * Gets the Y position of the upper banner
	 * 
	 * @return Gets the Y position of the upper banner
	 */
	public int getTopBannerPosition() {
		int highestPos = 9999;
		for (WebElement banner : banners) {
			highestPos = Math.min(banner.getLocation().y, highestPos);
		}
		return highestPos;
	}

	/**
	 * Get the Y position of the lower banner
	 * 
	 * @return The Y position of the lower banner
	 */
	public int getBottomBannerPos() {
		int lowestPos = -1;
		for (WebElement banner : banners) {
			lowestPos = Math.max(banner.getLocation().y + banner.getSize().height, lowestPos);
		}
		return lowestPos;
	}

}
