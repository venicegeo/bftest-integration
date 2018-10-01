package bfui.test.page;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
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
	@FindBy(className = "Navigation-linkHelp")				public WebElement helpButton;
	@FindBy(className = "Navigation-linkJobs")				public WebElement jobsButton;
	@FindBy(className = "Navigation-linkCreateJob")			public WebElement createJobButton;
	@FindBy(className = "Navigation-linkProductLines")		public WebElement productLinesButton;
	@FindBy(className = "Navigation-linkCreateProductLine")	public WebElement createProductLineButton;
	@FindBy(className = "PrimaryMap-search")				public WebElement searchButton;
	@FindBy(className = "PrimaryMap-measure")				public WebElement measureButton;
	@FindBy(className = "ol-zoom-in")						public WebElement zoomInButton;
	@FindBy(className = "ol-zoom-out")						public WebElement zoomOutButton;
	@FindBy(className = "ol-zoomslider-thumb")				public WebElement zoomSliderButton;
	@FindBy(className = "ol-zoomslider")					public WebElement zoomSlider;
	@FindBy(className = "ol-mouse-position")				public WebElement mouseoverCoordinates;
	@FindBy(className = "ol-unselectable")					public WebElement canvas;
	@FindBy(className = "coordinate-dialog")				public WebElement searchWindow;
	@FindBy(className = "measure-dialog")					public WebElement measureWindow;
	@FindBy(className = "CreateJob-root")					public WebElement createJobWindow;
	@FindBy(className = "JobStatusList-root")				public WebElement jobsWindow;
	@FindBy(className = "FeatureDetails-root")				public WebElement featureDetails;
	@FindBy(className = "SessionExpired-root")				public WebElement sessionExpiredOverlay;
	@FindBy(className = "SessionLoggedOut-root")			public WebElement loggedOutOverlay;
	@FindBy(className = "ClassificationBanner-root")		public List<WebElement> banners;
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
		// OAuth redirect behavior is not constant. Sometimes a redirect occurs, sometimes it does not. Handle either
		// condition. If the redirect occurs, then the URL will change to the logout page. If it does not, then the
		// Login screen will become visible again in the client.
		WebDriverWait wait = new WebDriverWait(driver, 3);
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
		return geoAxisLink.isEnabled();
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
	 * Checks if the current session is expired
	 * 
	 * @return True if session expired splash is displayed
	 */
	public boolean isSessionExpired() {
		return sessionExpiredOverlay.isDisplayed();
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

	public ImageSearchPage searchWindow() {
		return new ImageSearchPage(searchWindow);
	}

	public MeasureToolPage measureWindow() {
		Utils.assertBecomesVisible("Measure Tool window should be present", measureWindow, new WebDriverWait(driver, 3));
		return new MeasureToolPage(measureWindow);
	}

	public CreateJobPage createJobWindow() {
		return new CreateJobPage(createJobWindow);
	}

	public JobsPage jobsWindow() {
		return new JobsPage(driver);
	}

	public double zoomSliderValue() {
		String rx = "\\d+\\.?\\d*";
		Matcher m = Pattern.compile(rx).matcher(zoomSliderButton.getAttribute("style"));

		if (m.find()) {
			return -Double.parseDouble(m.group());
		} else {
			return 0;
		}
	}

	public void drawBoundingBox(Actions actions, int x1, int y1, int x2, int y2) throws InterruptedException {
		Utils.scrollInToView(driver, canvas);
		actions.moveToElement(canvas, x1, y1).click().build().perform();
		Thread.sleep(1000);
		actions.moveByOffset(x2 - x1, y2 - y1).click().build().perform();
	}

	public void drawBoundingBox(Actions actions, Point start, Point end) throws InterruptedException {
		drawBoundingBox(actions, start.x, start.y, end.x, end.y);
	}

	public Point2D.Double getCoords() {

		/*
		 * THIS USES AN INTERNAL API THAT IS SUBJECT TO CHANGE WITHOUT WARNING�
		 */
		JavascriptExecutor js = (JavascriptExecutor) driver;
		// System.out.println(js.executeScript(js2ex));
		ArrayList<Number> result = ((ArrayList) js.executeScript("return window.primaryMap.props.view.center"));
		System.out.println(result.get(0).doubleValue());
		System.out.println(result.get(1).doubleValue());
		return new Point2D.Double(result.get(0).doubleValue(), result.get(1).doubleValue());
		/*
		 * THIS USES AN INTERNAL API THAT IS SUBJECT TO CHANGE WITHOUT WARNING�
		 */

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

	public void pan(int x, int y) throws InterruptedException {
		Utils.scrollInToView(driver, canvas);
		actions.moveToElement(canvas, 500, 200).click().clickAndHold().moveByOffset(1, 1).moveByOffset(x, y).release().build().perform();
		Thread.sleep(2000);
	}
}
