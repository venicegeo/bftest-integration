package bfui.test.page;

import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import bfui.test.util.Utils;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BfMainPage {
	
	WebDriver driver;
	Actions actions;
	WebDriverWait wait;
	private String browser = System.getenv("browser");
	@FindBy(className = "BrowserSupport-root")				public WebElement browserSupportWindow;
	@FindBy(className = "BrowserSupport-close")				public WebElement browserSupportDismiss;
	@FindBy(className = "Login-button")						public WebElement geoAxisLink;
	@FindBy(className = "Login-warning")					public WebElement consentBanner;
	@FindBy(className = "PrimaryMap-logout")				public WebElement logoutButton;
	@FindBy(className = "Navigation-linkHome")				public WebElement homeButton;
	@FindBy(className = "Navigation-linkHelp")				public WebElement helpButton;
	@FindBy(className = "Navigation-linkJobs")				public WebElement jobsButton;
	@FindBy(className = "Navigation-linkCreateJob")			public WebElement createJobButton;
	@FindBy(className = "Navigation-linkProductLines")		public WebElement productLinesButton;
	@FindBy(className = "Navigation-linkCreateProductLine")	public WebElement createProductLineButton;
	@FindBy(className = "PrimaryMap-search")				public WebElement searchButton;
	@FindBy(className = "PrimaryMap-measure")				public WebElement measureButton;
	@FindBy(className = "ol-zoom-in")						public WebElement zoomInButton;
	@FindBy(className = "ol-zoom-out")						public WebElement zoomOutButton;
	@FindBy(className = "ol-zoomslider-thumb")				public WebElement zoomSliderButton;
	@FindBy(className = "ol-zoomslider")					public WebElement zoomSlider;
	@FindBy(className = "ol-mouse-position")				public WebElement mouseoverCoordinates;
	@FindBy(className = "ol-unselectable")					public WebElement canvas;
	@FindBy(className = "coordinate-dialog")				public WebElement searchWindow;
	@FindBy(className = "measure-dialog")					public WebElement measureWindow;
	@FindBy(className = "CreateJob-root")					public WebElement createJobWindow;
	@FindBy(className = "JobStatusList-root")				public WebElement jobsWindow;
	@FindBy(className = "FeatureDetails-root")				public WebElement featureDetails;
	@FindBy(className = "SessionExpired-root")				public WebElement sessionExpiredOverlay;
	@FindBy(className = "SessionLoggedOut-root")			public WebElement loggedOutOverlay;
	@FindBy(className = "ClassificationBanner-root")		public List<WebElement> banners;
	
	
	@FindBy(xpath = "//div[contains(@class, 'SceneFeatureDetails-root')			]/child::dl")	public WebElement detailTable;
	private Scanner sc;
	private String apiKeyPlanet = System.getenv("PL_API_KEY");
	
	public BfMainPage(WebDriver driver, WebDriverWait wait) {
		PageFactory.initElements(driver, this);
		actions = new Actions(driver);
		this.driver = driver;
		this.wait = wait;
	}
	
	public BfSearchWindowPage searchWindow() {
		return new BfSearchWindowPage(searchWindow);
	}
	
	public BfMeasureWindowPage measureWindow() {
		Utils.assertBecomesVisible("Measure Tool window should be present", measureWindow, wait);
		return new BfMeasureWindowPage(measureWindow);
	}
	
	public BfCreateJobWindowPage createJobWindow() {
		return new BfCreateJobWindowPage(createJobWindow);
	}
	
	public BfJobsWindowPage jobsWindow() {
		return new BfJobsWindowPage(jobsWindow);
	}
	
	public void rememberJob(String space, String returnUrl) throws Exception {
		OkHttpClient client;
		MediaType mediaType;
		RequestBody body;
		Request request;
		Response response;
		String rxVisitor = new String();
		String apiKey = new String();
		Set<Cookie> rxVisitorCK;
		Cookie apiKeyCK;
		rxVisitorCK = driver.manage().getCookies();
		rxVisitor = rxVisitorCK.toString();
		apiKeyCK = driver.manage().getCookieNamed("api_key");
		apiKey = apiKeyCK.getValue();
		switch (space) {	
		case "pz-int":
		 client = new OkHttpClient();
		   mediaType = MediaType.parse("application/json");
		   body = RequestBody.create(mediaType, "{\"algorithm_id\":\"e68cb98d-9109-44b5-9a5b-701f994ff57f\",\"compute_mask\":false,\"name\":\"ForJobTesting\",\"planet_api_key\":\""+apiKeyPlanet+"\",\"scene_id\":\"landsat:LC81412132016283LGN00\"}");
		   request = new Request.Builder()
		  .url("https://bf-api.int.dev.east.paas.geointservices.io/job")
		  .post(body)
		  .addHeader("content-type", "application/json")
		  .addHeader("Authorization", "Basic Og==")
		  .addHeader("Cookie", "api_key="+apiKey)
		  .build();
		    response = client.newCall(request).execute();
		    System.out.println("rxVisitor="+rxVisitor+"; "+"api_key=\""+apiKey+"\"");
		    System.out.println(response.message());
			Thread.sleep(10000);
			driver.get(returnUrl);
			break;
		case "pz-prod":
		    client = new OkHttpClient();
		    mediaType = MediaType.parse("application/json");
		    body = RequestBody.create(mediaType, "{\"algorithm_id\":\"e68cb98d-9109-44b5-9a5b-701f994ff57f\",\"compute_mask\":false,\"name\":\"ForJobTesting\",\"planet_api_key\":\""+apiKeyPlanet+"\",\"scene_id\":\"landsat:LC81412132016283LGN00\"}");
		    request = new Request.Builder()
		   .url("https://bf-api.geointservices.io/job")
		   .post(body)
			  .addHeader("content-type", "application/json")
			  .addHeader("Authorization", "Basic Og==")
			  .addHeader("Cookie", "api_key="+apiKey)
		   .build();
		   response = client.newCall(request).execute();
		   System.out.println("rxVisitor="+rxVisitor+"; "+"api_key="+apiKey);
		   System.out.println(response.message());
		  Thread.sleep(10000);
		  driver.get(returnUrl);
		  break;
		case "pz-test":
		   client = new OkHttpClient();
		   mediaType = MediaType.parse("application/json");
		   body = RequestBody.create(mediaType, "{\"algorithm_id\":\"e68cb98d-9109-44b5-9a5b-701f994ff57f\",\"compute_mask\":false,\"name\":\"ForJobTesting\",\"planet_api_key\":\""+apiKeyPlanet+"\",\"scene_id\":\"landsat:LC81412132016283LGN00\"}");
		   request = new Request.Builder()
		  .url("https://bf-api.test.dev.east.paas.geointservices.io/job")
		  .post(body)
		  .addHeader("content-type", "application/json")
		  .build();
		   response = client.newCall(request).execute();
		   System.out.println("rxVisitor="+rxVisitor+"; "+"api_key="+apiKey);
		   System.out.println(response.message());
		  Thread.sleep(10000);
		  driver.get(returnUrl);
		  break;
		case "int":
			driver.get("https://bf-api.int.geointservices.io/v0/job/ce304378-de52-430c-ab69-ae54b1db538e");
			Thread.sleep(3000);
			driver.get(returnUrl);
			break;
		case "stage":
			driver.get("https://bf-api.stage.geointservices.io/v0/job/d2de0718-4374-43e4-82cd-70fbc2a5a7a4");
			Thread.sleep(3000);
			driver.get(returnUrl);
			break;
		case "prod":
			driver.get("https://bf-api.geointservices.io/v0/job/fc992acc-34f3-4bf9-8d98-f05cb79df251");
			Thread.sleep(3000);
			driver.get(returnUrl);
			break;
		case "coastline":
			driver.get("https://coastline-api.apps.coastline.dg-cf-test.net/v0/job/edb34981-187c-4260-8996-cbbdd1e68e3d");
			Thread.sleep(3000);
			driver.get(returnUrl);
			break;
		default:
			throw new Exception("Test not configured for space, '" + space + "'.");
		}
	}
	
	public double zoomSliderValue() {
		String rx = "\\d+\\.?\\d*";
		Matcher m = Pattern.compile(rx).matcher(zoomSliderButton.getAttribute("style"));
		
		if (m.find()) {
			return -Double.parseDouble(m.group());
		} else {
			return 0;
		}
	}
	
	public void tryToClickJobs() {
		try {
			jobsButton.click();
		} catch (NoSuchElementException e) {
			throw e;
		}
	}
	
	public void drawBoundingBox(Actions actions, int x1, int y1, int x2, int y2) throws InterruptedException {
		Utils.scrollInToView(driver, canvas);
		if(browser.equalsIgnoreCase("firefox")){
			actions.moveToElement(canvas, x1, y1).click().build().perform();
		}else{
			actions.moveToElement(canvas, x1, y1).click().build().perform();
		}
		Thread.sleep(1000);
		actions.moveByOffset(x2 - x1, y2 - y1).click().build().perform();
	}
	public void drawBoundingBox(Actions actions, Point start, Point end) throws InterruptedException {
		drawBoundingBox(actions, start.x, start.y, end.x, end.y);
	}
	
	public void loginSongAndDance(String intUrl) throws InterruptedException {
		int i = 0;
		while (Utils.checkNotExists(sessionExpiredOverlay) && i < 10) {
			Thread.sleep(1000);
			i++;
		}
		if (Utils.checkExists(sessionExpiredOverlay)) {
			sessionExpiredOverlay.click();
			Thread.sleep(5000);
		}
		if (Utils.checkExists(geoAxisLink)) {
			geoAxisLink.click();
			Thread.sleep(5000);
		}
		driver.get(intUrl + "?logged_in=true");
	}
	
//	public double getMouseoverLatitude() {
//		String coordinates = mouseoverCoordinates.getText();
//		Pattern p;
//		Matcher m;
//		int sign;
//		p = Pattern.compile("(\\d+)(?=�[^EW]*[NS])");
//		if (coordinates.indexOf('N') >= 0) {
//			sign = 1;
//		} else {
//			sign = -1;
//		}
//		m = p.matcher(coordinates);
//		assertTrue("Should be able to parse latitude from the mouseover coordinates", m.find());
//		return sign*Double.parseDouble(m.group());
//	}
	
//	public double getMouseoverLongitude() {
//		String coordinates = mouseoverCoordinates.getText();
//		Pattern p;
//		Matcher m;
//		int sign;
//		p = Pattern.compile("(\\d+)(?=�[^NS]*[EW])");
//		if (coordinates.indexOf('E') >= 0) {
//			sign = 1;
//		} else {
//			sign = -1;
//		}
//		m = p.matcher(coordinates);
//		assertTrue("Should be able to parse longitude from the mouseover coordinates", m.find());
//		return sign*Double.parseDouble(m.group());
//	}
	
	public double getCoord(int coord) {
		/*
		 * THIS USES AN INTERNAL API THAT IS SUBJECT TO CHANGE WITHOUT WARNING�
		 */
		JavascriptExecutor js = (JavascriptExecutor) driver;
		return ((Number) ((ArrayList) js.executeScript("return primaryMap.props.view.center")).get(coord)).doubleValue();
		/*
		 * THIS USES AN INTERNAL API THAT IS SUBJECT TO CHANGE WITHOUT WARNING�
		 */
	}
	
	public Point2D.Double getCoords() {
//		String js2ex =	"var keyList = Object.keys(window);" +
//						"var outputString = '';" +
//						"for (var i = 0; i < keyList.length; i++) {" +
//							"var thisObj = window[keyList[i]];" +
//							"if (thisObj !== null && typeof thisObj === 'object') {" +
//								"outputString += keyList[i] + ': ' + Object.getOwnPropertyNames(thisObj) + '\\n';" +
//							"} else {" +
//								"outputString += keyList[i] + ': ---\\n';" +
//							"}" +
//						"}" +
//						"return outputString;";
//		System.out.println(js2ex);
		
		/*
		 * THIS USES AN INTERNAL API THAT IS SUBJECT TO CHANGE WITHOUT WARNING�
		 */
		JavascriptExecutor js = (JavascriptExecutor) driver;
//		System.out.println(js.executeScript(js2ex));
		ArrayList<Number> result = ((ArrayList) js.executeScript("return window.primaryMap.props.view.center"));
		System.out.println(result.get(0).doubleValue());
		System.out.println(result.get(1).doubleValue());
		return new Point2D.Double(result.get(0).doubleValue(), result.get(1).doubleValue());
		/*
		 * THIS USES AN INTERNAL API THAT IS SUBJECT TO CHANGE WITHOUT WARNING�
		 */
		
	}
	
	public int getFeatureCloudCover() {
		WebElement cloudCoverContainer = Utils.getTableData(featureDetails, "CLOUD COVER");
		int returnedInt = Integer.parseInt(cloudCoverContainer.getText().replaceFirst("%", ""));
		return returnedInt;
	}
	
	public boolean clickUntilResultFound(Point start, Point end, Point step, Actions actions) throws InterruptedException {
		Point currentPos = new Point(start.x, start.y);
		Utils.scrollInToView(driver, canvas);
		actions.moveToElement(canvas, start.x, start.y).build().perform();
		boolean found = false;
		while (!found) {
			actions.moveByOffset(step.x, step.y).click().build().perform();
			currentPos = currentPos.moveBy(step.x, step.y);
			found = Utils.checkExists(driver.findElement(By.className("Algorithm-startButton")));
			if (!inRange(currentPos, start, end)) {
				break;
			} else {
				Thread.sleep(1000);
			}
		}
		return found;
	}
	
	private boolean inRange(Point point, Point boundOne, Point boundTwo) {
		double maxX = Math.max(boundOne.x, boundTwo.x);
		double minX = Math.min(boundOne.x, boundTwo.x);
		double maxY = Math.max(boundOne.y, boundTwo.y);
		double minY = Math.min(boundOne.y, boundTwo.y);
		
		return point.x >= minX && point.x <= maxX && point.y >= minY && point.y <= maxY;
	}
	
	public boolean isBetweenBanners(WebElement element) {
		int bottomOfTopBanner = banners.get(0).getLocation().y + banners.get(0).getSize().height;
		int topOfBottomBanner = banners.get(1).getLocation().y;
		int topOfElement = element.getLocation().y;
		int bottomOfElement = element.getLocation().y + element.getSize().height;
		return topOfElement >= bottomOfTopBanner && bottomOfElement <= topOfBottomBanner;
	}
	
	public int topBannerPos() {
		int highestPos = 9999;
		for (WebElement banner : banners) {
			highestPos = Math.min(banner.getLocation().y, highestPos);
		}
		return highestPos;
	}
	
	public int bottomBannerPos() {
		int lowestPos = -1;
		for (WebElement banner : banners) {
			lowestPos = Math.max(banner.getLocation().y + banner.getSize().height, lowestPos);
		}
		return lowestPos;
	}
	
	public void pan(int x, int y) throws InterruptedException {
		Utils.scrollInToView(driver, canvas);
		actions.moveToElement(canvas, 500, 200).click().clickAndHold().moveByOffset(1, 1).moveByOffset(x, y).release().build().perform();
		Thread.sleep(2000);
	}
}
