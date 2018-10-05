package bfui.test.page;

import java.awt.geom.Point2D;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import bfui.test.page.core.PageObject;

/**
 * The coordinate search dialog, accessed by clicking the magnifying glass on the map. This page allows users to search
 * for map locations based on coordinates.
 */
public class CoordinateSearchPage extends PageObject {
	/* @formatter:off */
	@FindBy(css = "button[type=submit]")								public WebElement submitButton;
	@FindBy(css = "input[placeholder='Enter Coordinates']")				public WebElement entry;
	@FindBy(className = "error-message")								public WebElement errorMessage;
	/* @formatter:on */

	public CoordinateSearchPage(WebDriver driver) {
		super(driver);
	}

	/**
	 * Search for a coordinate string.
	 * 
	 * @param coordinates
	 *            Free-form coordinate String
	 */
	public void search(String coordinates) throws InterruptedException {
		entry.clear();
		entry.sendKeys(coordinates);
		submitButton.click();
		// Wait for map pan animation to complete.
		Thread.sleep(250);
	}

	/**
	 * Detects if the invalid entry error is displayed or not
	 * 
	 * @return True if error is displayed, false if not
	 */
	public boolean isErrorVisible() {
		// // Give a small period of time until the window is displayed
		// WebDriverWait wait = new WebDriverWait(driver, (long) 0.25);
		// wait.until(ExpectedConditions.visibilityOf(errorMessage));
		return errorMessage.isDisplayed();
	}

	/**
	 * Search for lat/lon coordinates, represented as a Point2D object.
	 * <p>
	 * Wraps {@link #search(String)}
	 * 
	 * @param point
	 *            The coordinates. lat=y, lon=x
	 */
	public void search(Point2D.Double point) throws InterruptedException {
		search(point.y, point.x);
	}

	/**
	 * Search for latitude and longitude coordinates, represented as a pair of doubles.
	 * <p>
	 * Wraps {@link #search(String)}
	 * 
	 * @param lat
	 *            Latitude
	 * @param lon
	 *            Longitude
	 */
	public void search(double lat, double lon) throws InterruptedException {
		search(Double.toString(lat) + ", " + Double.toString(lon));
	}
}