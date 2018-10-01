package bfui.test.util;

import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Utils {
	// Wait for an element to become visible, failing if it does not.
	public static void assertBecomesVisible(String msg, WebElement element, WebDriverWait wait) {
		try {
			wait.until(ExpectedConditions.visibilityOf(element));
		} catch (TimeoutException e) {
			throw new AssertionError(msg, e);
		}
	}

	// Wait for an element to become invisible (or disappear), failing if it still exists.
	public static void assertBecomesInvisible(String msg, WebElement element, WebDriverWait wait) {
		try {
			wait.until(ExpectedConditions.or(ExpectedConditions.not(ExpectedConditions.visibilityOf(element)),
					ExpectedConditions.stalenessOf(element)));
		} catch (TimeoutException e) {
			throw new AssertionError(msg, e);
		}
	}

	// Wait until (something), failing if it does not happen.
	public static void assertThatAfterWait(String msg, ExpectedCondition<?> expected, WebDriverWait wait) {
		try {
			wait.until(expected);
		} catch (TimeoutException e) {
			throw new AssertionError(msg, e);
		}
	}

	// wait until the element does not exist, failing if it is still there.
	public static void assertNotFound(String msg, WebElement element, WebDriverWait wait) {
		try {
			wait.until((WebDriver test) -> checkNotExists(element));
		} catch (TimeoutException e) {
			throw new AssertionError(msg, e);
		}
	}

	// Try to prove an element exists with .getText(), returning false if it fails.
	public static boolean checkExists(WebElement element) {
		try {
			element.getText();
			return true;
		} catch (NoSuchElementException | StaleElementReferenceException e) {
			return false;
		}
	}

	// Try to prove an element does not exist with .getText(), returning true if it fails.
	public static boolean checkNotExists(WebElement element) {
		return !checkExists(element);
	}

	// Check that both coordinates of a point are within range of another point.
	public static void assertPointInRange(Point2D.Double actual, Point2D.Double target, double range) {
		assertPointInRange("", actual, target, range);
	}

	public static void assertPointInRange(String msg, Point2D.Double actual, Point2D.Double target, double range) {
		assertLonInRange(msg, actual.x, target.x, range);
		assertLatInRange(msg, actual.y, target.y, range);
	}

	public static void assertLatInRange(String msg, double actual, double target, double range) {
		assertTrue("Latitude should be within [-90,90]", Math.abs(actual) <= 90);
		if (msg.isEmpty()) {
			msg = "Latitude should be within %f degrees of the target.  Expected <%f>, Actual <%f>";
		} else {
			msg += ": Latitude should be within %f degrees of the target.  Expected <%f>, Actual <%f>";
		}
		assertTrue(String.format(msg, range, target, actual), Math.abs(actual - target) < range);
	}

	public static void assertLonInRange(String msg, double actual, double target, double range) {
		assertTrue("Longitude should be within [-180,180]", Math.abs(actual) <= 180);
		if (msg.isEmpty()) {
			msg = "Longitude should be within %f degrees of the target.  Expected <%f>, Actual <%f>";
		} else {
			msg += ": Longitude should be within %f degrees of the target.  Expected <%f>, Actual <%f>";
		}
		assertTrue(String.format(msg, range, target, actual), Math.abs(actual - target) < range || 360 - Math.abs(actual - target) < range);
	}

	/**
	 * Create the WebDriver to configure for execution on Selenium Grid Chrome driver.
	 * 
	 * @return The Remote Chrome Driver.
	 */
	public static WebDriver getChromeRemoteDriver() throws MalformedURLException {
		String gridUrl = "http://localhost:4444/wd/hub";
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--ignore-certificate-errors");
		options.setCapability("acceptInsecureCerts", true);
		options.setCapability("acceptSslCerts", true);
		options.setCapability("acceptInsecureCerts", true);
		options.setCapability("commandTimeout", "600");
		options.setCapability("idleTimeout", "1000");
		options.setCapability("screenResolution", "1920x1080");
		RemoteWebDriver driver = new RemoteWebDriver(new URL(gridUrl), options);
		// Most requests should be given an implicit wait of 5 seconds, for animations to settle and pages to load.
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		return driver;
	}

	// Try to click an element, returning true if it is successful, false if it throws an error.
	public static boolean tryToClick(WebElement element) {
		try {
			element.click();
			return true;
		} catch (WebDriverException e) {
			return false;
		}
	}

	// Get the web element in the second column in the row where the first column has string.
	public static WebElement getTableData(WebElement table, String name) {
		int i = 0;
		for (WebElement header : table.findElements(By.tagName("dt"))) {
			if (header.getText().equals(name)) {
				return table.findElements(By.tagName("dd")).get(i);
			}
			i++;
		}
		return null;
	}

	// Convert coordinates to a string in DMS.
	public static String pointToDMS(Point2D.Double point) {
		String dirX;
		String dirY;
		if (point.x >= 0) {
			dirX = "E";
		} else {
			dirX = "W";
		}
		if (point.y >= 0) {
			dirY = "N";
		} else {
			dirY = "S";
		}
		// Format: DDMMSS(N/S)DDDMMSS(E/W) <---Longitude has one less degree place.
		return coordToDMS(Math.abs(point.y)).substring(1) + dirY + coordToDMS(Math.abs(point.x)) + dirX;
	}

	public static String coordToDMS(double coord) {
		int deg = (int) coord;
		int min = (int) ((coord - (double) deg) * 60);
		int sec = (int) ((coord - (double) deg - (double) min / 60) * 3600);
		return String.format("%03d%02d%02d", deg, min, sec);
	}

	public static void scrollInToView(WebDriver driver, WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}

	public static int getWindowInnerHeight(WebDriver driver) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		return ((Long) js.executeScript("return window.innerHeight")).intValue();
	}
}