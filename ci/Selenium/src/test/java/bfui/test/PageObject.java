package bfui.test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;

public class PageObject {
	
	private WebDriver driver;
	private WebDriverWait wait;
	private Map<String, By> elementFinder;
	private Map<String, String> parentFinder;
	private Map<String, By> childrenFinder;
	
	public PageObject(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, 5);
	}
	
	public WebElement getElement(String name) throws Exception {
		return getElement_INTERNAL(name, elementFinder, parentFinder, driver, wait);
	}
	
	protected WebElement getElement_INTERNAL(String name, Map<String, By> elementFinder, Map<String, String> parentFinder, WebDriver driver, WebDriverWait wait) throws Exception {
		if (elementFinder.containsKey(name)) {
			if (parentFinder.containsKey(name)) {
				WebElement parent = getElement(parentFinder.get(name));
				return Utils.assertElementLoads("The element '" + name + "' should load", parent, wait, elementFinder.get(name));
			} else {
				return Utils.assertElementLoads("The element '" + name + "' should load", driver, wait, elementFinder.get(name));
			}
		} else {
			throw new Exception("The key '" + name + "' does not exist in the element finder map.");
		}
	}
	
	public List<WebElement> getChildren(String parentName) throws Exception {
		return getChildren_INTERNAL(parentName, childrenFinder, elementFinder, parentFinder, driver, wait);
	}
	
	protected List<WebElement> getChildren_INTERNAL(String parentName, Map<String, By> childrenFinder, Map<String, By> elementFinder, Map<String, String> parentFinder, WebDriver driver, WebDriverWait wait) throws Exception {
		if (childrenFinder.containsKey(parentName)) {
			return getElement_INTERNAL(parentName, elementFinder, parentFinder, driver, wait).findElements(childrenFinder.get(parentName));
		} else {
			throw new Exception("The key '" + parentName + "' does not exist in the children finder map.");
		}
	}
}
