package bfui.test.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CoastlineLoginPage extends LoginPage {
	
	@FindBy(xpath = "//*[@id='authmechlinks']/div/ul/li[4]/a")	public WebElement disadvantagedLink;
	@FindBy(name = "username")									public WebElement userField;
	@FindBy(name = "password")									public WebElement pwField;
	@FindBy(tagName = "button")									public WebElement submitButton;
	
	public CoastlineLoginPage(WebDriver driver) {
		super(driver);
	}
	
	@Override
	public void login(String username, String password) {
		userField.sendKeys(username);
		pwField.sendKeys(password);
		submitButton.click();
	}
	
}