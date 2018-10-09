package bfui.test.page;

import java.net.URI;
import java.net.URISyntaxException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
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
	@FindBy(xpath = "//h3[contains(@class, 'JobStatus-title')]/span")				public WebElement name;
	@FindBy(className = "JobStatus-caret")											public WebElement caret;
	@FindBy(className = "JobStatus-status")											public WebElement status;
	@FindBy(className = "JobStatus-removeToggle")									public WebElement forgetDiv;
	@FindBy(className = "JobStatus-removeWarning")									public WebElement warningDiv;
	@FindBy(className = "JobStatus-download")										public WebElement downloadButton;
	@FindBy(xpath = "//div[contains(@class, 'JobStatus-removeToggle')]/button")		public WebElement forgetButton;
	@FindBy(xpath = "//div[contains(@class, 'JobStatus-removeWarning')]/button[1]")	public WebElement confirmButton;
	@FindBy(xpath = "//div[contains(@class, 'JobStatus-removeWarning')]/button[2]")	public WebElement cancelButton;
	@FindBy(css = "a[title=\"View on Map\"]")										public WebElement viewLink;
	@FindBy(css = "a[title=\"Download GeoJSON\"]")									public WebElement downloadGeoJSON;
	@FindBy(css = "a[title=\"Download GeoPackage\"]")								public WebElement downloadGeoPackage;
	@FindBy(css = "a[title=\"Download Shapefile\"]")								public WebElement downloadShapefile;
	/* @formatter:on */

	private WebElement jobStatusRoot;
	private WebDriver driver;
	private Actions actions;

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
		this.actions = new Actions(driver);
		this.jobStatusRoot = parent;
	}

	/**
	 * Gets the Root element of this Job Status entry
	 * 
	 * @return Root element "JobStatus-root"
	 */
	public WebElement getRoot() {
		return jobStatusRoot;
	}

	/**
	 * Scrolls this particular Job Status list item into the viewport
	 */
	public void scrollIntoView() {
		actions.moveToElement(getRoot()).pause(500).build().perform(); // Pause for scroll animation
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

	/**
	 * Zooms to the current job location on the map.
	 * <p>
	 * Adds a slight delay to allow for map zoom/panning.
	 */
	public void zoomTo() {
		actions.click(viewLink).pause(500).build().perform();
	}

	/**
	 * Gets the GeoJSON download link
	 * 
	 * @return GeoJSON download link
	 */
	public String getGeoJsonLink() throws URISyntaxException {
		actions.click(downloadButton).pause(50).build().perform();
		return getDownloadLink("geojson");
	}

	/**
	 * Gets the GeoPackage download link
	 * 
	 * @return GeoPackage download link
	 */
	public String getGeoPackageLink() throws URISyntaxException {
		actions.click(downloadButton).pause(50).build().perform();
		return getDownloadLink("gpkg");
	}

	/**
	 * Gets the Shapefile download link
	 * 
	 * @return Shapefile download link
	 */
	public String getShapefileLink() throws URISyntaxException {
		actions.click(downloadButton).pause(50).build().perform();
		return getDownloadLink("shp");
	}

	/**
	 * Gets the base Beachfront API Url to append the download file link to.
	 * <p>
	 * Because of the clever way Beachfront downloads files, it is impossible to get that raw file link in the DOM.
	 * Thus, this URL must be constructed here.
	 * 
	 * @param The
	 *            extension of the file to download
	 * @return BF API Base Url
	 */
	private String getDownloadLink(String extension) throws URISyntaxException {
		// Click the name to get the GUID to ensure that the URL contains the Job ID GUID
		name.click();
		// Build the full URL since it is not present in the DOM at all.
		URI uri = new URI(driver.getCurrentUrl());
		String[] queryParts = uri.getQuery().split("=");
		String jobId = queryParts[1];
		String bfApi = uri.getAuthority().replaceAll("beachfront", "bf-api");
		return String.format("https://%s/job/%s.%s", bfApi, jobId, extension);
	}

	/**
	 * Forgets this job, thus removing it from the list.
	 */
	public void forgetJob() {
		if (!isExpanded()) { // Expand if necessary
			actions.click(caret).pause(100).build().perform(); // pause for animation
		}
		forgetButton.click();
		confirmButton.click();
	}

	/**
	 * Detects if the Job Status is expanded or not
	 * 
	 * @return True if expanded, false if not
	 */
	private boolean isExpanded() {
		String classes = jobStatusRoot.getAttribute("class");
		return classes.contains("JobStatus-expanded");
	}
}