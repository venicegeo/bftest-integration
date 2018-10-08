package bfui.test;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

import bfui.test.page.CreateJobPage;
import bfui.test.page.GxLoginPage;
import bfui.test.page.JobStatusPage;
import bfui.test.page.JobsPage;
import bfui.test.page.MainPage;
import bfui.test.util.Info;
import bfui.test.util.Info.Importance;
import bfui.test.util.Reporter;
import bfui.test.util.Utils;

/**
 * Tests the creation, tracking, and results verification of Beachfront jobs.
 */
public class TestCreateJob {
	private String BASE_URL = System.getenv("bf_url");
	private String USERNAME = System.getenv("bf_username");
	private String PASSWORD = System.getenv("bf_password");

	private WebDriver driver;
	private MainPage mainPage;

	@Rule
	public Reporter reporter = new Reporter();
	@Rule
	public TestName name = new TestName();

	@Before
	public void setUp() throws Exception {
		driver = Utils.getChromeRemoteDriver();
		mainPage = new MainPage(driver);
		driver.get(BASE_URL);
		// Perform Login
		GxLoginPage loginPage = mainPage.beginLogin();
		mainPage = loginPage.loginDisadvantaged(USERNAME, PASSWORD, mainPage);
	}

	@After
	public void tearDown() {
		driver.quit();
	}

	@Test
	@Info(importance = Importance.HIGH)
	public void landsat_job() throws Exception {
		// Landsat8 job, mask enabled
		String jobName = createJob("landsat_pds", null /* No key needed for local Landsat */, true);

	}

	@Test
	@Info(importance = Importance.HIGH)
	public void landsat_job_no_mask() throws Exception {
		// Landsat8 job, no mask
		String jobName = createJob("landsat_pds", null /* No key needed for local Landsat */, false);

	}

	/**
	 * Creates a Job with assertions. This will select a random job from the results list as the target image.
	 * 
	 * @param source
	 *            The value of the source select control
	 * @param apiKey
	 *            The API key, if needed
	 * @param mask
	 *            True if coastal mask should be enabled, false if not.
	 * @return The name of the Job that was submitted.
	 */
	private String createJob(String source, String apiKey, boolean doMask) throws Exception {
		// Navigate to the Create Jobs Page
		CreateJobPage createJobPage = mainPage.navigateCreateJobPage();
		assertTrue("Instruction text initially displayed", createJobPage.isInstructionTextVisible());

		// Perform a bounding box search for imagery and submit
		Point start = new Point(500, 600);
		Point end = new Point(100, 100);
		mainPage.drawBoundingBox(start, end);
		assertTrue("Minimap displays on bounding box draw", createJobPage.isMinimapDisplayed());

		// Enter Search Options
		createJobPage.selectSource(source);
		if (apiKey != null) { // Enter key if provided
			createJobPage.enterKey(apiKey);
		}
		// Accept default search dates, but replace the "from" year with the previous year to get 1 years worth of
		// results
		String[] currentDates = createJobPage.getSearchDates();
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		createJobPage.enterDates(currentDates[0].replaceAll(String.valueOf(currentYear), String.valueOf(currentYear - 1)), currentDates[1]);
		// Ensure the search button is enabled with search criteria fully populated
		assertTrue("Criteria are populated and search is enabled", createJobPage.isSearchEnabled());

		// Search for images:
		createJobPage.searchForImagery();
		assertTrue("Search is performing", createJobPage.isSearching());
		createJobPage.waitForSearchToComplete();

		// Click on a random result in the results table
		createJobPage.selectRandomJobResult();
		createJobPage.setComputeMask(doMask);

		// Set a Random Name for the Job
		String jobName = "Test-" + UUID.randomUUID().toString().substring(0, 8);
		createJobPage.setJobName(jobName);

		// Run Algorithm:
		JobsPage jobsPage = createJobPage.runAlgorithm();
		assertTrue("Algorithm successfully redirects to Jobs page", jobsPage.isJobsPageUrlActive());

		// Check that the job appears in the list
		JobStatusPage jobStatusPage = jobsPage.getJobStatus(jobName);
		assertTrue("Job is contained in the Jobs list", jobName.equals(jobStatusPage.getName()));

		// Return the Job Name
		return jobName;
	}
	//
	// @Test
	// @Info(importance = Importance.MEDIUM)
	// public void view_on_map_test() {
	// testJob = jobsWindow.singleJob("JobForTestingMask");
	// view_on_map("JobForTestingMask");
	// moveToNonMaskJob();
	// view_on_map("JobForTestingNoMask");
	// }
	//
	// private void view_on_map(String jobName) {
	// // Make sure that the "View On Map" Job button navigates the canvas to that Job's location.
	// testJob = jobsWindow.singleJob("JobForTestingMask");
	// testJob.viewLink.click();
	// Utils.assertPointInRange("", mainPage.getCoords(), new Point2D.Double(-29, -49.5), 10);
	// }
	//
	// @Test
	// @Info(importance = Importance.HIGH)
	// public void download_geojson_result_test() throws InterruptedException, IOException {
	// testJob = jobsWindow.singleJob("JobForTestingMask");
	// download_geojson_result("JobForTestingMask");
	// moveToNonMaskJob();
	// download_geojson_result("JobForTestingNoMask");
	// }
	//
	// private void download_geojson_result(String jobName) throws InterruptedException, IOException {
	// // Make sure that the "Download" Job button does something. Selenium cannot tell if a download occurred.
	// // assertEquals("There should not be a download link before clicking", null,
	// // testJob.downloadLink.getAttribute("href"));
	// String home = System.getProperty("user.home");
	// File file = new File(home + "/Downloads/" + jobName + ".geojson");
	// if (file.exists()) {
	// file.delete();
	// }
	// testJob.downloadButton().click();
	// testJob.downloadLinkGeojson().click();
	// Thread.sleep(20000); // 1000 milliseconds is one second.
	// if (browser.equalsIgnoreCase("firefox")) {
	// actions.sendKeys(Keys.ARROW_DOWN);
	// actions.sendKeys(Keys.ARROW_DOWN);
	// actions.sendKeys(Keys.ENTER);
	// Thread.sleep(2000);
	// } else {
	// driver.get("chrome://downloads");
	// Thread.sleep(2000);
	// String getNumberOfDownloadsJS = "function getNumDl() {"
	// + "var list =
	// document.querySelector('downloads-manager').shadowRoot.querySelector('#downloads-list').getElementsByTagName('downloads-item');"
	// + "return list.length;};" + "return getNumDl()";
	// long numberOfDownloads = (long) ((JavascriptExecutor) driver).executeScript(getNumberOfDownloadsJS);
	// System.out.println(numberOfDownloads);
	// Assert.assertTrue("File shows in downloads", numberOfDownloads > 0);
	// }
	// System.out.println(file.length());
	// Assert.assertTrue("File Size is larger than 1kb", file.length() > 1000);
	// }
	//
	// @Test
	// @Info(importance = Importance.HIGH)
	// public void download_geopackage_result_test() throws InterruptedException, IOException {
	// testJob = jobsWindow.singleJob("JobForTestingMask");
	// download_geopackage_result("JobForTestingMask");
	// moveToNonMaskJob();
	// download_geopackage_result("JobForTestingNoMask");
	// }
	//
	// public void download_geopackage_result(String jobName) throws InterruptedException, IOException {
	// // Make sure that the "Download" Job button does something. Selenium cannot tell if a download occurred.
	// // assertEquals("There should not be a download link before clicking", null,
	// // testJob.downloadLink.getAttribute("href"));
	// String home = System.getProperty("user.home");
	// File file = new File(home + "/Downloads/" + jobName + ".gpkg");
	// if (file.exists()) {
	// file.delete();
	// }
	// testJob.downloadButton().click();
	// testJob.downloadLinkGeopkg().click();
	// Thread.sleep(20000);
	// if (browser.equalsIgnoreCase("firefox")) {
	// actions.sendKeys(Keys.ARROW_DOWN);
	// actions.sendKeys(Keys.ARROW_DOWN);
	// actions.sendKeys(Keys.ENTER);
	// Thread.sleep(2000);
	// } else {
	// driver.get("chrome://downloads");
	// Thread.sleep(2000);
	// String getNumberOfDownloadsJS = "function getNumDl() {"
	// + "var list =
	// document.querySelector('downloads-manager').shadowRoot.querySelector('#downloads-list').getElementsByTagName('downloads-item');"
	// + "return list.length;};" + "return getNumDl()";
	// long numberOfDownloads = (long) ((JavascriptExecutor) driver).executeScript(getNumberOfDownloadsJS);
	// System.out.println(numberOfDownloads);
	// Assert.assertTrue("File shows in downloads", numberOfDownloads > 0);
	// }
	// System.out.println(file.length());
	// Assert.assertTrue("File Size is larger than 1kb", file.length() > 1000);
	// }
	//
	// @Test
	// @Info(importance = Importance.HIGH)
	// public void download_shapefile_result_test() throws InterruptedException, IOException {
	// testJob = jobsWindow.singleJob("JobForTestingMask");
	// download_shapefile_result("JobForTestingMask");
	// moveToNonMaskJob();
	// download_shapefile_result("JobForTestingNoMask");
	// }
	//
	// public void download_shapefile_result(String jobName) throws InterruptedException, IOException {
	// // Make sure that the "Download" Job button does something. Selenium cannot tell if a download occurred.
	// // assertEquals("There should not be a download link before clicking", null,
	// // testJob.downloadLink.getAttribute("href"));
	// String home = System.getProperty("user.home");
	// File file = new File(home + "/Downloads/" + jobName + ".shp.zip");
	// if (file.exists()) {
	// file.delete();
	// }
	// testJob.downloadButton().click();
	// testJob.downloadLinkShapefile().click();
	// Thread.sleep(20000);
	// if (browser.equalsIgnoreCase("firefox")) {
	// actions.sendKeys(Keys.ARROW_DOWN);
	// actions.sendKeys(Keys.ARROW_DOWN);
	// actions.sendKeys(Keys.ENTER);
	// Thread.sleep(2000);
	// } else {
	// driver.get("chrome://downloads");
	// Thread.sleep(2000);
	// String getNumberOfDownloadsJS = "function getNumDl() {"
	// + "var list =
	// document.querySelector('downloads-manager').shadowRoot.querySelector('#downloads-list').getElementsByTagName('downloads-item');"
	// + "return list.length;};" + "return getNumDl()";
	// long numberOfDownloads = (long) ((JavascriptExecutor) driver).executeScript(getNumberOfDownloadsJS);
	// System.out.println(numberOfDownloads);
	// Assert.assertTrue("File shows in downloads", numberOfDownloads > 0);
	// }
	// System.out.println(file.length());
	// Assert.assertTrue("File Size is larger than 1kb", file.length() > 1000);
	// }
	//
	// @Test
	// @Info(importance = Importance.LOW)
	// public void forget_job_test() throws Exception {
	// testJob = jobsWindow.singleJob("JobForTestingMask");
	// forget_job("JobForTestingMask");
	// moveToNonMaskJob();
	// forget_job("JobForTestingNoMask");
	// }
	//
	// public void forget_job(String jobName) throws Exception {
	//
	// // Click on test job.
	// testJob.thisWindow.click();
	// testJob.caret.click();
	// Utils.assertBecomesVisible("Job opens to reveal forget button", testJob.forgetButton, wait);
	// jobUrl = driver.getCurrentUrl();
	// // Click on forget button, but cancel.
	// testJob.forgetButton.click();
	// Utils.assertBecomesVisible("Confirmation screen appears", testJob.confirmButton, wait);
	// testJob.cancelButton.click();
	// assertFalse("Can't click confirm after cancel", Utils.tryToClick(testJob.confirmButton));
	//
	// // Click on forget button, then confirm.
	// testJob.forgetButton.click();
	// Utils.assertBecomesVisible("Confirmation screen appears again", testJob.confirmButton, wait);
	// testJob.confirmButton.click();
	// Utils.assertBecomesInvisible("Job was removed from list", testJob.thisWindow, wait);
	//
	// // Make sure job is still missing after refresh.
	// driver.get(driver.getCurrentUrl());
	// assertNull(mainPage.jobsWindow().singleJob(jobName));
	// driver.get(jobUrl);
	// }
	//
	// private void moveToNonMaskJob() {
	// jobsWindow = mainPage.jobsWindow();
	// testJob = jobsWindow.singleJob("JobForTestingNoMask");
	// }

}
