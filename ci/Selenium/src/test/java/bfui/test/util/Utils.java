package bfui.test.util;

import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
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

	/**
	 * Performs assertions that each lat/lon value of a point is within a specific range of a target point.
	 * 
	 * @param message
	 *            The assertion message
	 * @param actual
	 *            The actual point
	 * @param target
	 *            The expected target point
	 * @param range
	 *            The range
	 */
	public static void assertPointInRange(String message, Point2D.Double actual, Point2D.Double target, double range) {
		assertTrue("Longitude should be within [-180,180]", Math.abs(actual.x) <= 180);
		assertTrue(String.format(message + ": Longitude should be within %f degrees of the target.  Expected <%f>, Actual <%f>", range,
				target.x, actual.x), Math.abs(actual.x - target.x) < range || 360 - Math.abs(actual.x - target.x) < range);
		assertTrue("Latitude should be within [-90,90]", Math.abs(actual.y) <= 90);
		assertTrue(String.format(message + ": Latitude should be within %f degrees of the target.  Expected <%f>, Actual <%f>", range,
				target.y, actual.y), Math.abs(actual.y - target.y) < range);
	}

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
		// driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
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

	/**
	 * Converts a HDMS Coordinate, as represented in the string format in the OpenLayers coordinate control with HDMS
	 * format, to a lat/lon decimal degree.
	 * <p>
	 * Example form of this HDMS string is "85° 06? 10? N 5° 00? 07? E" or "84° 53? 44? S 4° 59? 41? W"
	 * 
	 * @param dmsString
	 *            HDMS Coordinate string
	 * @return Decimal degrees point
	 */
	public static Point2D.Double HdmsToPoint(String dmsString) {
		// Tokenize the HDMS String
		Pattern pattern = Pattern.compile("([0-9]+).\\s([0-9]+).\\s([0-9]+).\\s([A-Z]+)\\s([0-9]+).\\s([0-9]+).\\s([0-9]+).\\s([A-Z]+)");
		Matcher matcher = pattern.matcher(dmsString);
		Double lonD = null, lonM = null, lonS = null, latD = null, latM = null, latS = null;
		String lonH = null, latH = null;
		if (matcher.find()) {
			// Parse the Tokens for individual values
			latD = Double.parseDouble(matcher.group(1));
			latM = Double.parseDouble(matcher.group(2));
			latS = Double.parseDouble(matcher.group(3));
			latH = matcher.group(4);
			lonD = Double.parseDouble(matcher.group(5));
			lonM = Double.parseDouble(matcher.group(6));
			lonS = Double.parseDouble(matcher.group(7));
			lonH = matcher.group(8);
		}
		// Calculate Northing and Easting as lat/lon
		Double lat = (latD + ((latM * 60 + latS) / 3600)) * (latH.equals("S") ? -1 : 1);
		Double lon = (lonD + ((lonM * 60 + lonS) / 3600)) * (lonH.equals("W") ? -1 : 1);
		// Wrap coordinates
		return new Point2D.Double(lon, lat);
	}

	public static void takeScreenshot(WebDriver driver) {
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			Random random = new Random();
			String fileName = "C:/temp/screenshot" + random.nextLong() + ".png";
			FileUtils.copyFile(scrFile, new File(fileName));
			System.out.println(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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