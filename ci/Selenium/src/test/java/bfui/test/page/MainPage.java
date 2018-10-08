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
	@FindBy(className = "BrowserSupport-root")				public WebElement browserSupportWindow;
	@FindBy(className = "BrowserSupport-close")				public WebElement browserSupportDismiss;
	@FindBy(className = "Login-button")						public WebElement geoAxisLink;
	@FindBy(className = "Login-warning")					public WebElement consentBanner;
	@FindBy(className = "PrimaryMap-logout")				public WebElement logoutButton;
	@FindBy(className = "Navigation-linkHome")				public WebElement homeButton;
	@FindBy(className = "Navigation-linkJobs")				public WebElement jobsButton;
	@FindBy(className = "Navigation-linkCreateJob")			public WebElement createJobButton;
	@FindBy(className = "PrimaryMap-search")				public WebElement searchButton;
	@FindBy(className = "PrimaryMap-measure")				public WebElement measureButton;
	@FindBy(className = "ol-zoom-in")						public WebElement zoomInButton;
	@FindBy(className = "ol-zoom-out")						public WebElement zoomOutButton;
	@FindBy(className = "ol-zoomslider-thumb")				public WebElement zoomSliderButton;
	@FindBy(className = "ol-zoomslider")					public WebElement zoomSlider;
	@FindBy(className = "ol-mouse-position")				public WebElement mouseoverCoordinates;
	@FindBy(className = "ol-viewport")						public WebElement viewport;
	@FindBy(className = "ol-unselectable")					public WebElement canvas;
	@FindBy(className = "coordinate-dialog")				public WebElement searchWindow;
	@FindBy(className = "measure-dialog")					public WebElement measureWindow;
	@FindBy(className = "CreateJob-root")					public WebElement createJobWindow;
	@FindBy(className = "JobStatusList-root")				public WebElement jobsWindow;
	@FindBy(className = "FeatureDetails-root")				public WebElement featureDetails;
	@FindBy(className = "SessionExpired-root")				public WebElement sessionExpiredOverlay;
	@FindBy(className = "SessionLoggedOut-root")			public WebElement loggedOutOverlay;
	@FindBy(className = "ClassificationBanner-root")		public List<WebElement> banners;
	@FindBy(xpath = "//div[contains(@class,'PrimaryMap-scale')]/div/span")			
															public WebElement mapScale;
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
		actions.pause(250).click(canvas).pause(250).build().perform();
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
	public JobsPage displayJobs() {
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
	 * Clicks the button to navigate to the Creat Job Panel
	 * 
	 * @return The Create Job Page reference
	 */
	public CreateJobPage navigateCreateJobPage() {
		createJobButton.click();
		return new CreateJobPage(driver);
	}

	public JobsPage jobsWindow() {
		return new JobsPage(driver);
	}

	public void drawBoundingBox(Point start, Point end) throws InterruptedException {
		drawBoundingBox(start.x, start.y, end.x, end.y);
	}

	public int getFeatureCloudCover() {
		WebElement cloudCoverContainer = Utils.getTableData(featureDetails, "CLOUD COVER");
		int returnedInt = Integer.parseInt(cloudCoverContainer.getText().replaceFirst("%", ""));
		return returnedInt;
	}

	public boolean isBetweenBanners(WebElement element) {
		int bottomOfTopBanner = banners.get(0).getLocation().y + banners.get(0).getSize().height;
		int topOfBottomBanner = banners.get(1).getLocation().y;
		int topOfElement = element.getLocation().y;
		int bottomOfElement = element.getLocation().y + element.getSize().height;
		return topOfElement >= bottomOfTopBanner && bottomOfElement <= topOfBottomBanner;
	}

	public int topBannerPos() {
		int highestPos = 9999;
		for (WebElement banner : banners) {
			highestPos = Math.min(banner.getLocation().y, highestPos);
		}
		return highestPos;
	}

	public int bottomBannerPos() {
		int lowestPos = -1;
		for (WebElement banner : banners) {
			lowestPos = Math.max(banner.getLocation().y + banner.getSize().height, lowestPos);
		}
		return lowestPos;
	}

}
