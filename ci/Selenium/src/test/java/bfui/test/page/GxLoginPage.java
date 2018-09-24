package bfui.test.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class GxLoginPage extends LoginPage {
	
	@FindBy(xpath = "//*[@id='authmechlinks']/div/ul/li[4]/a")	public WebElement disadvantagedLink;
	@FindBy(id = "username")									public WebElement userField;
	@FindBy(id = "password")									public WebElement pwField;
	@FindBy(id = "formNotify")									public WebElement notificationToContinue;
	@FindBy(css = "input[type=submit]")							public WebElement submitButton;
	
	public GxLoginPage(WebDriver driver) {
		super(driver);
	}
	
	@Override
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