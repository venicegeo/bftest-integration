package bfui.test;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import bfui.test.Importance.Level;

public class TestJobsList {
	private WebDriver driver;
	private Actions actions;
	private WebDriverWait wait;
	private BfMainPage bfMain;
	private BfJobsWindowPage jobsWindow;
	private BfSingleJobPage testJob;
	private GxLoginPage gxLogin;
	
	// Strings used:
	private String baseUrl = System.getenv("bf_url");
	private String gxUrl = System.getenv("GX_url");
	private String username = System.getenv("bf_username");
	private String password = System.getenv("bf_password");
	private String driverPath = System.getenv("driver_path");
	private String browserPath = System.getenv("browser_path");
	
	@Rule
	public ImportanceReporter reporter = new ImportanceReporter();
	
	@Before
	public void setUp() throws Exception {
		// Setup Browser:
		driver = Utils.createWebDriver(browserPath, driverPath);
		wait = new WebDriverWait(driver, 5);
		gxLogin = new GxLoginPage(driver);
		bfMain = new BfMainPage(driver);
		actions = new Actions(driver);

		// Log in to GX:
		driver.get(gxUrl);
		gxLogin.loginToGeoAxis(username, password);
		bfMain.loginSongAndDance(baseUrl);
		driver.manage().window().maximize();
		
		// Make sure job is present in jobs list:
		driver.get("https://bf-api.stage.geointservices.io/v0/job/d2de0718-4374-43e4-82cd-70fbc2a5a7a4");
		driver.get(baseUrl);
		
		// Open Job Window:
		bfMain.jobsButton.click();
		jobsWindow = bfMain.jobsWindow();
		testJob = jobsWindow.singleJob("ForJobTesting");
	}

	@After
	public void tearDown() {
		driver.quit();
	}
	
	@Test @Importance(level = Level.MEDIUM)
	public void viewOnMap() {
		// Make sure that the "View On Map" Job button navigates the canvas to that Job's location.
		testJob.viewLink.click();
		Utils.assertPointInRange(bfMain.getCoords(), new Point2D.Double(0, 38), 5);
	}
	
	@Test @Importance(level = Level.HIGH)
	public void downloadResult() {
		// Make sure that the "Download" Job button does something.  Selenium cannot tell if a download occurred.
		assertEquals("There should not be a download link before clicking", null, testJob.downloadLink.getAttribute("href"));
		testJob.downloadLink.click();
		Utils.assertBecomesVisible(testJob.downloadLink, wait);
		Assert.assertTrue("Download path should appear after click", testJob.downloadLink.getAttribute("href").contains("blob"));
	}
	
	@Test @Importance(level = Level.LOW)
	public void forgetJob() {
		// Click on test job.
		testJob.thisWindow.click();
		Utils.assertBecomesVisible("Job opens to reveal forget button", testJob.forgetButton, wait);
		
		// Click on forget button, but cancel.
		testJob.forgetButton.click();
		Utils.assertBecomesVisible("Confirmation screen appears", testJob.confirmButton, wait);
		testJob.cancelButton.click();
		assertFalse("Can't click confirm after cancel", Utils.tryToClick(testJob.confirmButton));
		
		// Click on forget button, then confirm.
		testJob.forgetButton.click();
		Utils.assertBecomesVisible("Confirmation screen appears again", testJob.confirmButton, wait);
		testJob.confirmButton.click();
		Utils.assertBecomesInvisible("Job was removed from list", testJob.thisWindow, wait);
		
		// Make sure job is still missing after refresh.
		driver.get(driver.getCurrentUrl());
		assertNull(bfMain.jobsWindow().singleJob("ForJobTesting"));
	}
	
	@Test @Importance(level = Level.LOW)
	public void bypass_confirmation() throws InterruptedException {
		Utils.ignoreOnInt();
		// Try to bypass the the forget -> confirm process by directly clicking on confirm.	
		testJob.thisWindow.click();
		assertFalse("Should not be able to click 'confirm'", Utils.tryToClick(testJob.confirmButton));
		
		// Try to bypass the the forget -> confirm process by tabbing to the confirm button.
		actions.sendKeys(Keys.TAB, Keys.TAB, Keys.ENTER).build().perform();
		Thread.sleep(1000);
		assertTrue("Job should not be removed (Bug #5637)", Utils.checkExists(testJob.thisWindow));
	}
}
