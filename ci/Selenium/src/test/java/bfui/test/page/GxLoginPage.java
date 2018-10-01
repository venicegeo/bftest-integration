package bfui.test.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class GxLoginPage {
	
	@FindBy(xpath = "//*[@id='authmechlinks']/div/ul/li[4]/a")	public WebElement disadvantagedLink;
	@FindBy(id = "username")									public WebElement userField;
	@FindBy(id = "password")									public WebElement pwField;
	@FindBy(id = "formNotify")									public WebElement notificationToContinue;
	@FindBy(css = "input[type=submit]")							public WebElement submitButton;
	
	public GxLoginPage(WebDriver driver) {
		PageFactory.initElements(driver, this);
	}
	
	public void login(String username, String password) throws InterruptedException {
		disadvantagedLink.click();
		userField.sendKeys(username);
		pwField.sendKeys(password);
		submitButton.click();
		Thread.sleep(1000);
		if (notificationToContinue.isDisplayed()) {
			notificationToContinue.click();
		}
	}
	
}