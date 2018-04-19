package bfui.test.util;

import static org.junit.Assert.*;

import java.awt.Robot;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assume;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Utils {

	// Check that an element is present on the page,
	// without throwing an exception if it is not present.
	public static boolean isElementPresent(WebDriver driver, By by) {
		try {
			driver.findElement(by);
			return true;
		}
		catch (NoSuchElementException e) {
			return false;
		}
	}

	// Wait for an element to exist, failing if it does not.
	private static WebElement assertElementLoads_GENERIC(String msg, Object o, WebDriverWait wait, By by) {
		try {
			wait.until(ExpectedConditions.presenceOfElementLocated(by));
		} catch (TimeoutException e) {
			throw new AssertionError(msg, e);
		}
		if (o instanceof WebDriver) {
			return ((WebDriver) o).findElement(by);
		} else if (o instanceof WebElement) {
			return ((WebElement) o).findElement(by);
		} else {
			return null;
		}
	}
	public static WebElement assertElementLoads(String msg, WebElement element, WebDriverWait wait, By by) {
		return assertElementLoads_GENERIC(msg, element, wait, by);
	}
	public static WebElement assertElementLoads(String msg, WebDriver driver, WebDriverWait wait, By by) {
		return assertElementLoads_GENERIC(msg, driver, wait, by);
	}
	
	// Wait for an element to become visible, failing if it does not.
	public static void assertBecomesVisible(String msg, WebElement element, WebDriverWait wait) {
		try {
			wait.until(ExpectedConditions.visibilityOf(element));
		} catch (TimeoutException e) {
			throw new AssertionError(msg, e);
		}
	}
	public static void assertBecomesVisible(WebElement element, WebDriverWait wait) {
		assertBecomesVisible("", element, wait);
	}
	
	// Wait for an element to become invisible (or disappear), failing if it still exists.
	public static void assertBecomesInvisible(String msg, WebElement element, WebDriverWait wait) {
		try {
			wait.until(
					ExpectedConditions.or(
							ExpectedConditions.not(ExpectedConditions.visibilityOf(element)), 
							ExpectedConditions.stalenessOf(element)
					)
			);
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

	// Send keys to the active element.
	public static void typeToFocus(WebDriver driver, CharSequence k) {
		getFocusedField(driver).sendKeys(k);
	}
	
	// Get the active element.
	public static WebElement getFocusedField(WebDriver driver) {
		return driver.switchTo().activeElement();
	}
	
	// Check that both coordinates of a point are within range of another point.
	public static void assertPointInRange(Point2D.Double actual, Point2D.Double target, double range) {
		assertPointInRange("", actual, target, range);
	}
	public static void assertPointInRange(String msg, Point2D.Double actual, Point2D.Double target, double range) {
		assertLonInRange(msg, actual.x, target.x, range);
		assertLatInRange(msg, actual.y, target.y, range);
	}

	// Check that a latitude is within range.
	public static void assertLatInRange(double actual, double target, double range) {
		assertLatInRange("", actual, target, range);
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

	// Check that a longitude is within a range, accounting for wrap-around.
	public static void assertLonInRange(double actual, double target, double range) {
		assertLonInRange("", actual, target, range);
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
	
	// Return a new WebDriver.  Follow a process based on a chrome or firefox driver.
	public static WebDriver createWebDriver(String browserPath, String driverPath) throws Exception {
		if (browserPath.contains("fox")) {
			System.setProperty("webdriver.gecko.driver", driverPath);
			DesiredCapabilities caps = DesiredCapabilities.firefox();
			caps.setCapability("marionette", false);
			caps.setCapability("acceptInsecureCerts",true);
			caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			return new FirefoxDriver(caps);
		} else if (browserPath.contains("chrom")) {
			Logger logger = Logger.getLogger("");
			logger.setLevel(Level.OFF);
			DesiredCapabilities caps = DesiredCapabilities.chrome();
			System.setProperty("webdriver.chrome.driver", driverPath);
			caps.setCapability("chrome.verbose", false);
			caps.setCapability("acceptInsecureCerts",true);
			return new ChromeDriver(caps);
		} else {
			throw new Exception("Could not identify browser from path: " + browserPath);
		}
	}
	
	public static RemoteWebDriver createSauceDriver(String testName) throws Exception {
		String browser = System.getenv("browser");
		DesiredCapabilities caps;
		RemoteWebDriver driver;
		String url = "";
		
		if (browser.equals("chrome")) {
			url="http://54.190.42.103:4444/wd/hub";
		    ChromeOptions ops = new ChromeOptions();
		    ops.addArguments("--ignore-certificate-errors");
		    
		    //ops.setCapability("platform", "Windows 10");
		    //ops.setCapability("version", "60");
			//ops.setCapability("seleniumVersion", "3.8.1");
		    ops.setCapability("name", testName);
		    //ops.setCapability(ChromeOptions.CAPABILITY, ops);
		    ops.setCapability("acceptInsecureCerts", true);
		    ops.setCapability("acceptSslCerts", true);
		    ops.setCapability("acceptInsecureCerts", true);
			ops.setCapability("commandTimeout", "600");
			ops.setCapability("idleTimeout", "1000");
			ops.setCapability("screenResolution", "1280x1024");
			driver = new RemoteWebDriver(new URL(url), ops);
		    
		} else if (browser.equals("firefox")) {
			url="http://34.217.104.90:4444/wd/hub";
			FirefoxOptions ops = new FirefoxOptions();
			ops.addArguments("--trustAllSSLCertificates");
			//caps.setCapability("platform", "Windows 10");
			///caps.setCapability("version", "45.0");
			//caps.setCapability("seleniumVersion", "3.8.1");
			ops.setCapability("marionette", true);
			ops.setCapability("name", testName);
		    ops.setCapability("acceptInsecureCerts", true);
		    ops.setCapability("acceptSslCerts", true);
		    ops.setCapability("acceptInsecureCerts", true);
			ops.setCapability("commandTimeout", "600");
			ops.setCapability("idleTimeout", "600");
		    driver = new RemoteWebDriver(new URL(url), ops);
		} else {
			throw new Exception("The browser, " + browser + " is not supported.");
		}
		
	    
	    
		
		// give SauceStatusReporter driver so it knows session id.
		//SauceResultReporter.setSession(driver.getSessionId());
		
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

	// Move the mouse a back-and-forth a couple pixels.
	public static void jostleMouse(Actions actions, WebElement element,WebDriver driver) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
		actions.moveToElement(element, 500, 100).moveByOffset(1, 1).moveByOffset(-1, -1).build().perform();
	}
	public static void jostleMouse(Robot robot, WebElement element) {
		robot.mouseMove(element.getSize().width/2, element.getSize().height/2);
		robot.mouseMove(element.getSize().width/2 + 1, element.getSize().height/2 + 1);
	}
	
	// Send a GET request to the URL, and return the integer status code.
	public static int getStatusCode(String path) throws IOException {
		URL url = new URL(path);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		
		return connection.getResponseCode();
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
		int min = (int) ((coord - (double) deg)*60);
		int sec = (int) ((coord - (double) deg - (double) min/60)*3600);
		return String.format("%03d%02d%02d", deg, min, sec);
	}
	
	// Check the $space environment Variable.  If it is "int", ignore the test.
	public static void ignoreOnInt() {
		String space = System.getenv("space");
		if (space != null) {
			Assume.assumeFalse("Not running this test in the `int` environment", space.equals("int"));
		}
	}
	
	public static int getWindowInnerHeight(WebDriver driver) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		return ((Long) js.executeScript("return window.innerHeight")).intValue();
	}
}