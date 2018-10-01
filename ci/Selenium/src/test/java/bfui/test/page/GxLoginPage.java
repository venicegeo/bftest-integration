package bfui.test.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import bfui.test.page.core.PageObject;

/**
 * The OAuth-driven Login page, where credentials of various types can be used to authenticate.
 */
public class GxLoginPage extends PageObject {
	/* @formatter:off */
	@FindBy(xpath = "//*[@id='authmechlinks']/div/ul/li[4]/a")	public WebElement disadvantagedLink;
	@FindBy(id = "username")									public WebElement userField;
	@FindBy(id = "password")									public WebElement pwField;
	@FindBy(id = "formNotify")									public WebElement notificationToContinue;
	@FindBy(css = "input[type=submit]")							public WebElement submitButton;
	/* @formatter:on */

	public GxLoginPage(WebDriver driver) {
		super(driver);
	}

	/**
	 * Login with a disadvantaged username and password
	 * 
	 * @param username
	 *            The username
	 * @param password
	 *            The password
	 * @param mainPage
	 *            The Main page for OAuth redirection. Will re-use if provided a MainPage.
	 */
	public MainPage loginDisadvantaged(String username, String password, MainPage mainPage) {
		WebDriverWait wait = new WebDriverWait(driver, 2);
		wait.until(ExpectedConditions.elementToBeClickable(disadvantagedLink));
		disadvantagedLink.click();
		userField.sendKeys(username);
		pwField.sendKeys(password);
		submitButton.click();
		wait.until(ExpectedConditions.elementToBeClickable(notificationToContinue));
		notificationToContinue.click();
		// Wait until the redirect has succeeded back to Beachfront's page
		wait.until(ExpectedConditions.urlContains("beachfront"));
		return mainPage != null ? mainPage : new MainPage(driver);
	}

}