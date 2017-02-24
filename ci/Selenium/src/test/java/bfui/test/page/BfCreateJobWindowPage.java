package bfui.test.page;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import bfui.test.util.SearchContextElementLocatorFactory;

public class BfCreateJobWindowPage {
	WebElement thisWindow;

	@FindBy(className = "CreateJob-placeholder")															public WebElement instructionText;
	@FindBy(className = "CatalogSearchCriteria-invalidDates")												public WebElement invalidDateText;
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
	
	private SearchContextElementLocatorFactory findByParentFactory;

	public  BfCreateJobWindowPage(WebElement parent) {
		findByParentFactory = new SearchContextElementLocatorFactory(parent);
		PageFactory.initElements(findByParentFactory, this);
		thisWindow = parent;
	}
	
	public void selectSource(String selection) {
		Select source = new Select(sourceDropdown);
		source.selectByValue(selection);
	}
	
	public WebElement algorithmButton(String name) {
		for (WebElement algorithm : algorithms) {
			WebElement algorithmText = algorithm.findElement(By.className("Algorithm-name"));
			if (algorithmText.getText().equals(name)) {
				return algorithm;
			}
		}
		Assert.fail("The algorithm " + name + " should be found");
		return null;
	}
	
	public void enterDates(String fromDate, String toDate) {
		fromDateEntry.clear();
		fromDateEntry.sendKeys(fromDate);
		toDateEntry.clear();
		toDateEntry.sendKeys(toDate);
		submitButton.click();
	}
	
	public int cloudSliderValue() {
		return Integer.parseInt(cloudSlider.getAttribute("value"));
	}
	
}