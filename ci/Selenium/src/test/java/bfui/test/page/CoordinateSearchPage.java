package bfui.test.page;

import java.awt.geom.Point2D;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import bfui.test.page.core.PageObject;

/**
 * The coordinate search dialog, accessed by clicking the magnifying glass on the map. This page allows users to search
 * for map locations based on coordinates.
 */
public class CoordinateSearchPage extends PageObject {
	/* @formatter:off */
	@FindBy(css = "button[type=submit]")								public WebElement submitButton;
	@FindBy(css = "input[placeholder='Enter Coordinates']")				public WebElement entry;
	@FindBy(xpath = "/html/body/div/div/main/form/div[2]/dl/dd/code")	public List<WebElement> exampleCoordinates;
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
	public void searchCoordinates(String coordinates) throws InterruptedException {
		entry.clear();
		entry.sendKeys(coordinates);
		submitButton.click();
		// Wait for the Dialog to disappear
		WebDriverWait wait = new WebDriverWait(driver, 1);
		wait.until(ExpectedConditions.invisibilityOf(submitButton));
		// Wait for map pan animation to complete.
		Thread.sleep(100);
	}

	/**
	 * Search for lat/lon coordinates, represented as a Point2D object.
	 * <p>
	 * Wraps {@link #searchCoordinates(String)}
	 * 
	 * @param point
	 *            The coordinates. lat=y, lon=x
	 */
	public void searchCoordinates(Point2D.Double point) throws InterruptedException {
		searchCoordinates(point.y, point.x);
	}

	/**
	 * Search for latitude and longitude coordinates, represented as a pair of doubles.
	 * <p>
	 * Wraps {@link #searchCoordinates(String)}
	 * 
	 * @param lat
	 *            Latitude
	 * @param lon
	 *            Longitude
	 */
	public void searchCoordinates(double lat, double lon) throws InterruptedException {
		searchCoordinates(Double.toString(lat) + ", " + Double.toString(lon));
	}
}