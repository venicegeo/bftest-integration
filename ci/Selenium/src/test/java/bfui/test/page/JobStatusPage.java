package bfui.test.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import bfui.test.util.SearchContextElementLocatorFactory;

/**
 * Page for a single Job Status from the Jobs list. This objects locator is based on an individual Job Status entry.
 */
public class JobStatusPage {
	/* @formatter:off */
	@FindBy(className = "JobStatus-title")								public WebElement name;
	@FindBy(className = "JobStatus-caret")								public WebElement caret;
	@FindBy(className = "JobStatus-removeToggle")						public WebElement forgetDiv;
	@FindBy(css = ".JobStatus-removeToggle > button")					public WebElement forgetButton;
	@FindBy(css = ".JobStatus-removeWarning > button")					public WebElement confirmButton;
	@FindBy(css = ".JobStatus-removeWarning > button:nth-of-type(2)")	public WebElement cancelButton;
	@FindBy(className = "JobStatus-removeWarning")						public WebElement warningDiv;
	@FindBy(css = "a[title=\"View on Map\"]")							public WebElement viewLink;
	/* @formatter:on */

	public WebElement jobStatusContainer;

	/**
	 * Creates an instance for a single Job Status.
	 * <p>
	 * The parent must be the "JobStatus-root" CSS class or this Page will not be able to locate any elements.
	 * 
	 * @param parent
	 *            The "JobStatus-root" element in the DOM that contains this particular Job Status entry.
	 */
	public JobStatusPage(WebElement parent) {
		PageFactory.initElements(new SearchContextElementLocatorFactory(parent), this);
		jobStatusContainer = parent;
	}

	public String getName() {
		return name.getText();
	}

	public WebElement forgetButton() {
		return forgetDiv.findElement(By.tagName("button"));
	}

	public WebElement downloadButton() {
		return jobStatusContainer.findElement(By.className("JobStatus-download"));
	}

	public WebElement downloadLinkGeojson() {
		return jobStatusContainer.findElement(By.cssSelector("a[title=\"Download GeoJSON\"]"));
	}

	public WebElement downloadLinkGeopkg() {
		return jobStatusContainer.findElement(By.cssSelector("a[title=\"Download GeoPackage\"]"));
	}

	public WebElement downloadLinkShapefile() {
		return jobStatusContainer.findElement(By.cssSelector("a[title=\"Download Shapefile\"]"));
	}

	public WebElement confirmButton() {
		return warningDiv.findElement(By.xpath("button[contains(text(),'Remove this Job')]"));
	}

	public WebElement cancelButton() {
		return warningDiv.findElement(By.xpath("button[contains(text(),'Cancel')]"));
	}

}