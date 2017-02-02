package bfui.test;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GX extends PageObject {
	
	private WebDriver driver;
	private WebDriverWait wait;
	private Map<String, By> elementFinder;
	private Map<String, String> parentFinder;

	
	public GX(WebDriver driver) {
		super(driver);
		this.driver = driver;
		wait = new WebDriverWait(driver, 5);
		
		// "Element Name" -> "By" map:
		Map<String, By> ef = new HashMap<String, By>();

		ef.put("authLinksWindow",	By.id("authmechlinks"));

		ef.put("disadvantagedLink",	By.xpath("//*[contains(text(), 'Disadvantaged Users')]"));

		ef.put("userField",			By.id("username"));
		ef.put("pwField",			By.id("password"));

		ef.put("submitButton",		By.cssSelector("input[type=submit]"));
		
		elementFinder = ef;
		
		
		// "Element Name" -> "Parent Element" map:
		Map<String, String> pf = new HashMap<String, String>();
		pf.put("disadvantagedLink",	"authLinksWindow");
		parentFinder = pf;
		
	}
	
	@Override
	public WebElement getElement(String name) throws Exception {
		return getElement_INTERNAL(name, elementFinder, parentFinder, driver, wait);
	}

}
