package bfui.test.page;

import java.awt.geom.Point2D;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import bfui.test.page.core.PageObject;

/**
 * The coordinate search dialog, accessed by clicking the magnifying glass on the map. This page allows users to search
 * for map locations based on coordinates.
 */
public class CoordinateSearchPage extends PageObject {
	/* @formatter:off */
	@FindBy(css = "button[type=submit]")								private WebElement submitButton;
	@FindBy(css = "input[placeholder='Enter Coordinates']")				private WebElement entry;
	@FindBy(className = "error-message")								private WebElement errorMessage;
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
	public void search(String coordinates) {
		entry.clear();
		entry.sendKeys(coordinates);
		new Actions(driver).click(submitButton).pause(500).build().perform(); // Wait for map animation
	}

	/**
	 * Detects if the invalid entry error is displayed or not
	 * 
	 * @return True if error is displayed, false if not
	 */
	public boolean isErrorVisible() {
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
	public void search(Point2D.Double point) {
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
	public void search(double lat, double lon) {
		search(Double.toString(lat) + ", " + Double.toString(lon));
	}
}