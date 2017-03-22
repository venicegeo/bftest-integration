package bfui.test;

import org.junit.*;
import org.junit.rules.TestName;

import static org.junit.Assert.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import bfui.test.page.BfMainPage;
import bfui.test.page.GxLoginPage;
import bfui.test.util.Info;
import bfui.test.util.Reporter;
import bfui.test.util.SauceResultReporter;
import bfui.test.util.Utils;
import bfui.test.util.Info.Importance;

public class TestGeoAxis {
	private WebDriver driver;
	private WebDriverWait wait;
	private GxLoginPage gxLogin;
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
		gxLogin = new GxLoginPage(driver);
		bfMain = new BfMainPage(driver);

		// Navigate to BF:
		driver.get(baseUrl);
		driver.manage().window().maximize();
	}

	@After 
	public void tearDown() throws Exception {
		driver.quit();
	}

	@Test @Info(importance = Importance.HIGH)
	public void standard_login() throws Exception {
		// Click the GX link provided by BF, then log in through GX.

		bfMain.geoAxisLink.click();
		Utils.assertThatAfterWait("Should navigate away from BF", ExpectedConditions.not(ExpectedConditions.urlMatches(baseUrl)), wait);
		Thread.sleep(1000);
		gxLogin.loginToGeoAxis(username, password);
		bfMain.loginSongAndDance(baseUrl);
		Utils.assertThatAfterWait("Should navigate back to BF", ExpectedConditions.urlMatches(baseUrl), wait);
		assertTrue("Should be able to click after login", Utils.tryToClick(bfMain.jobsButton));
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
