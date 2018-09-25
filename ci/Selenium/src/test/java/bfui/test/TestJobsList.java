package bfui.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import bfui.test.page.BfJobsWindowPage;
import bfui.test.page.BfMainPage;
import bfui.test.page.BfSingleJobPage;
import bfui.test.page.GxLoginPage;
import bfui.test.page.LoginPage;
import bfui.test.util.Info;
import bfui.test.util.Info.Importance;
import bfui.test.util.Reporter;
//import bfui.test.util.SauceResultReporter;
import bfui.test.util.Utils;

public class TestJobsList {
	private WebDriver driver;
	private Actions actions;
	private WebDriverWait wait;
	private BfMainPage bfMain;
	private BfJobsWindowPage jobsWindow;
	private BfSingleJobPage testJob;
	private LoginPage login;
	private String jobUrl;
	
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


	
	@Rule
	public Reporter reporter = new Reporter("http://dashboard.venicegeo.io/cgi-bin/bf_ui_" + browser + "/" + space + "/load.pl");
	@Rule
	public TestName name = new TestName();
	//@Rule
	//public SauceResultReporter sauce = new SauceResultReporter();
	
	@Before
	public void setUp() throws Exception {
		// Setup Browser:
		driver = new Utils().getBuiltInChromeDriver();
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
		bfMain.rememberJob(space, driver.getCurrentUrl());
		// Open Job Window:
		bfMain.jobsButton.click();
		
		Utils.scrollInToView(driver, bfMain.canvas);
		actions.moveToElement(bfMain.canvas).build().perform(); // Move mouse to clear title text (that may obscure jobs list)
		jobsWindow = bfMain.jobsWindow();
		testJob = jobsWindow.singleJob("ForJobTesting");
		
	}

	@After
	public void tearDown() {
		driver.quit();
	}
	
	@Test @Info(importance = Importance.MEDIUM)
	public void view_on_map() {
		// Make sure that the "View On Map" Job button navigates the canvas to that Job's location.
		testJob.viewLink.click();
		Utils.assertPointInRange(bfMain.getCoords(), new Point2D.Double(-123.83, 38.95), 10);
	}
	
	@Test @Info(importance = Importance.HIGH)
	public void download_geojson_result() throws InterruptedException, IOException {
		// Make sure that the "Download" Job button does something.  Selenium cannot tell if a download occurred.
		//assertEquals("There should not be a download link before clicking", null, testJob.downloadLink.getAttribute("href"));
		String home = System.getProperty("user.home");
		File file = new File(home+"/Downloads/"+"ForJobTesting"+".geojson");
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
	public void download_geopackage_result() throws InterruptedException, IOException {
		// Make sure that the "Download" Job button does something.  Selenium cannot tell if a download occurred.
		//assertEquals("There should not be a download link before clicking", null, testJob.downloadLink.getAttribute("href"));
		String home = System.getProperty("user.home");
		File file = new File(home+"/Downloads/"+"ForJobTesting"+".gpkg");
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
	public void download_shapefile_result() throws InterruptedException, IOException {
		// Make sure that the "Download" Job button does something.  Selenium cannot tell if a download occurred.
		//assertEquals("There should not be a download link before clicking", null, testJob.downloadLink.getAttribute("href"));
		String home = System.getProperty("user.home");
		File file = new File(home+"/Downloads/"+"ForJobTesting"+".shp.zip");
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
	public void forget_job() throws InterruptedException {

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
		assertNull(bfMain.jobsWindow().singleJob("ForJobTesting"));
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
		assertTrue("Job should not be removed", Utils.checkExists(bfMain.jobsWindow().singleJob("ForJobTesting").thisWindow));
	}
	*/
}
