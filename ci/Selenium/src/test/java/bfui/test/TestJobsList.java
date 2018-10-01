package bfui.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import bfui.test.page.CreateJobPage;
import bfui.test.page.GxLoginPage;
import bfui.test.page.JobStatusPage;
import bfui.test.page.JobsPage;
import bfui.test.page.MainPage;
import bfui.test.util.Info;
import bfui.test.util.Info.Importance;
import bfui.test.util.Reporter;
//import bfui.test.util.SauceResultReporter;
import bfui.test.util.Utils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJobsList {
	private WebDriver driver;
	private Actions actions;
	private WebDriverWait wait;
	private MainPage bfMain;
	private JobsPage jobsWindow;
	private JobStatusPage testJob;
	private String jobUrl;
	private CreateJobPage createJobWindow;
	private GxLoginPage gxLogin;

	// Strings used:
	private String baseUrl = System.getenv("bf_url");
	private String username = System.getenv("bf_username");
	private String password = System.getenv("bf_password");

	private String browser = System.getenv("browser");

	private CookieManager cm = new CookieManager();
	private String fromDate = "2016-11-01";
	private String toDate = "2016-11-20";

	@Rule
	public Reporter reporter = new Reporter();
	@Rule
	public TestName name = new TestName();

	@Before
	public void setUp() throws Exception {
		// Setup Browser:
		driver = Utils.getChromeRemoteDriver();
		wait = new WebDriverWait(driver, 60);
		gxLogin = new GxLoginPage(driver);
		bfMain = new MainPage(driver, wait);
		actions = new Actions(driver);
		cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(cm);

		driver.get(baseUrl);
		bfMain.geoAxisLink.click();
		Thread.sleep(1000);
		gxLogin.login(username, password);
		Utils.assertThatAfterWait("Should navigate back to BF", ExpectedConditions.urlMatches(baseUrl), wait);

		bfMain.jobsButton.click();

		Utils.scrollInToView(driver, bfMain.canvas);
		actions.moveToElement(bfMain.canvas).build().perform(); // Move mouse to clear title text (that may obscure jobs
																// list)
		jobsWindow = bfMain.jobsWindow();
	}

	@After
	public void tearDown() {
		driver.quit();
	}

	public void moveToNonMaskJob() {
		jobsWindow = bfMain.jobsWindow();
		testJob = jobsWindow.singleJob("JobForTestingNoMask");
	}

	@Test
	@Info(importance = Importance.HIGH)
	public void createMaskJobs() throws Exception {
		// create job with mask
		createJob(true);
	}

	@Test
	@Info(importance = Importance.HIGH)
	public void createNoMaskJobs() throws Exception {
		// create job without mask
		createJob(false);
	}

	public void createJob(boolean mask) throws Exception {
		// Verify Create Job Window Opens and has expected contents:
		bfMain.createJobButton.click();
		createJobWindow = bfMain.createJobWindow();
		Utils.assertThatAfterWait("Instructions should become visible", ExpectedConditions.visibilityOf(createJobWindow.instructionText),
				wait);

		assertTrue("Instructions should prompt user to draw a bounding box",
				createJobWindow.instructionText.getText().matches(".*[Dd]raw.*[Bb]ound.*"));

		Point start = new Point(500, 600);
		Point end = new Point(100, 100);

		// Navigate to South America:
		bfMain.searchButton.click();
		bfMain.searchWindow().searchCoordinates(-29, -49.5);
		bfMain.zoomOutButton.click();
		bfMain.zoomOutButton.click();
		bfMain.zoomOutButton.click();
		bfMain.zoomOutButton.click();
		System.out.println(driver.manage().window().getSize());

		// Draw Bounding Box:
		bfMain.canvas.click();
		bfMain.drawBoundingBox(actions, start, end);

		// Enter Options:
		// createJobWindow.apiKeyEntry.clear();
		// createJobWindow.apiKeyEntry.sendKeys(apiKeyPlanet);
		createJobWindow.selectSource("landsat_pds");
		actions.moveToElement(createJobWindow.cloudSlider).click().build().perform();

		LocalDateTime todayLDT = LocalDateTime.now();
		LocalDateTime tomorrowLDT = LocalDateTime.now().plusDays(10);
		String fromDay = (todayLDT.getDayOfMonth() < 10 ? "0" + todayLDT.getDayOfMonth() : todayLDT.getDayOfMonth()).toString();
		String fromMonth = (todayLDT.getMonthValue() < 10 ? "0" + todayLDT.getMonthValue() : todayLDT.getMonthValue()).toString();
		String toDay = (tomorrowLDT.getDayOfMonth() < 10 ? "0" + tomorrowLDT.getDayOfMonth() : tomorrowLDT.getDayOfMonth()).toString();
		String toMonth = (tomorrowLDT.getMonthValue() < 10 ? "0" + tomorrowLDT.getMonthValue() : tomorrowLDT.getMonthValue()).toString();
		fromDate = (todayLDT.getYear() - 1) + "-" + fromMonth + "-" + fromDay;
		toDate = (tomorrowLDT.getYear() - 1) + "-" + toMonth + "-" + toDay;
		createJobWindow.enterDates(fromDate, toDate);
		Utils.assertThatAfterWait("Search button should be clickable",
				ExpectedConditions.elementToBeClickable(createJobWindow.submitButton), wait);

		// Search for images:
		createJobWindow.submitButton.click();

		// Wait for search to complete:
		assertTrue("Image search should complete", createJobWindow.waitForCompleteSearch(45));
		createJobWindow.retryIfNeeded(3, 45);
		Thread.sleep(5000);
		List<WebElement> tiles = driver.findElements(By.cssSelector(".ImagerySearchList-results > table > tbody > tr"));
		// Click until an image is found:
		// bfMain.clickUntilResultFound(start, end, new Point(10, 10), actions);
		tiles.get(0).click();
		// createJobWindow.scroll(driver, 0, 5000); //scroll way down, in case there are multiple algorithms.
		Thread.sleep(5000);

		if (mask) {
			createJobWindow.computeMask.click();
			createJobWindow.jobName.clear();
			createJobWindow.jobName.sendKeys("JobForTestingMask");
		} else {
			createJobWindow.jobName.clear();
			createJobWindow.jobName.sendKeys("JobForTestingNoMask");
		}
		// Run Algorithm:

		Utils.scrollInToView(driver, createJobWindow.algorithmButton);
		createJobWindow.algorithmButton.click();
		Thread.sleep(60000);
		// Utils.takeSnapShot(driver,"test3.png"); For Testing

	}

	@Test
	@Info(importance = Importance.MEDIUM)
	public void view_on_map_test() {
		testJob = jobsWindow.singleJob("JobForTestingMask");
		view_on_map("JobForTestingMask");
		moveToNonMaskJob();
		view_on_map("JobForTestingNoMask");
	}

	private void view_on_map(String jobName) {
		// Make sure that the "View On Map" Job button navigates the canvas to that Job's location.
		testJob = jobsWindow.singleJob("JobForTestingMask");
		testJob.viewLink.click();
		Utils.assertPointInRange(bfMain.getCoords(), new Point2D.Double(-29, -49.5), 10);
	}

	@Test
	@Info(importance = Importance.HIGH)
	public void download_geojson_result_test() throws InterruptedException, IOException {
		testJob = jobsWindow.singleJob("JobForTestingMask");
		download_geojson_result("JobForTestingMask");
		moveToNonMaskJob();
		download_geojson_result("JobForTestingNoMask");
	}

	private void download_geojson_result(String jobName) throws InterruptedException, IOException {
		// Make sure that the "Download" Job button does something. Selenium cannot tell if a download occurred.
		// assertEquals("There should not be a download link before clicking", null,
		// testJob.downloadLink.getAttribute("href"));
		String home = System.getProperty("user.home");
		File file = new File(home + "/Downloads/" + jobName + ".geojson");
		if (file.exists()) {
			file.delete();
		}
		testJob.downloadButton().click();
		testJob.downloadLinkGeojson().click();
		Thread.sleep(20000); // 1000 milliseconds is one second.
		if (browser.equalsIgnoreCase("firefox")) {
			actions.sendKeys(Keys.ARROW_DOWN);
			actions.sendKeys(Keys.ARROW_DOWN);
			actions.sendKeys(Keys.ENTER);
			Thread.sleep(2000);
		} else {
			driver.get("chrome://downloads");
			Thread.sleep(2000);
			String getNumberOfDownloadsJS = "function getNumDl() {"
					+ "var list = document.querySelector('downloads-manager').shadowRoot.querySelector('#downloads-list').getElementsByTagName('downloads-item');"
					+ "return list.length;};" + "return getNumDl()";
			long numberOfDownloads = (long) ((JavascriptExecutor) driver).executeScript(getNumberOfDownloadsJS);
			System.out.println(numberOfDownloads);
			Assert.assertTrue("File shows in downloads", numberOfDownloads > 0);
		}
		System.out.println(file.length());
		Assert.assertTrue("File Size is larger than 1kb", file.length() > 1000);
	}

	@Test
	@Info(importance = Importance.HIGH)
	public void download_geopackage_result_test() throws InterruptedException, IOException {
		testJob = jobsWindow.singleJob("JobForTestingMask");
		download_geopackage_result("JobForTestingMask");
		moveToNonMaskJob();
		download_geopackage_result("JobForTestingNoMask");
	}

	public void download_geopackage_result(String jobName) throws InterruptedException, IOException {
		// Make sure that the "Download" Job button does something. Selenium cannot tell if a download occurred.
		// assertEquals("There should not be a download link before clicking", null,
		// testJob.downloadLink.getAttribute("href"));
		String home = System.getProperty("user.home");
		File file = new File(home + "/Downloads/" + jobName + ".gpkg");
		if (file.exists()) {
			file.delete();
		}
		testJob.downloadButton().click();
		testJob.downloadLinkGeopkg().click();
		Thread.sleep(20000);
		if (browser.equalsIgnoreCase("firefox")) {
			actions.sendKeys(Keys.ARROW_DOWN);
			actions.sendKeys(Keys.ARROW_DOWN);
			actions.sendKeys(Keys.ENTER);
			Thread.sleep(2000);
		} else {
			driver.get("chrome://downloads");
			Thread.sleep(2000);
			String getNumberOfDownloadsJS = "function getNumDl() {"
					+ "var list = document.querySelector('downloads-manager').shadowRoot.querySelector('#downloads-list').getElementsByTagName('downloads-item');"
					+ "return list.length;};" + "return getNumDl()";
			long numberOfDownloads = (long) ((JavascriptExecutor) driver).executeScript(getNumberOfDownloadsJS);
			System.out.println(numberOfDownloads);
			Assert.assertTrue("File shows in downloads", numberOfDownloads > 0);
		}
		System.out.println(file.length());
		Assert.assertTrue("File Size is larger than 1kb", file.length() > 1000);
	}

	@Test
	@Info(importance = Importance.HIGH)
	public void download_shapefile_result_test() throws InterruptedException, IOException {
		testJob = jobsWindow.singleJob("JobForTestingMask");
		download_shapefile_result("JobForTestingMask");
		moveToNonMaskJob();
		download_shapefile_result("JobForTestingNoMask");
	}

	public void download_shapefile_result(String jobName) throws InterruptedException, IOException {
		// Make sure that the "Download" Job button does something. Selenium cannot tell if a download occurred.
		// assertEquals("There should not be a download link before clicking", null,
		// testJob.downloadLink.getAttribute("href"));
		String home = System.getProperty("user.home");
		File file = new File(home + "/Downloads/" + jobName + ".shp.zip");
		if (file.exists()) {
			file.delete();
		}
		testJob.downloadButton().click();
		testJob.downloadLinkShapefile().click();
		Thread.sleep(20000);
		if (browser.equalsIgnoreCase("firefox")) {
			actions.sendKeys(Keys.ARROW_DOWN);
			actions.sendKeys(Keys.ARROW_DOWN);
			actions.sendKeys(Keys.ENTER);
			Thread.sleep(2000);
		} else {
			driver.get("chrome://downloads");
			Thread.sleep(2000);
			String getNumberOfDownloadsJS = "function getNumDl() {"
					+ "var list = document.querySelector('downloads-manager').shadowRoot.querySelector('#downloads-list').getElementsByTagName('downloads-item');"
					+ "return list.length;};" + "return getNumDl()";
			long numberOfDownloads = (long) ((JavascriptExecutor) driver).executeScript(getNumberOfDownloadsJS);
			System.out.println(numberOfDownloads);
			Assert.assertTrue("File shows in downloads", numberOfDownloads > 0);
		}
		System.out.println(file.length());
		Assert.assertTrue("File Size is larger than 1kb", file.length() > 1000);
	}

	@Test
	@Info(importance = Importance.LOW)
	public void forget_job_test() throws Exception {
		testJob = jobsWindow.singleJob("JobForTestingMask");
		forget_job("JobForTestingMask");
		moveToNonMaskJob();
		forget_job("JobForTestingNoMask");
	}

	public void forget_job(String jobName) throws Exception {

		// Click on test job.
		testJob.thisWindow.click();
		testJob.caret.click();
		Utils.assertBecomesVisible("Job opens to reveal forget button", testJob.forgetButton, wait);
		jobUrl = driver.getCurrentUrl();
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
		assertNull(bfMain.jobsWindow().singleJob(jobName));
		driver.get(jobUrl);
	}

}
