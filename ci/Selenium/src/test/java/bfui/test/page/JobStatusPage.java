package bfui.test.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import bfui.test.util.SearchContextElementLocatorFactory;

public class JobStatusPage {
	public WebElement thisWindow;

	@FindBy(className = "JobStatus-title")				public WebElement name;
	@FindBy(className = "JobStatus-caret")				public WebElement caret;
	@FindBy(className = "JobStatus-removeToggle")		public WebElement forgetDiv;
	@FindBy(css = ".JobStatus-removeToggle > button")	public WebElement forgetButton;
	@FindBy(css = ".JobStatus-removeWarning > button")	public WebElement confirmButton;
	@FindBy(css = ".JobStatus-removeWarning > button:nth-of-type(2)")				public WebElement cancelButton;
	@FindBy(className = "JobStatus-removeWarning")		public WebElement warningDiv;
	@FindBy(css = "a[title=\"View on Map\"]")			public WebElement viewLink;
	
	private SearchContextElementLocatorFactory findByParentFactory;

	public  JobStatusPage(WebElement parent) {
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
	public WebElement downloadButton() {
		return thisWindow.findElement(By.className("JobStatus-download"));
	}
	public WebElement downloadLinkGeojson() {
		return thisWindow.findElement(By.cssSelector("a[title=\"Download GeoJSON\"]"));
	}
	public WebElement downloadLinkGeopkg() {
		return thisWindow.findElement(By.cssSelector("a[title=\"Download GeoPackage\"]"));
	}
	public WebElement downloadLinkShapefile() {
		return thisWindow.findElement(By.cssSelector("a[title=\"Download Shapefile\"]"));
	}
	
	public WebElement confirmButton() {
		return warningDiv.findElement(By.xpath("button[contains(text(),'Remove this Job')]"));
	}
	
	public WebElement cancelButton() {
		return warningDiv.findElement(By.xpath("button[contains(text(),'Cancel')]"));
	}
	
	
}