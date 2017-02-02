package bfui.test;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class BfSearchWindowPage {

	@FindBy(css = "button[type=submit]")								public WebElement submitButton;
	@FindBy(css = "input[placeholder='Enter Coordinates']")				public WebElement entry;
	@FindBy(xpath = "/html/body/div/div/main/form/div[2]/dl/dd/code")	public List<WebElement> exampleCoordinates;
	@FindBy(className = "error-message")								public WebElement errorMessage;
	private SearchContextElementLocatorFactory findByParentFactory;
	

	
	public BfSearchWindowPage(WebElement parent) {
		findByParentFactory = new SearchContextElementLocatorFactory(parent);
		PageFactory.initElements(findByParentFactory, this);
	}
	
	public void searchCoordinates(String coordinates) {
		entry.clear();
		entry.sendKeys(coordinates);
		submitButton.click();
	}
	
	public ArrayList<String> getExamples() {
		ArrayList<String> exampleTexts = new ArrayList<String>();
		for (WebElement example : exampleCoordinates) {
			exampleTexts.add(example.getText());
		}
		return exampleTexts;
	}
}