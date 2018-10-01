package bfui.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import bfui.test.page.GxLoginPage;
import bfui.test.page.MainPage;
import bfui.test.util.Info;
import bfui.test.util.Info.Importance;
import bfui.test.util.Reporter;
//import bfui.test.util.SauceResultReporter;
import bfui.test.util.Utils;

public class TestGeoAxis {
	private WebDriver driver;
	private WebDriverWait wait;
	private MainPage bfMain;
	private GxLoginPage gxLogin;

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
		wait = new WebDriverWait(driver, 5);
		gxLogin = new GxLoginPage(driver);

		bfMain = new MainPage(driver, wait);

		// Navigate to BF:
		driver.get(baseUrl);
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
	}

	@Test
	@Info(importance = Importance.HIGH)
	public void standard_login_logout() throws Exception {
		// Check that the consent banner is present and contains "Consent".
		assertTrue("Consent banner should contain 'consent'", bfMain.consentBanner.getText().toUpperCase().contains("CONSENT"));
		// Click the GX link provided by BF, then log in through GX:

		bfMain.geoAxisLink.click();
		Thread.sleep(5000);
		gxLogin.login(username, password);
		Utils.assertThatAfterWait("Should navigate back to BF", ExpectedConditions.urlMatches(baseUrl), wait);
		assertTrue("Should be able to click after login", Utils.tryToClick(bfMain.jobsButton));

		// Now, logout:
		bfMain.logoutButton.click();
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
