package bfui.test.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import bfui.test.util.SearchContextElementLocatorFactory;

/**
 * Page for a single Job Status from the Jobs list. This objects locator is based on an individual Job Status entry.
 */
public class JobStatusPage {
	/* @formatter:off */
	@FindBy(xpath = "//h3[contains(@class, 'JobStatus-title')]/span")		public WebElement name;
	@FindBy(className = "JobStatus-caret")									public WebElement caret;
	@FindBy(className = "JobStatus-status")									public WebElement status;
	@FindBy(className = "JobStatus-removeToggle")							public WebElement forgetDiv;
	@FindBy(className = "JobStatus-removeWarning")							public WebElement warningDiv;
	@FindBy(css = ".JobStatus-removeToggle > button")						public WebElement forgetButton;
	@FindBy(css = ".JobStatus-removeWarning > button")						public WebElement confirmButton;
	@FindBy(css = ".JobStatus-removeWarning > button:nth-of-type(2)")		public WebElement cancelButton;
	@FindBy(css = "a[title=\"View on Map\"]")								public WebElement viewLink;
	/* @formatter:on */

	public WebElement jobStatusContainer;
	private WebDriver driver;

	/**
	 * Creates an instance for a single Job Status.
	 * <p>
	 * The parent must be the "JobStatus-root" CSS class or this Page will not be able to locate any elements.
	 * 
	 * @param parent
	 *            The "JobStatus-root" element in the DOM that contains this particular Job Status entry.
	 */
	public JobStatusPage(WebDriver driver, WebElement parent) {
		PageFactory.initElements(new SearchContextElementLocatorFactory(parent), this);
		this.driver = driver;
		jobStatusContainer = parent;
	}

	/**
	 * Gets the name of the Job, as the user entered it in the Create Job window
	 * 
	 * @return User-defined name of the Job
	 */
	public String getName() {
		return name.getText();
	}

	/**
	 * Gets the Status of the Job
	 * 
	 * @return The Job status
	 */
	public String getStatus() {
		return status.getText();
	}

	/**
	 * Waits until the Job is in a completed state, and then returns the Status. Completed states are Success, Error,
	 * Fail.
	 * <p>
	 * This will block the specified amount of time and then return the status of the job.
	 * 
	 * @param timeoutInSeconds
	 *            Time in seconds to wait for the job to complete.
	 * @return The status of the Job. Either Success, Error, or Fail.
	 */
	public String getStatusOnCompletion(long timeoutInSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
		wait.until(ExpectedConditions.or(ExpectedConditions.textToBePresentInElement(status, "Success"),
				ExpectedConditions.textToBePresentInElement(status, "Error"), ExpectedConditions.textToBePresentInElement(status, "Fail")));
		return status.getText();
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