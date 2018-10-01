package bfui.test.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import bfui.test.util.SearchContextElementLocatorFactory;

public class JobsPage {
	public WebElement thisWindow;

	@FindBy(tagName = "ul")		public WebElement list;
	
	private SearchContextElementLocatorFactory findByParentFactory;

	public  JobsPage(WebElement parent) {
		findByParentFactory = new SearchContextElementLocatorFactory(parent);
		PageFactory.initElements(findByParentFactory, this);
		thisWindow = parent;
	}
	
	public JobStatusPage singleJob(String name) {
		JobStatusPage jobPage;
		for (WebElement job : list.findElements(By.className("JobStatus-root"))) {
			jobPage = new JobStatusPage(job);
			if (jobPage.getName().equals(name)) {
				return jobPage;
			}
		}
		return null;
	}
}