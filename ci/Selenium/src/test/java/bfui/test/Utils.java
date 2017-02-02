package bfui.test;

import static org.junit.Assert.*;

import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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

	static void assertLatInRange(double actual, double target, double range) {
		assertLatInRange("", actual, target, range);
	}

	static void assertLatInRange(String msg, double actual, double target, double range) {
		assertTrue("Latitude should be within [-90,90]", Math.abs(actual) <= 90);
		if (msg.isEmpty()) {
			msg = "Latitude should be within %f degrees of the target";
		} else {
			msg += ": Latitude should be within %f degrees of the target";
		}
		assertTrue(String.format(msg, range), Math.abs(actual - target) < range);
	}

	static void assertLonInRange(double actual, double target, double range) {
		assertLonInRange("", actual, target, range);
	}

	static void assertLonInRange(String msg, double actual, double target, double range) {
		assertTrue("Longitude should be within [-180,180]", Math.abs(actual) <= 180);
		if (msg.isEmpty()) {
			msg = "Longitude should be within %f degrees of the target";
		} else {
			msg += ": Longitude should be within %f degrees of the target";
		}
		assertTrue(String.format(msg, range), Math.abs(actual - target) < range || Math.abs(actual - target) - 180 < range);
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
		actions.moveToElement(element).moveByOffset(1, 1).moveByOffset(-1, -1).build().perform();
	}
}