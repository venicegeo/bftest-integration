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
	@FindBy(className = "measureControl__distance")			public WebElement measurement;
	@FindBy(className = "measureControl__units")			public WebElement units;
	@FindBy(className = "measureControl__close")			public WebElement closeButton;
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

	public double getMeasurement() {
		return Double.parseDouble(measurement.getText());
	}

	public void selectUnits(String selectedUnits) {
		(new Select(units)).selectByVisibleText(selectedUnits);
	}

	public void close() {
		closeButton.click();
	}
}