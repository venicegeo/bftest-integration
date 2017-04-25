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

public class BfSingleJobPage {
	public WebElement thisWindow;

	@FindBy(className = "JobStatus-title")				public WebElement name;
	@FindBy(className = "JobStatus-removeToggle")		public WebElement forgetDiv;
	@FindBy(className = "JobStatus-removeWarning")		public WebElement warningDiv;
	@FindBy(css = "a[title=\"View on Map\"]")			public WebElement viewLink;
	@FindBy(css = "a[title=\"Download\"]")				public WebElement downloadLink;
	
	private SearchContextElementLocatorFactory findByParentFactory;

	public  BfSingleJobPage(WebElement parent) {
		findByParentFactory = new SearchContextElementLocatorFactory(parent);
		PageFactory.initElements(findByParentFactory, this);
		thisWindow = parent;
	}
	
	public String getName() {
		return name.getText();
	}
	
	public WebElement forgetButton() {
		return forgetDiv.findElement(By.tagName("button"));
	}
	
	public WebElement confirmButton() {
		return warningDiv.findElement(By.xpath("button[contains(text(),'Remove this Job')]"));
	}
	
	public WebElement cancelButton() {
		return warningDiv.findElement(By.xpath("button[contains(text(),'Cancel')]"));
	}
	
	
}