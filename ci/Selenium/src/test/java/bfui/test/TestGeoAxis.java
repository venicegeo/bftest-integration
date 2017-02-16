package bfui.test;

import org.junit.*;
import static org.junit.Assert.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
	// Allows use of @Importance(level = Level.[LOW, MEDIUM, or HIGH])
	// This will display a list of failing methods at the end of the test suite.
	//  The failing methods will be marked by their importance.
	
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

	@Test
	public void standard_login() throws Exception {
		bfMain.geoAxisLink.click();
		Utils.assertThatAfterWait("Should navigate away from BF", ExpectedConditions.not(ExpectedConditions.urlMatches(baseUrl)), wait);
		gxLogin.loginToGeoAxis(username, password);
		Utils.assertThatAfterWait("Should navigate back to BF", ExpectedConditions.urlMatches(baseUrl), wait);
	}
	
	@Test
	public void attempt_bypass() throws Exception {
		Utils.ignoreOnInt();
		
		driver.get(baseUrl + "?logged_in=true");
		Utils.tryToClick(bfMain.jobsButton);
		assertFalse("Should not navigate to jobs", driver.getCurrentUrl().contains("job"));
	}

	@Test
	public void click_behind_login() throws Exception {
		Utils.tryToClick(bfMain.jobsButton);
		assertFalse("Should not navigate to jobs", driver.getCurrentUrl().contains("job"));
	}

	@After 
	public void tearDown() throws Exception {
		System.out.println("Starting tearDown");
		driver.quit();
		System.out.println("TearDown Complete");
	}

}
