package bfui.test;

import static org.junit.Assert.*;

import java.awt.Robot;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assume;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;

public class Utils {

	// Check that an element is present on the page,
	// without throwing an exception if it is not present.
	static boolean isElementPresent(WebDriver driver, By by) {
		try {
			driver.findElement(by);
			return true;
		}
		catch (NoSuchElementException e) {
			return false;
		}
	}

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
	static WebElement assertElementLoads(String msg, WebElement element, WebDriverWait wait, By by) {
		return assertElementLoads_GENERIC(msg, element, wait, by);
	}
	static WebElement assertElementLoads(String msg, WebDriver driver, WebDriverWait wait, By by) {
		return assertElementLoads_GENERIC(msg, driver, wait, by);
	}
	
	static void assertBecomesVisible(String msg, WebElement element, WebDriverWait wait) {
		try {
			wait.until(ExpectedConditions.visibilityOf(element));
		} catch (TimeoutException e) {
			throw new AssertionError(msg, e);
		}
	}
	public static void assertBecomesVisible(WebElement element, WebDriverWait wait) {
		assertBecomesVisible("", element, wait);
	}
	
	static void assertBecomesInvisible(String msg, WebElement element, WebDriverWait wait) {
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
	
	static void assertThatAfterWait(String msg, ExpectedCondition<?> expected, WebDriverWait wait) {
		try {
			wait.until(expected);
		} catch (TimeoutException e) {
			throw new AssertionError(msg, e);
		}
	}
	
	static void assertNotFound(String msg, WebElement element, WebDriverWait wait) {
		try {
			wait.until((WebDriver test) -> checkNotExists(element));
		} catch (TimeoutException e) {
			throw new AssertionError(msg, e);
		}
	}
	
	static boolean checkExists(WebElement element) {
		try {
			element.getText();
			return true;
		} catch (NoSuchElementException | StaleElementReferenceException e) {
			return false;
		}
	}
	
	static boolean checkNotExists(WebElement element) {
		return !checkExists(element);
	}

	static void typeToFocus(WebDriver driver, CharSequence k) {
		getFocusedField(driver).sendKeys(k);
	}

	static WebElement getFocusedField(WebDriver driver) {
		return driver.switchTo().activeElement();
	}
	
	static void assertPointInRange(Point2D.Double actual, Point2D.Double target, double range) {
		assertPointInRange("", actual, target, range);
	}
	static void assertPointInRange(String msg, Point2D.Double actual, Point2D.Double target, double range) {
		assertLonInRange(msg, actual.x, target.x, range);
		assertLatInRange(msg, actual.y, target.y, range);
	}

	static void assertLatInRange(double actual, double target, double range) {
		assertLatInRange("", actual, target, range);
	}

	static void assertLatInRange(String msg, double actual, double target, double range) {
		assertTrue("Latitude should be within [-90,90]", Math.abs(actual) <= 90);
		if (msg.isEmpty()) {
			msg = "Latitude should be within %f degrees of the target.  Expected <%f>, Actual <%f>";
		} else {
			msg += ": Latitude should be within %f degrees of the target.  Expected <%f>, Actual <%f>";
		}
		assertTrue(String.format(msg, range, target, actual), Math.abs(actual - target) < range);
	}

	static void assertLonInRange(double actual, double target, double range) {
		assertLonInRange("", actual, target, range);
	}

	static void assertLonInRange(String msg, double actual, double target, double range) {
		assertTrue("Longitude should be within [-180,180]", Math.abs(actual) <= 180);
		if (msg.isEmpty()) {
			msg = "Longitude should be within %f degrees of the target.  Expected <%f>, Actual <%f>";
		} else {
			msg += ": Longitude should be within %f degrees of the target.  Expected <%f>, Actual <%f>";
		}
		assertTrue(String.format(msg, range, target, actual), Math.abs(actual - target) < range || 360 - Math.abs(actual - target) < range);
	}
	
	static WebDriver createWebDriver(String browserPath, String driverPath) throws Exception {
		if (browserPath.contains("fox")) {
			System.setProperty("webdriver.gecko.driver", driverPath);
			FirefoxBinary binary =new FirefoxBinary(new File(browserPath));
			FirefoxProfile profile = new FirefoxProfile();
			return new FirefoxDriver(binary, profile);
		} else if (browserPath.contains("chrom")) {
			System.setProperty("webdriver.chrome.driver", driverPath);
			return new ChromeDriver();
		} else {
			throw new Exception("Could not identify browser from path: " + browserPath);
		}
	}
	
	static boolean tryToClick(WebElement element) {
		try {
			element.click();
			return true;
		} catch (WebDriverException e) {
			return false;
		}
	}

	static void jostleMouse(Actions actions, WebElement element) {
		actions.moveToElement(element, 500, 100).moveByOffset(1, 1).moveByOffset(-1, -1).build().perform();
	}
	static void jostleMouse(Robot robot, WebElement element) {
		robot.mouseMove(element.getSize().width/2, element.getSize().height/2);
		robot.mouseMove(element.getSize().width/2 + 1, element.getSize().height/2 + 1);
	}
	
	static int getStatusCode(String path) throws IOException {
		URL url = new URL(path);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		
		return connection.getResponseCode();
	}
	
	static WebElement getTableData(WebElement table, String name) {
		int i = 0;
		for (WebElement header : table.findElements(By.tagName("dt"))) {
			if (header.getText().equals(name)) {
				return table.findElements(By.tagName("dd")).get(i);
			}
			i++;
		}
		return null;
	}
	
	static String pointToDMS(Point2D.Double point) {
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
	static String coordToDMS(double coord) {
		int deg = (int) coord;
		int min = (int) ((coord - (double) deg)*60);
		int sec = (int) ((coord - (double) deg - (double) min/60)*3600);
		return String.format("%03d%02d%02d", deg, min, sec);
	}
	
	// Check the $space environment Variable.  If it is "int", ignore the test.
	static void ignoreOnInt() {
		String space = System.getenv("space");
		if (space != null) {
			Assume.assumeFalse("Not running this test in the `int` environment", space.equals("int"));
		}
	}
}