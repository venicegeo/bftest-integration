package bfui.test.page;

import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import bfui.test.page.core.PageObject;

/**
 * The Jobs list
 */
public class JobsPage extends PageObject {
	/* @formatter:off */
	@FindBy(className = "JobStatusList-root")	public WebElement list;
	/* @formatter:on */

	public JobsPage(WebDriver driver) {
		super(driver);
	}

	/**
	 * Gets the Job Status Page for a single Job. This corresponds to a single entry in the Jobs list.
	 * 
	 * @param name
	 *            The name of the job
	 * @return The Job Status Page for the selected job
	 */
	public JobStatusPage getJobStatus(String name) {
		for (WebElement job : list.findElements(By.className("JobStatus-root"))) {
			JobStatusPage jobPage = new JobStatusPage(driver, job);
			if (jobPage.getName().equals(name)) {
				return jobPage;
			}
		}
		throw new NotFoundException(String.format("Could not find Job Status for Job named %s", name));
	}

	/**
	 * Returns true if the URL matches the patch for the Jobs page
	 * 
	 * @return True if the jobs Page URL matches, false if not
	 */
	public boolean isJobsPageUrlActive() {
		return driver.getCurrentUrl().contains("jobs?");
	}
}