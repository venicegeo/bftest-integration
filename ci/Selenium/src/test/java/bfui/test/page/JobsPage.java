package bfui.test.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import bfui.test.page.core.PageObject;

public class JobsPage extends PageObject {
	@FindBy(tagName = "ul")
	public WebElement list;

	public JobsPage(WebDriver driver) {
		super(driver);
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