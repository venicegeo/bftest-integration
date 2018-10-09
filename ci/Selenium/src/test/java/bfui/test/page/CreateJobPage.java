package bfui.test.page;

import java.security.SecureRandom;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import bfui.test.page.core.PageObject;

/**
 * The Create Job dialog, which contains the logic for searching for imagery and submitting a new Beachfront job
 */
public class CreateJobPage extends PageObject {
	/* @formatter:off */
	@FindBy(tagName = "ul")																					private WebElement scrollableWindow;
	@FindBy(className = "NewJobDetails-computeMaskInput")													private WebElement computeMaskCheckbox;
	@FindBy(className = "NewJobDetails-nameInput") 														    private WebElement jobNameInput;
	@FindBy(className = "CreateJob-placeholder")															private WebElement instructionText;
	@FindBy(className = "ImagerySearch-loadingMask")														private WebElement loadingMask;
	@FindBy(className = "ImagerySearch-errorMessage")														private WebElement errorMessage;
	@FindBy(className = "CatalogSearchCriteria-clearBbox")													private WebElement clearButton;
	@FindBy(className = "Algorithm-startButton")															private WebElement algorithmButton;
	@FindBy(className = "CatalogSearchCriteria-minimap")													private WebElement minimapContainer;
	@FindBy(xpath = "//div[contains(@class, 'ImagerySearchList-results')				]/table")			private WebElement resultsTable;
	@FindBy(xpath = "//label[contains(@class, 'CatalogSearchCriteria-apiKey')			]/child::input")	private WebElement apiKeyEntry;
	@FindBy(xpath = "//label[contains(@class, 'CatalogSearchCriteria-captureDateFrom')	]/child::input")	private WebElement fromDateEntry;
	@FindBy(xpath = "//label[contains(@class, 'CatalogSearchCriteria-captureDateTo')	]/child::input")	private WebElement toDateEntry;
	@FindBy(xpath = "//label[contains(@class, 'CatalogSearchCriteria-cloudCover')		]/child::input")	private WebElement cloudSlider;
	@FindBy(xpath = "//label[contains(@class, 'CatalogSearchCriteria-source')			]/child::select")	private WebElement sourceDropdown;
	@FindBy(css = "button[type=submit]")																	private WebElement submitButton;
	/* @formatter:on */

	private Actions actions;

	public CreateJobPage(WebDriver driver) {
		super(driver);
		actions = new Actions(driver);
	}

	/**
	 * Selects a data type source in the drop-down window for searching for Imagery.
	 * 
	 * @param selection
	 *            The Data Type source for job creation
	 */
	public void selectSource(String selection) {
		WebDriverWait wait = new WebDriverWait(driver, 1);
		wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(sourceDropdown, By.tagName("option")));
		Select source = new Select(sourceDropdown);
		source.selectByValue(selection);
	}

	/**
	 * Clicks on the Run Algorithm button
	 */
	public JobsPage runAlgorithm() {
		// Scroll to the algorithm button and click
		actions.moveToElement(algorithmButton);
		actions.click(algorithmButton).pause(500).build().perform();
		// Ensure no errors occurred
		WebDriverWait wait = new WebDriverWait(driver, 3);
		// Ensure that the Jobs page is enabled, indicating a successful job submission
		wait.until(ExpectedConditions.urlContains("jobs?"));
		return new JobsPage(driver);
	}

	/**
	 * Detects if the search bounding box minimap is displayed or not
	 * 
	 * @return True if the minimap is displayed, false if not
	 */
	public boolean isMinimapDisplayed() {
		return minimapContainer.isDisplayed();
	}

	/**
	 * Enters the specified dates in the to and from fields
	 * 
	 * @param fromDate
	 *            The start date
	 * @param toDate
	 *            The end date
	 */
	public void enterDates(String fromDate, String toDate) {
		fromDateEntry.clear();
		fromDateEntry.sendKeys(fromDate);
		toDateEntry.clear();
		toDateEntry.sendKeys(toDate);
	}

	/**
	 * Sets the value of the compute mask checkbox input
	 * 
	 * @param value
	 *            True to enable compute mask, false to disable
	 */
	public void setComputeMask(boolean value) {
		boolean currentlySelected = computeMaskCheckbox.isSelected();
		if (currentlySelected != value) {
			computeMaskCheckbox.click();
		}
	}

	/**
	 * Sets the Job Name entry field
	 * 
	 * @param jobName
	 *            The name to set
	 */
	public void setJobName(String jobName) {
		jobNameInput.clear();
		jobNameInput.sendKeys(jobName);
	}

	/**
	 * Gets the search dates. From and to
	 * 
	 * @return Array containing two entries. The first entry is the start date, the second entry is the end date.
	 */
	public String[] getSearchDates() {
		return new String[] { fromDateEntry.getText(), toDateEntry.getText() };
	}

	/**
	 * Enters the API Key in the API key input
	 * 
	 * @param key
	 *            The API Key
	 */
	public void enterKey(String key) {
		apiKeyEntry.clear();
		apiKeyEntry.sendKeys(key);
	}

	/**
	 * Returns the status of the instruction text when the Create Job window is first displayed
	 * 
	 * @return True if the instruction text is displayed, false if not
	 */
	public boolean isInstructionTextVisible() {
		return instructionText.isDisplayed();
	}

	/**
	 * Clicks the submit button to search for imagery.
	 */
	public void searchForImagery() {
		submitButton.click();
	}

	/**
	 * Blocks until the search for imagery has completed.
	 * <p>
	 * As an implicit assertion, searches should not exceed the duration of the wait.
	 */
	public void waitForSearchToComplete() {
		WebDriverWait wait = new WebDriverWait(driver, 15);
		wait.until(ExpectedConditions.visibilityOf(resultsTable));
	}

	/**
	 * Checks if the search button is enabled or not, thus indicating search parameters are successfully filled out
	 * 
	 * @return True if search is enabled, false if not
	 */
	public boolean isSearchEnabled() {
		return submitButton.isEnabled();
	}

	/**
	 * Upon successful search for imagery, this will select a random entry from the search results table.
	 */
	public void selectRandomJobResult() {
		// Get all results
		List<WebElement> results = resultsTable.findElements(By.tagName("tr"));
		// Click a random result
		SecureRandom random = new SecureRandom();
		WebElement randomJobRow = results.get(random.nextInt(results.size()));
		// Scroll to the row and click
		actions.moveToElement(randomJobRow);
		randomJobRow.click();
	}

	/**
	 * Checks if the Search Mask is applied or not; corresponding with an ongoing search
	 * 
	 * @return True if searching, false if not
	 */
	public boolean isSearching() {
		return loadingMask.isDisplayed();
	}

	/**
	 * Sets the Cloud Cover slider to the middle value, which corresponds with a value of 50%
	 */
	public void setCloudCoverToMiddle() {
		actions.moveToElement(cloudSlider).click().build().perform();
	}

	/**
	 * Returns the cloud cover % slider value
	 * 
	 * @return Current Cloud cover %
	 */
	public int cloudSliderValue() {
		return Integer.parseInt(cloudSlider.getAttribute("value"));
	}

}