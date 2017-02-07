package bfui.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class BfSingleJobPage {
	WebElement thisWindow;

	@FindBy(xpath = "//h3[contains(@class, 'JobStatus-title')			]/child::span")			public WebElement name;
	@FindBy(xpath = "//div[contains(@class, 'JobStatus-removeToggle')	]/child::button")		public WebElement forgetButton;
	@FindBy(xpath = "//div[contains(@class, 'JobStatus-removeWarning')	]/child::button")		public WebElement confirmButton;
	@FindBy(xpath = "(//div[contains(@class, 'JobStatus-removeWarning')	]/child::button)[2]")	public WebElement cancelButton;
	@FindBy(className = "JobStatus-removeWarning")												public WebElement forgetWarning;
	@FindBy(css = "a[title=\"View on Map\"]")													public WebElement viewLink;
	@FindBy(css = "a[title=\"Download\"]")														public WebElement downloadLink;
	
	private SearchContextElementLocatorFactory findByParentFactory;

	public  BfSingleJobPage(WebElement parent) {
		findByParentFactory = new SearchContextElementLocatorFactory(parent);
		PageFactory.initElements(findByParentFactory, this);
		thisWindow = parent;
	}
	
	public String getName() {
		return name.getText();
	}
	
	
	
}