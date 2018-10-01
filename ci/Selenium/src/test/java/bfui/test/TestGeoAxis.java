package bfui.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import bfui.test.page.GxLoginPage;
import bfui.test.page.LogoutPage;
import bfui.test.page.MainPage;
import bfui.test.util.Info;
import bfui.test.util.Info.Importance;
import bfui.test.util.Reporter;
import bfui.test.util.Utils;

public class TestGeoAxis {
	private WebDriver driver;
	private WebDriverWait wait;
	private MainPage bfMain;

	// Strings used:
	private String baseUrl = System.getenv("bf_url");
	private String username = System.getenv("bf_username");
	private String password = System.getenv("bf_password");
	@Rule
	public Reporter reporter = new Reporter();
	@Rule
	public TestName name = new TestName();

	@Before
	public void setUp() throws Exception {
		driver = Utils.getChromeRemoteDriver();
		driver.get(baseUrl);
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
	}

	@Test
	@Info(importance = Importance.HIGH)
	public void standard_login_logout() {
		MainPage mainPage = new MainPage(driver);
		// Check that the consent banner is present and contains "Consent".
		assertTrue("Consent banner should contain 'consent' text", mainPage.getConsentBannerText().toUpperCase().contains("CONSENT"));
		// Login via Disadvantaged
		GxLoginPage loginPage = mainPage.beginLogin();
		mainPage = loginPage.loginDisadvantaged(username, password, mainPage);
		// Ensure the Cookie has been populated and login was successful
		Cookie apiKeyCookie = mainPage.getApiKeyCookie();
		assertNotNull("Login cookie is present", apiKeyCookie);
		assertTrue("Login cookie is valid GUID",
				apiKeyCookie.getValue().matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"));
		// Logout
		LogoutPage logoutPage = mainPage.logout();
		assertEquals("Logout text is displayed", logoutPage.getLogoutMessage(), LogoutPage.LOGOUT_MESSAGE);
		assertTrue("Logout redirect occurs", mainPage.getCurrentURL().contains("logout"));
	}

	@Test
	@Info(importance = Importance.LOW)
	public void session_expired() throws Exception {
		driver.get(baseUrl + "?logged_in=true");
		Utils.assertBecomesVisible("Session Expired Overlay Appears", bfMain.sessionExpiredOverlay, wait);
		Utils.tryToClick(bfMain.jobsButton);
		assertFalse("Should not navigate to jobs", driver.getCurrentUrl().contains("job"));
	}

	@Test
	@Info(importance = Importance.LOW)
	public void click_behind_login() throws Exception {
		// Try to click a button without being logged in.
		Utils.tryToClick(bfMain.jobsButton);
		assertFalse("Should not navigate to jobs", driver.getCurrentUrl().contains("job"));
	}
}
