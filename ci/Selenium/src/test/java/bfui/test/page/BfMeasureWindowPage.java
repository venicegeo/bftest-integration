package bfui.test.page;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import bfui.test.util.SearchContextElementLocatorFactory;

public class BfMeasureWindowPage {
	public WebElement thisWindow;
	

	@FindBy(className = "measureControl__distance")		public WebElement measurement;
	@FindBy(className = "measureControl__units")		public WebElement units;
	@FindBy(className = "measureControl__close")		public WebElement closeButton;
	
	
	private SearchContextElementLocatorFactory findByParentFactory;
	
	public BfMeasureWindowPage(WebElement parent) {
		findByParentFactory = new SearchContextElementLocatorFactory(parent);
		PageFactory.initElements(findByParentFactory, this);
		thisWindow = parent;
	}
	
	public double getMeasurement() {
		return Double.parseDouble(measurement.getText());
	}
	
	public void selectUnits(String selectedUnits) {
		(new Select(units)).selectByVisibleText(selectedUnits);
	}
}