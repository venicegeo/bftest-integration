package bfui.test;

import org.junit.*;
import org.junit.rules.TestName;

import static org.junit.Assert.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import bfui.test.page.BfMainPage;
import bfui.test.page.CoastlineLoginPage;
import bfui.test.page.GxLoginPage;
import bfui.test.page.LoginPage;
import bfui.test.util.Info;
import bfui.test.util.Reporter;
import bfui.test.util.SauceResultReporter;
import bfui.test.util.Utils;
import bfui.test.util.Info.Importance;

public class TestGeoAxis {
	private WebDriver driver;
	private WebDriverWait wait;
	private LoginPage login;
	private BfMainPage bfMain;

	// Strings used:
	private String baseUrl = System.getenv("bf_url");
	private String username = System.getenv("bf_username");
	private String password = System.getenv("bf_password");
	private String space = System.getenv("space");
	private String browser = System.getenv("browser");
	
	@Rule
	public Reporter reporter = new Reporter("http://dashboard.venicegeo.io/cgi-bin/bf_ui_" + browser + "/" + space + "/load.pl");
	@Rule
	public TestName name = new TestName();
	@Rule
	public SauceResultReporter sauce = new SauceResultReporter();
	
	@Before
	public void setUp() throws Exception {
		driver = Utils.createSauceDriver(name.getMethodName());
		wait = new WebDriverWait(driver, 5);
		switch (space) {
			case "int": case "stage": case "prod":
				login = new GxLoginPage(driver);
				break;
			case "coastline":
				login = new CoastlineLoginPage(driver);
				break;
			default:
				throw new Exception("No Login page specified for , '" + space + "'.");
		}
	
		bfMain = new BfMainPage(driver, wait);

		// Navigate to BF:
		driver.get(baseUrl);
		driver.manage().window().maximize();
	}

	@After 
	public void tearDown() throws Exception {
		driver.quit();
	}

	@Test @Info(importance = Importance.HIGH)
	public void standard_login_logout() throws Exception {
		
		// Check that the consent banner is present and contains "Consent".
		assertTrue("Consent banner should contain 'consent'", bfMain.consentBanner.getText().toUpperCase().contains("CONSENT"));
		// Click the GX link provided by BF, then log in through GX:
		
		bfMain.geoAxisLink.click();
		Utils.assertThatAfterWait("Should navigate away from BF", ExpectedConditions.not(ExpectedConditions.urlMatches(baseUrl)), wait);
		Thread.sleep(1000);
		login.login(username, password);
		Utils.assertThatAfterWait("Should navigate back to BF", ExpectedConditions.urlMatches(baseUrl), wait);
		assertTrue("Should be able to click after login", Utils.tryToClick(bfMain.jobsButton));
		
		// Now, logout:
		bfMain.logoutButton.click();
//		Utils.assertBecomesVisible("Logged Out Overlay should appear", bfMain.loggedOutOverlay, wait);
//		bfMain.loggedOutOverlay.click();
//		Utils.assertBecomesVisible("Login Button should appear", bfMain.geoAxisLink, wait);
		
		// Now, BF will redirect to a GX single sign-out.
		Utils.assertThatAfterWait("Should navigate away from BF", ExpectedConditions.not(ExpectedConditions.urlMatches(baseUrl)), wait);
		Utils.assertThatAfterWait("Should navigate to a logout page", ExpectedConditions.urlMatches("logout"), wait);
		
	}
	
	@Test @Info(importance = Importance.LOW)
	public void session_expired() throws Exception {
		// Lie to bf-ui, saying that you are logged in, when you are not.
		
		driver.get(baseUrl + "?logged_in=true");
		Utils.assertBecomesVisible("Session Expired Overlay Appears", bfMain.sessionExpiredOverlay, wait);
		Utils.tryToClick(bfMain.jobsButton);
		assertFalse("Should not navigate to jobs", driver.getCurrentUrl().contains("job"));
	}

	@Test @Info(importance = Importance.LOW)
	public void click_behind_login() throws Exception {
		// Try to click a button without being logged in.
		Utils.tryToClick(bfMain.jobsButton);
		assertFalse("Should not navigate to jobs", driver.getCurrentUrl().contains("job"));
	}
}
