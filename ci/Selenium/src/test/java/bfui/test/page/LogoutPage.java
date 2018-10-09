package bfui.test.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import bfui.test.page.core.PageObject;

/**
 * The redirect page that the client navigates to upon successful logout
 */
public class LogoutPage extends PageObject {
	/* @formatter:off */
	@FindBy(id = "title")	private WebElement title;
	/* @formatter:on */

	public static final String LOGOUT_MESSAGE = "User logged out successfully";

	public LogoutPage(WebDriver driver) {
		super(driver);
	}

	/**
	 * Returns the content of the logout text
	 * 
	 * @return The logout text
	 */
	public String getLogoutMessage() {
		return title.getText();
	}
}
