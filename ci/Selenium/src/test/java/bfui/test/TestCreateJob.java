package bfui.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Base64;
import java.util.Calendar;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import bfui.test.page.CoordinateSearchPage;
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
	private String PLANET_LOCATION = System.getenv("planet_location");
	private String PLANET_KEY = System.getenv("planet_key");
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
	public void landsatJobWithMask() throws Exception {
		// Local Landsat8 job, mask enabled
		createJobFullTest("landsat_pds", null, true);
	}

	@Test
	@Info(importance = Importance.HIGH)
	public void landsatJobNoMask() throws Exception {
		// Local Landsat8 job, no mask
		createJobFullTest("landsat_pds", null, false);
	}

	@Test
	@Info(importance = Importance.HIGH)
	public void planetLandsatJob() throws Exception {
		// Planet Landsat Job, no mask
		if (PLANET_KEY != null) {
			zoomToLocation(PLANET_LOCATION);
			createJobFullTest("landsat", PLANET_KEY, false);
		} else {
			System.out.println("Skipping Planet Landsat, no key");
		}
	}

	@Test
	@Info(importance = Importance.HIGH)
	public void planetScopeJob() throws Exception {
		// PlanetScope Job, no mask
		if (PLANET_KEY != null) {
			zoomToLocation(PLANET_LOCATION);
			createJobFullTest("planetscope", PLANET_KEY, false);
		} else {
			System.out.println("Skipping PlanetScope, no key");
		}
	}

	@Test
	@Info(importance = Importance.HIGH)
	public void planetRapidEyeJob() throws Exception {
		// Planet RapidEye Job, no mask
		if (PLANET_KEY != null) {
			zoomToLocation(PLANET_LOCATION);
			createJobFullTest("rapideye", PLANET_KEY, false);
		} else {
			System.out.println("Skipping RapidEye, no key");
		}
	}

	/**
	 * Runs the full Job creation and verification logic, with assertions.
	 * <p>
	 * Tests Landsat_pds dataset type because this is currently the only job that is reliably testable.
	 * 
	 * @param source
	 *            The value of the source select control
	 * @param apiKey
	 *            The API Key, null if not to be filled out
	 * @param doMask
	 *            True for compute mask to be enabled, false if not.
	 */
	private void createJobFullTest(String source, String apiKey, boolean doMask) throws Exception {
		// Create the Job
		JobStatusPage statusPage = createJob(source, apiKey, doMask);
		try {
			// Waiting for the job to complete
			statusPage.scrollIntoView();
			String status = statusPage.getStatusOnCompletion(10 * 60);
			assertTrue(String.format("Job must complete successfully: %s", status), "Success".equals(status));
			// Test Map Interaction
			statusPage.zoomTo();
			// If no mask was applied, then Vectors should exist on the map
			if (!doMask) {
				// TODO: Test Further Later, failing
				//boolean vectorsDisplayed = Utils.doesImageContainVectorColors(Utils.getScreenshotImage(driver));
				//assertTrue("Vectors displayed on Map after successful Job Zoom", vectorsDisplayed);
			}
			// Download tests
			verifyDownloadLinks(statusPage);
		} finally {
			// Remove the job from the list
			statusPage.forgetJob();
			assertFalse("Job removed upon forget button clicked", mainPage.getCurrentURL().contains("jobId"));
		}
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
	 * @return The Status Page for the Job that was created
	 */
	private JobStatusPage createJob(String source, String apiKey, boolean doMask) throws Exception {
		// Navigate to the Create Jobs Page
		CreateJobPage createJobPage = mainPage.navigateCreateJobPage();
		assertTrue("Instruction text initially displayed", createJobPage.isInstructionTextVisible());

		// Perform a bounding box search for imagery and submit
		Point start = new Point(400, 500);
		Point end = new Point(300, 300);
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

		// Ensure the search completes successfully
		try {
			createJobPage.waitForSearchToComplete();
		} catch (TimeoutException timeoutException) {
			// Report errors if they are encountered
			if (createJobPage.isSearchErrorDisplayed()) {
				throw new Exception(
						String.format("%s imagery search has failed with error %s", source, createJobPage.getImagerySearchErrorMessage()));
			} else {
				throw new TimeoutException(
						String.format("Search imagery for %s failed to complete in time. It has likely timed out.", source),
						timeoutException);
			}
		}

		// Click on a random result in the results table
		createJobPage.selectRandomJobResult();
		createJobPage.setComputeMask(doMask);

		// Set a Random Name for the Job
		String jobName = "Test-" + UUID.randomUUID().toString().substring(0, 8);
		createJobPage.setJobName(jobName);

		// Run Algorithm:
		JobsPage jobsPage;
		try {
			jobsPage = createJobPage.runAlgorithm();
		} catch (TimeoutException timeoutException) {
			// Check if the Algorithm has failed to execute and grab the error information if so
			if (createJobPage.isAlgorithmErrorDisplayed()) {
				// Inject specific error information in the exception
				String error = String.format("Algorithm for source %s has failed because of error: %s", source,
						createJobPage.getAlgorithmErrorMessage());
				throw new TimeoutException(error, timeoutException);
			} else {
				// Unexpected error, simply re-throw
				throw timeoutException;
			}
		}
		assertTrue("Algorithm successfully redirects to Jobs page", jobsPage.isJobsPageUrlActive());

		// Check that the job appears in the list
		JobStatusPage jobStatusPage = jobsPage.getJobStatus(jobName);
		assertTrue("Job is contained in the Jobs list", jobName.equals(jobStatusPage.getName()));

		// Return the Job Status Page
		return jobStatusPage;
	}

	/**
	 * Runs assertions for testing the three download links for a particular Job Status Page.
	 * <p>
	 * This will not perform the actual download, but will submit HEAD requests to verify the link is valid and contains
	 * a reasonable (arbitrary) number of bytes.
	 * 
	 * @param statusPage
	 *            The Job Status page for a single job containing references to the download buttons
	 */
	private void verifyDownloadLinks(JobStatusPage statusPage) throws Exception {
		// Verify each of the download links
		verifyHeadRequest(statusPage.getGeoJsonLink());
		verifyHeadRequest(statusPage.getGeoPackageLink());
		verifyHeadRequest(statusPage.getShapefileLink());
	}

	/**
	 * Asserts that a download link HEAD request has proper response codes and bytes
	 * 
	 * @param Url
	 *            Download link for a Beachfront detection file
	 */
	private void verifyHeadRequest(String downloadLink) throws ClientProtocolException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		// Generate credentials header
		String plainCredentials = String.format("%s:%s", mainPage.getApiKeyCookie().getValue(), "");
		byte[] credentialBytes = plainCredentials.getBytes();
		byte[] encodedCredentials = Base64.getEncoder().encode(credentialBytes);
		String credentials = new String(encodedCredentials);
		HttpHead request = new HttpHead(downloadLink);
		request.addHeader("Authorization", String.format("Basic %s", credentials));
		// Execute
		HttpResponse response = client.execute(request);
		// Validate Response. 200 code with some reasonable amount of bytes
		int statusCode = response.getStatusLine().getStatusCode();
		assertTrue(String.format("%s Download must have 200 OK status; %s ", downloadLink, statusCode), statusCode == 200);
		assertTrue("Download has sufficient bytes",
				Integer.parseInt(response.getHeaders("Content-Length")[0].getValue()) > 500 /* Totally arbitrary */);
	}

	/**
	 * Zooms to a particular testing location.
	 * 
	 * @param location
	 *            The string location
	 */
	private void zoomToLocation(String location) {
		CoordinateSearchPage searchPage = mainPage.openCoordinateSearchDialog();
		searchPage.search(location);
	}

}
