package bfui.test.page;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import bfui.test.util.SearchContextElementLocatorFactory;
import bfui.test.util.Utils;

public class BfCreateJobWindowPage {
	WebElement thisWindow;

	@FindBy(tagName = "ul")																					public WebElement scrollableWindow;
	@FindBy(className = "CreateJob-placeholder")															public WebElement instructionText;
	@FindBy(className = "CatalogSearchCriteria-invalidDates")												public List<WebElement> invalidDateText;
	@FindBy(className = "CatalogSearchCriteria-value")														public WebElement cloudText;
	@FindBy(className = "ImagerySearch-loadingMask")														public WebElement loadingMask;
	@FindBy(className = "ImagerySearch-errorMessage")														public WebElement errorMessage;
	@FindBy(className = "CatalogSearchCriteria-clearBbox")													public WebElement clearButton;
	@FindBy(xpath = "//label[contains(@class, 'CatalogSearchCriteria-apiKey')			]/child::input")	public WebElement apiKeyEntry;
	@FindBy(xpath = "//label[contains(@class, 'CatalogSearchCriteria-captureDateFrom')	]/child::input")	public WebElement fromDateEntry;
	@FindBy(xpath = "//label[contains(@class, 'CatalogSearchCriteria-captureDateTo')	]/child::input")	public WebElement toDateEntry;
	@FindBy(xpath = "//label[contains(@class, 'CatalogSearchCriteria-cloudCover')		]/child::input")	public WebElement cloudSlider;
	@FindBy(xpath = "//label[contains(@class, 'CatalogSearchCriteria-source')			]/child::select")	public WebElement sourceDropdown;
	@FindBy(xpath = "//div[contains(@class, 'ImagerySearch-errorMessage')				]/child::p")		public WebElement errorMessageDescription;
	@FindBy(xpath = "//div[contains(@class, 'AlgorithmList-root')						]/child::ul/li")	public List<WebElement> algorithms;
	@FindBy(css = "button[type=submit]")																	public WebElement submitButton;
	
	public By algorithmButtonLocator = By.className("Algorithm-startButton");
	
	private SearchContextElementLocatorFactory findByParentFactory;

	public  BfCreateJobWindowPage(WebElement parent) {
		findByParentFactory = new SearchContextElementLocatorFactory(parent);
		PageFactory.initElements(findByParentFactory, this);
		thisWindow = parent;
	}
	
	public void scroll(WebDriver driver, int x, int y) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].scrollTop+=arguments[1]", scrollableWindow, y);
		jse.executeScript("arguments[0].scrollLeft+=arguments[1]", scrollableWindow, x);
	}
	
	public void selectSource(String selection) {
		Select source = new Select(sourceDropdown);
		source.selectByValue(selection);
	}
	
	public WebElement algorithmButton(String name) {
		for (WebElement algorithm : algorithms) {
			WebElement algorithmText = algorithm.findElement(By.className("Algorithm-name"));
			if (algorithmText.getText().equals(name)) {
				return algorithm.findElement(algorithmButtonLocator);
			}
		}
		Assert.fail("The algorithm " + name + " should be found");
		return null;
	}
	
	public void enterDates(String fromDate, String toDate) {
		fromDateEntry.sendKeys(Keys.chord(Keys.CONTROL, "a"), fromDate);
		toDateEntry.sendKeys(Keys.chord(Keys.CONTROL, "a"), toDate);
		// Previously used the .clear() method, but that sometimes did not work.
	}
	
	public void enterKey(String key) {
		apiKeyEntry.sendKeys(Keys.chord(Keys.CONTROL, "a"), key);
		// Previously used the .clear() method, but that sometimes did not work.
	}
	
	public int cloudSliderValue() {
		return Integer.parseInt(cloudSlider.getAttribute("value"));
	}
	
	public Boolean checkDateWarningContains(String checkString) {
		Boolean present = false;
		for (WebElement line : invalidDateText) {
			present = present || line.getText().contains(checkString);
		}
		return present;
	}
	
	// Wait for the search complete.  Wait for the loading mask to appear.  If it does, then wait for it to disappear
	public Boolean waitForCompleteSearch(int timeout) throws InterruptedException {
		int i = 0;
		while (Utils.checkExists(loadingMask)) {
			if (i < timeout) {
				Thread.sleep(1000);
				i++;
			} else {
				return false;
			}
		}
		return true;
	}
}