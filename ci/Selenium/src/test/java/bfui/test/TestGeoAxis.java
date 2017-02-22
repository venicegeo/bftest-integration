package bfui.test;

import org.junit.*;
import static org.junit.Assert.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import bfui.test.Importance.Level;

public class TestGeoAxis {
	private WebDriver driver;
	private WebDriverWait wait;
	private GxLoginPage gxLogin;
	private BfMainPage bfMain;

	// Strings used:
	private String baseUrl = System.getenv("bf_url");
	private String username = System.getenv("bf_username");
	private String password = System.getenv("bf_password");
	private String driverPath = System.getenv("driver_path");
	private String browserPath = System.getenv("browser_path");

	@Rule
	public ImportanceReporter reporter = new ImportanceReporter();
	
	@Before
	public void setUp() throws Exception {
		System.out.println("Starting setUp - GX Login");
		driver = Utils.createWebDriver(browserPath, driverPath);
		wait = new WebDriverWait(driver, 5);
		gxLogin = new GxLoginPage(driver);
		bfMain = new BfMainPage(driver);

		// Navigate to BF:
		driver.get(baseUrl);
		driver.manage().window().maximize();
		System.out.println("SetUp Complete");
	}

	@After 
	public void tearDown() throws Exception {
		driver.quit();
	}

	@Test @Importance(level = Level.HIGH)
	public void standard_login() throws Exception {
		// Click the GX link provided by BF, then log in through GX.
		
		bfMain.geoAxisLink.click();
		Utils.assertThatAfterWait("Should navigate away from BF", ExpectedConditions.not(ExpectedConditions.urlMatches(baseUrl)), wait);
		gxLogin.loginToGeoAxis(username, password);
		bfMain.loginSongAndDance(baseUrl);
		Utils.assertThatAfterWait("Should navigate back to BF", ExpectedConditions.urlMatches(baseUrl), wait);
		assertTrue("Should be able to click after login", Utils.tryToClick(bfMain.jobsButton));
	}
	
	@Test @Importance(level = Level.LOW)
	public void attempt_bypass() throws Exception {
		// Lie to bf-ui, saying that you are logged in, when you are not.
		
		driver.get(baseUrl + "?logged_in=true");
		Utils.assertBecomesVisible("Session Expired Overlay Appears", bfMain.sessionExpiredOverlay, wait);
		Utils.tryToClick(bfMain.jobsButton);
		assertFalse("Should not navigate to jobs", driver.getCurrentUrl().contains("job"));
	}

	@Test @Importance(level = Level.LOW)
	public void click_behind_login() throws Exception {
		// Try to click a button without being logged in.
		Utils.tryToClick(bfMain.jobsButton);
		assertFalse("Should not navigate to jobs", driver.getCurrentUrl().contains("job"));
	}
}
