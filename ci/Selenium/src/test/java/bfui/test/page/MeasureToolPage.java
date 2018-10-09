package bfui.test.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import bfui.test.util.SearchContextElementLocatorFactory;

/**
 * The Measure Tool
 */
public class MeasureToolPage {
	/* @formatter:off */
	@FindBy(className = "measureControl__distance")		private WebElement measurement;
	@FindBy(className = "measureControl__units")		private WebElement units;
	@FindBy(className = "measureControl__close")		private WebElement closeButton;
	/* @formatter:on */

	/**
	 * Creates a Page instance for the Measuring tool, based off of the parent Measuring control.
	 * 
	 * @param parent
	 *            The parent measure control with class "measure-dialog"
	 */
	public MeasureToolPage(WebElement parent) {
		PageFactory.initElements(new SearchContextElementLocatorFactory(parent), this);
	}

	/**
	 * Gets the measurement value
	 * 
	 * @return The measurement value
	 */
	public double getMeasurement() {
		return Double.parseDouble(measurement.getText());
	}

	/**
	 * Set the drop-down to the specified units value
	 * 
	 * @param selectedUnits
	 *            The drop-down select value
	 */
	public void selectUnits(String selectedUnits) {
		(new Select(units)).selectByVisibleText(selectedUnits);
	}

	/**
	 * Closes this control
	 */
	public void close() {
		closeButton.click();
	}
}