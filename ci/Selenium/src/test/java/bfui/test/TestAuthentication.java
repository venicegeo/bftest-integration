package bfui.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import bfui.test.page.GxLoginPage;
import bfui.test.page.LogoutPage;
import bfui.test.page.MainPage;
import bfui.test.util.Info;
import bfui.test.util.Info.Importance;
import bfui.test.util.Reporter;
import bfui.test.util.Utils;

/**
 * Tests Login and Logout functionality
 */
public class TestAuthentication {
	private final String BASE_URL = System.getenv("bf_url");
	private final String USERNAME = System.getenv("bf_username");
	private final String PASSWORD = System.getenv("bf_password");

	@Rule
	public Reporter reporter = new Reporter();
	@Rule
	public TestName name = new TestName();

	private WebDriver driver;
	private MainPage mainPage;

	@Before
	public void setUp() throws Exception {
		driver = Utils.getChromeRemoteDriver();
		mainPage = new MainPage(driver);
		driver.get(BASE_URL);
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
	}

	@Test
	@Info(importance = Importance.MEDIUM)
	public void url_jack_login() throws Exception {
		// Insert the logged_in param before actually logging in
		driver.get(BASE_URL + "?logged_in=true");
		// Jobs should fail to display due to not being clickable
		assertTrue("Forcing logged_in parameter fails", mainPage.isLoggedOut());
	}

	@Test(expected = WebDriverException.class)
	@Info(importance = Importance.MEDIUM)
	public void click_behind_login() throws Exception {
		// Try to click a button without being logged in.
		mainPage.navigateJobsPage();
		assertFalse("No interaction with map before login", mainPage.getCurrentURL().contains("job"));
	}

	@Test
	@Info(importance = Importance.HIGH)
	public void standard_login_logout() throws InterruptedException {
		// Check that the consent banner is present and contains "Consent".
		assertTrue("Consent banner should contain 'consent' text", mainPage.getConsentBannerText().toUpperCase().contains("CONSENT"));
		// Login via Disadvantaged
		GxLoginPage loginPage = mainPage.beginLogin();
		mainPage = loginPage.loginDisadvantaged(USERNAME, PASSWORD, mainPage);
		// Ensure the Cookie has been populated and login was successful
		Cookie apiKeyCookie = mainPage.getApiKeyCookie();
		assertNotNull("Login cookie is present", apiKeyCookie);
		assertTrue("Login cookie is valid GUID",
				apiKeyCookie.getValue().matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"));
		// Logout. The OAuth is crazy here. Sometimes the provider fulfills the redirect, sometimes it doesn't. So we'll
		// have to check if it has redirected and if so, check the title. If not, we'll at least ensure the cookie was
		// deleted from the session.
		LogoutPage logoutPage = mainPage.logout();
		if (mainPage.getCurrentURL().contains("logout")) {
			assertEquals("Logout text is displayed", logoutPage.getLogoutMessage(), LogoutPage.LOGOUT_MESSAGE);
		} else {
			assertTrue("Session closed after logout", mainPage.isLoggedOut());
		}
	}
}
