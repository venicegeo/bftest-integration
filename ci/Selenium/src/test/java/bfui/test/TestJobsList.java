package bfui.test;

import static org.junit.Assert.*;

import java.awt.Robot;
import java.awt.geom.Point2D;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestJobsList {
	private WebDriver driver;
	private Actions actions;
	private WebDriverWait wait;
	private BfMainPage bfMain;
	private BfJobsWindowPage jobsWindow;
	private BfSingleJobPage testJob;
	private GxLoginPage gxLogin;
	private Robot robot;
	
	// Strings used:
	private String baseUrl = System.getenv("bf_url");
	private String gxUrl = System.getenv("GX_url");
	private String username = System.getenv("bf_username");
	private String password = System.getenv("bf_password");
	private String driverPath = System.getenv("driver_path");
	private String browserPath = System.getenv("browser_path");
	
	@Before
	public void setUp() throws Exception {
		System.out.println("Starting setUp - Jobs List");
		// Setup Browser:
		driver = Utils.createWebDriver(browserPath, driverPath);
		wait = new WebDriverWait(driver, 5);
		gxLogin = new GxLoginPage(driver);
		bfMain = new BfMainPage(driver);
		actions = new Actions(driver);

		// Log in to GX:
		driver.get(gxUrl);
		gxLogin.loginToGeoAxis(username, password);
		driver.manage().window().maximize();
		
		// Make sure job is present in jobs list:
		driver.get("https://bf-api.stage.geointservices.io/v0/job/d2de0718-4374-43e4-82cd-70fbc2a5a7a4");
		driver.get(baseUrl);
		
		// Open Job Window:
		bfMain.jobsButton.click();
		jobsWindow = bfMain.jobsWindow();
		testJob = jobsWindow.singleJob("ForJobTesting");
		//d2de0718-4374-43e4-82cd-70fbc2a5a7a4
		System.out.println("SetUp complete");
	}

	@After
	public void tearDown() {
		System.out.println("Starting tearDown");
		driver.quit();
		System.out.println("TearDown complete");
	}
	
	@Test
	public void viewOnMap() {
		testJob.viewLink.click();
		Utils.assertPointInRange(bfMain.getCoords(), new Point2D.Double(0, 38), 5);
	}
	
	@Test
	public void downloadResult() {
		assertEquals("Download path should appear after click", null, testJob.downloadLink.getAttribute("href"));
		testJob.downloadLink.click();
		Utils.assertBecomesVisible(testJob.downloadLink, wait);
		Assert.assertTrue("Download path should appear after click", testJob.downloadLink.getAttribute("href").contains("blob"));
	}
	
	@Test
	public void forgetJob() {
		testJob.thisWindow.click();
		Utils.assertBecomesVisible("Job opens to reveal forget button", testJob.forgetButton, wait);
		testJob.forgetButton.click();
		Utils.assertBecomesVisible("Confirmation screen appears", testJob.confirmButton, wait);
		testJob.cancelButton.click();
		assertFalse("Can't click confirm after cancel", Utils.tryToClick(testJob.confirmButton));
		testJob.forgetButton.click();
		Utils.assertBecomesVisible("Confirmation screen appears again", testJob.confirmButton, wait);
		testJob.confirmButton.click();
		Utils.assertBecomesInvisible("Job was removed from list", testJob.thisWindow, wait);
		driver.get(driver.getCurrentUrl()); //Reload page
		assertNull(bfMain.jobsWindow().singleJob("ForJobTesting"));
	}
	
	@Test
	public void bypass_confirmation() throws InterruptedException {
		Utils.ignoreOnInt();
		
		assertFalse("Should not be able to click 'confirm'", Utils.tryToClick(testJob.confirmButton));
		
		// Tab through options:
		testJob.thisWindow.click();
		actions.sendKeys(Keys.TAB, Keys.TAB, Keys.ENTER).build().perform();
		Thread.sleep(1000);
		assertTrue("Job should not be removed (Bug #5637)", Utils.checkExists(testJob.thisWindow));
	}
	

}
