package bfui.test.page;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import bfui.test.util.SearchContextElementLocatorFactory;

public class BfJobsWindowPage {
	public WebElement thisWindow;

	@FindBy(tagName = "ul")																					public WebElement list;
	
	private SearchContextElementLocatorFactory findByParentFactory;

	public  BfJobsWindowPage(WebElement parent) {
		findByParentFactory = new SearchContextElementLocatorFactory(parent);
		PageFactory.initElements(findByParentFactory, this);
		thisWindow = parent;
	}
	
	public BfSingleJobPage singleJob(String name) {
		BfSingleJobPage jobPage;
		for (WebElement job : list.findElements(By.className("JobStatus-root"))) {
			jobPage = new BfSingleJobPage(job);
			if (jobPage.getName().equals(name)) {
				return jobPage;
			}
		}
		return null;
	}
}