package bfui.test;

import static org.junit.Assert.*;
import org.openqa.selenium.JavascriptExecutor;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import bfui.test.page.BfCreateJobWindowPage;
import bfui.test.page.BfJobsWindowPage;
import bfui.test.page.BfMainPage;
import bfui.test.page.BfSingleJobPage;
import bfui.test.page.CoastlineLoginPage;
import bfui.test.page.GxLoginPage;
import bfui.test.page.LoginPage;
import bfui.test.util.Info;
import bfui.test.util.Reporter;
//import bfui.test.util.SauceResultReporter;
import bfui.test.util.Utils;
import bfui.test.util.Info.Importance;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJobsList {
	private WebDriver driver;
	private Actions actions;
	private WebDriverWait wait;
	private BfMainPage bfMain;
	private BfJobsWindowPage jobsWindow;
	private BfSingleJobPage testJob;
	private LoginPage login;
	private String jobUrl;
	private BfCreateJobWindowPage createJobWindow;
	
	// Strings used:
	private String baseUrl = System.getenv("bf_url");
	private String gxUrl = System.getenv("GX_url");
	private String username = System.getenv("bf_username");
	private String password = System.getenv("bf_password");
	private String space = System.getenv("space");
	private String browser = System.getenv("browser");
	private String apiKeyPlanet = System.getenv("PL_API_KEY");
	private String downloadPath = "C:\\Downloads";
	private CookieManager cm = new CookieManager();
	private String fromDate = "2016-11-01";
	private String toDate = "2016-11-05";


	
	@Rule
	public Reporter reporter = new Reporter("http://dashboard.venicegeo.io/cgi-bin/bf_ui_" + browser + "/" + space + "/load.pl");
	@Rule
	public TestName name = new TestName();
	//@Rule
	//public SauceResultReporter sauce = new SauceResultReporter();
	
	@Before
	public void setUp() throws Exception {
		// Setup Browser:
		driver = Utils.createSauceDriver(name.getMethodName());
		wait = new WebDriverWait(driver, 60);
		login = new GxLoginPage(driver);
		bfMain = new BfMainPage(driver, wait);
		actions = new Actions(driver);
		cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
	    CookieHandler.setDefault(cm);
		// Log in to GX:
//		driver.get(gxUrl);
//		gxLogin.loginToGeoAxis(username, password);
		driver.get(baseUrl);
		bfMain.geoAxisLink.click();
		Thread.sleep(1000);
		login.login(username, password);
		Utils.assertThatAfterWait("Should navigate back to BF", ExpectedConditions.urlMatches(baseUrl), wait);
		
		//if (bfMain.browserSupportWindow.isDisplayed()){
		//	bfMain.browserSupportDismiss.click();
		//}
		// Make sure job is present in jobs list:
		//bfMain.rememberJob(space, driver.getCurrentUrl());
		//create job with mask
		bfMain.jobsButton.click();
		
		Utils.scrollInToView(driver, bfMain.canvas);
		actions.moveToElement(bfMain.canvas).build().perform(); // Move mouse to clear title text (that may obscure jobs list)
		jobsWindow = bfMain.jobsWindow();
<<<<<<< Updated upstream
		testJob = jobsWindow.singleJob("LC08_L1TP_185054_20180917_20180917_01_RT");
=======
		
>>>>>>> Stashed changes
		
	}

	@After
	public void tearDown() {
		driver.quit();
	}
	public void moveToNonMaskJob(){
		bfMain.jobsButton.click();
		
		Utils.scrollInToView(driver, bfMain.canvas);
		actions.moveToElement(bfMain.canvas).build().perform(); // Move mouse to clear title text (that may obscure jobs list)
		jobsWindow = bfMain.jobsWindow();
		testJob = jobsWindow.singleJob("JobForTestingMask");
	}
	
	@Test @Info(importance = Importance.HIGH)
	public void createMaskJobs() throws Exception {
		//create job with mask
		createJob(true);
	}
	
	@Test @Info(importance = Importance.HIGH)
	public void createNoMaskJobs() throws Exception {
		//create job without mask
		createJob(false);
	}
	
	public void createJob(boolean mask)throws Exception {
		// Verify Create Job Window Opens and has expected contents:
				bfMain.createJobButton.click();
				createJobWindow = bfMain.createJobWindow();
				Utils.assertThatAfterWait("Instructions should become visible", ExpectedConditions.visibilityOf(createJobWindow.instructionText), wait);
	
				assertTrue("Instructions should prompt user to draw a bounding box", createJobWindow.instructionText.getText().matches(".*[Dd]raw.*[Bb]ound.*"));

				Point start = new Point(500, 600);
				Point end = new Point(100, 100);
				
				// Navigate to South America:
				bfMain.searchButton.click();
				bfMain.searchWindow().searchCoordinates(-29,-49.5);
				bfMain.zoomOutButton.click();
				bfMain.zoomOutButton.click();
				System.out.println(driver.manage().window().getSize());
				
				// Draw Bounding Box:
				bfMain.canvas.click();
				bfMain.drawBoundingBox(actions,start,end);
				
				//Utils.takeSnapShot(driver,"test.png"); For Testing
				
				// Enter Options:
				//createJobWindow.apiKeyEntry.clear();
				//createJobWindow.apiKeyEntry.sendKeys(apiKeyPlanet);
				createJobWindow.selectSource("landsat_pds");
				actions.moveToElement(createJobWindow.cloudSlider).click().build().perform();
				Date today = new Date();

				LocalDateTime todayLDT = LocalDateTime.now();
				LocalDateTime tomorrowLDT = LocalDateTime.now().plusDays(1);
				String fromDay = (todayLDT.getDayOfMonth()<10?"0"+todayLDT.getDayOfMonth():todayLDT.getDayOfMonth()).toString();
				String fromMonth = (todayLDT.getMonthValue()<10?"0"+todayLDT.getMonthValue():todayLDT.getMonthValue()).toString();
				String toDay = (tomorrowLDT.getDayOfMonth()<10?"0"+tomorrowLDT.getDayOfMonth():tomorrowLDT.getDayOfMonth()).toString();
				String toMonth = (tomorrowLDT.getMonthValue()<10?"0"+tomorrowLDT.getMonthValue():tomorrowLDT.getMonthValue()).toString();
				fromDate=(todayLDT.getYear()-1)+"-"+fromMonth+"-"+fromDay;
				toDate=(tomorrowLDT.getYear()-1)+"-"+toMonth+"-"+toDay;
				createJobWindow.enterDates(fromDate, toDate);
				Utils.assertThatAfterWait("Search button should be clickable", ExpectedConditions.elementToBeClickable(createJobWindow.submitButton), wait);
				
				// Search for images:
				createJobWindow.submitButton.click();
				
				// Wait for search to complete:
				assertTrue("Image search should complete", createJobWindow.waitForCompleteSearch(45));
				createJobWindow.retryIfNeeded(3, 45);
				Thread.sleep(5000);
				//Utils.takeSnapShot(driver,"test2.png"); For Testing
				List<WebElement> tiles =  driver.findElements(By.cssSelector(".ImagerySearchList-results > table > tbody > tr"));
				// Click until an image is found:
				//bfMain.clickUntilResultFound(start, end, new Point(10, 10), actions);
				tiles.get(0).click();
				//createJobWindow.scroll(driver, 0, 5000); //scroll way down, in case there are multiple algorithms.
				Thread.sleep(5000);
				
				if(mask){
				createJobWindow.computeMask.click();
				createJobWindow.jobName.clear();
				createJobWindow.jobName.sendKeys("JobForTestingMask");
				}else{
				createJobWindow.jobName.clear();
				createJobWindow.jobName.sendKeys("JobForTestingNoMask");
				}
				// Run Algorithm:
				
				
				Utils.scrollInToView(driver, createJobWindow.algorithmButton);
				createJobWindow.algorithmButton.click();
				wait.withTimeout(60, TimeUnit.SECONDS);
				//Utils.takeSnapShot(driver,"test3.png"); For Testing
				//Utils.assertThatAfterWait("Navigated to jobs page", ExpectedConditions.urlMatches(baseUrl + "jobs\\?jobId=.*"), wait);
	}
	
	
	@Test @Info(importance = Importance.MEDIUM)
	public void view_on_map_test() {
		testJob = jobsWindow.singleJob("JobForTestingMask");
		view_on_map("JobForTestingMask");
		moveToNonMaskJob();
		view_on_map("JobForTestingNoMask");
	}
	
	
	private void view_on_map(String jobName) {
		// Make sure that the "View On Map" Job button navigates the canvas to that Job's location.
		testJob.viewLink.click();
		Utils.assertPointInRange(bfMain.getCoords(), new Point2D.Double(-123.83, 38.95), 10);
	}
	
	@Test @Info(importance = Importance.HIGH)
	public void download_geojson_result_test() throws InterruptedException, IOException {
		testJob = jobsWindow.singleJob("JobForTestingMask");
		download_geojson_result("JobForTestingMask");
		moveToNonMaskJob();
		download_geojson_result("JobForTestingNoMask");
	}
	
	private void download_geojson_result(String jobName) throws InterruptedException, IOException {
		// Make sure that the "Download" Job button does something.  Selenium cannot tell if a download occurred.
		//assertEquals("There should not be a download link before clicking", null, testJob.downloadLink.getAttribute("href"));
		String home = System.getProperty("user.home");
<<<<<<< Updated upstream
		File file = new File(home+"/Downloads/"+"LC08_L1TP_185054_20180917_20180917_01_RT"+".geojson");
=======
		File file = new File(home+"/Downloads/"+jobName+".geojson");
>>>>>>> Stashed changes
		if(file.exists())
		{
			file.delete();
		}
		testJob.downloadButton().click();
		testJob.downloadLinkGeojson().click();
    	Thread.sleep(10000);                 //1000 milliseconds is one second.
		if(browser.equalsIgnoreCase("firefox")){
			actions.sendKeys(Keys.ARROW_DOWN);
			actions.sendKeys(Keys.ARROW_DOWN);
			actions.sendKeys(Keys.ENTER);
			Thread.sleep(2000); 
		}else{
			driver.get("chrome://downloads");
			Thread.sleep(2000); 
			String getNumberOfDownloadsJS="function getNumDl() {"
			+"var list = document.querySelector('downloads-manager').shadowRoot.querySelector('#downloads-list').getElementsByTagName('downloads-item');"
			+"return list.length;};"
			+"return getNumDl()";
			long numberOfDownloads = (long)((JavascriptExecutor) driver).executeScript(getNumberOfDownloadsJS);
			System.out.println(numberOfDownloads);
			Assert.assertTrue("File shows in downloads",numberOfDownloads>0);
		}
		System.out.println(file.length());
		Assert.assertTrue("File Size is larger than 1kb",file.length()>1000);
			//Assert.assertTrue("Download path should appear after click", testJob.downloadLink.getAttribute("href").contains("blob"));
	}
	@Test @Info(importance = Importance.HIGH)
	public void download_geopackage_result_test() throws InterruptedException, IOException {
		testJob = jobsWindow.singleJob("JobForTestingMask");
		download_geopackage_result("JobForTestingMask");
		moveToNonMaskJob();
		download_geopackage_result("JobForTestingNoMask");
	}
	
	public void download_geopackage_result(String jobName) throws InterruptedException, IOException {
		// Make sure that the "Download" Job button does something.  Selenium cannot tell if a download occurred.
		//assertEquals("There should not be a download link before clicking", null, testJob.downloadLink.getAttribute("href"));
		String home = System.getProperty("user.home");
<<<<<<< Updated upstream
		File file = new File(home+"/Downloads/"+"LC08_L1TP_185054_20180917_20180917_01_RT"+".gpkg");
=======
		File file = new File(home+"/Downloads/"+jobName+".gpkg");
>>>>>>> Stashed changes
		if(file.exists())
		{
			file.delete();
		}
		testJob.downloadButton().click();
		testJob.downloadLinkGeopkg().click();
		Thread.sleep(10000);
		if(browser.equalsIgnoreCase("firefox")){
			actions.sendKeys(Keys.ARROW_DOWN);
			actions.sendKeys(Keys.ARROW_DOWN);
			actions.sendKeys(Keys.ENTER);
			Thread.sleep(2000); 
		}else{
			driver.get("chrome://downloads");
			Thread.sleep(2000); 
			String getNumberOfDownloadsJS="function getNumDl() {"
			+"var list = document.querySelector('downloads-manager').shadowRoot.querySelector('#downloads-list').getElementsByTagName('downloads-item');"
			+"return list.length;};"
			+"return getNumDl()";
			long numberOfDownloads = (long)((JavascriptExecutor) driver).executeScript(getNumberOfDownloadsJS);
			System.out.println(numberOfDownloads);
			Assert.assertTrue("File shows in downloads",numberOfDownloads>0);
		}
		System.out.println(file.length());
		Assert.assertTrue("File Size is larger than 1kb",file.length()>1000);
		//Thread.sleep(2000);
		//Assert.assertTrue("Download path should appear after click", testJob.downloadLink.getAttribute("href").contains("blob"));
	}
	
	@Test @Info(importance = Importance.HIGH)
	public void download_shapefile_result_test() throws InterruptedException, IOException {
		testJob = jobsWindow.singleJob("JobForTestingMask");
		download_shapefile_result("JobForTestingMask");
		moveToNonMaskJob();
		download_shapefile_result("JobForTestingNoMask");
	}
	
	public void download_shapefile_result(String jobName) throws InterruptedException, IOException {
		// Make sure that the "Download" Job button does something.  Selenium cannot tell if a download occurred.
		//assertEquals("There should not be a download link before clicking", null, testJob.downloadLink.getAttribute("href"));
		String home = System.getProperty("user.home");
<<<<<<< Updated upstream
		File file = new File(home+"/Downloads/"+"LC08_L1TP_185054_20180917_20180917_01_RT"+".shp.zip");
=======
		File file = new File(home+"/Downloads/"+jobName+".shp.zip");
>>>>>>> Stashed changes
		if(file.exists())
		{
			file.delete();
		}
		testJob.downloadButton().click();
		testJob.downloadLinkShapefile().click();
		Thread.sleep(10000);
		if(browser.equalsIgnoreCase("firefox")){
			actions.sendKeys(Keys.ARROW_DOWN);
			actions.sendKeys(Keys.ARROW_DOWN);
			actions.sendKeys(Keys.ENTER);
			Thread.sleep(2000); 
		}else{
			driver.get("chrome://downloads");
			Thread.sleep(2000); 
			String getNumberOfDownloadsJS="function getNumDl() {"
			+"var list = document.querySelector('downloads-manager').shadowRoot.querySelector('#downloads-list').getElementsByTagName('downloads-item');"
			+"return list.length;};"
			+"return getNumDl()";
			long numberOfDownloads = (long)((JavascriptExecutor) driver).executeScript(getNumberOfDownloadsJS);
			System.out.println(numberOfDownloads);
			Assert.assertTrue("File shows in downloads",numberOfDownloads>0);	
		}
		System.out.println(file.length());
		Assert.assertTrue("File Size is larger than 1kb",file.length()>1000);
		//Thread.sleep(2000);
		//Assert.assertTrue("Download path should appear after click", testJob.downloadLink.getAttribute("href").contains("blob"));
	}
	
	@Test @Info(importance = Importance.LOW)
	public void forget_job_test() throws InterruptedException {
		testJob = jobsWindow.singleJob("JobForTestingMask");
		forget_job("JobForTestingMask");
		moveToNonMaskJob();
		forget_job("JobForTestingNoMask");
	}
	public void forget_job(String jobName) throws InterruptedException {

		// Click on test job.
		testJob.thisWindow.click();
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
<<<<<<< Updated upstream
		assertNull(bfMain.jobsWindow().singleJob("LC08_L1TP_185054_20180917_20180917_01_RT"));
=======
		assertNull(bfMain.jobsWindow().singleJob(jobName));
>>>>>>> Stashed changes
		driver.get(jobUrl);
	}

	private boolean isFileDownloaded_Ext(String dirPath, String ext){
 		boolean flag=false;
    File dir = new File(dirPath);
    File[] files = dir.listFiles();
    if (files == null || files.length == 0) {
        flag = false;
    	}
   
    for (int i = 1; i < files.length; i++) {
     	if(files[i].getName().contains(ext)) {
      	flag=true;
     	}
    	}
    	return flag;
	}
	/*
	@Test @Info(importance = Importance.LOW, bugs = {"5637"})
	public void bypass_confirmation() throws InterruptedException {
		// Try to bypass the the forget -> confirm process by directly clicking on confirm.	
		testJob.thisWindow.click();
		assertFalse("Should not be able to click 'confirm'", testJob.confirmButton.isDisplayed());
		
		// Try to bypass the the forget -> confirm process by tabbing to the confirm button.
		//actions.sendKeys(Keys.TAB, Keys.TAB, Keys.ENTER).build().perform(); fails in firefox
		Thread.sleep(1000);
		bfMain.jobsButton.click();
		assertTrue("Job should not be removed", Utils.checkExists(bfMain.jobsWindow().singleJob("LC08_L1TP_185054_20180917_20180917_01_RT").thisWindow));
	}
	*/
}
