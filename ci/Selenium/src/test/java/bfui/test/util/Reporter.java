package bfui.test.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import bfui.test.util.Info.Importance;

public class Reporter extends TestWatcher {
	/*
	 * Allows use of @Importance(level = Level.[LOW, MEDIUM, or HIGH])
	 * This will display a list of failing methods at the end of the test suite.
	 * The failing methods will be marked by their importance.
	 */
	private static ArrayList<Description> failingTests = new ArrayList<Description>();
	private static int currentTest = 0;
	private static int totalTests = 0;
	private static JSONObject json;
	private static String url;
	
	public Reporter(String url) {
		Reporter.url = url;
	}
	
	@Override
	protected void starting(Description desc) {
		// Count the number of tests in the test class (only once per test class).
		if (currentTest == 0) {
			for (Method method : desc.getTestClass().getDeclaredMethods()) {
				if (method.isAnnotationPresent(Test.class)) {
					totalTests++;
				}
			}
			System.out.println("Total tests: " + totalTests);
			JSONObject collection = new JSONObject();
			collection.put("name", desc.getClassName());
			collection.put("requests", new JSONArray());
			json = new JSONObject();
			json.put("name", "N/A");
			json.put("results", new JSONArray());
			json.put("collection", collection);
		}
	}
	
	@Override
	protected void failed(Throwable e, Description desc) {
		String id = UUID.randomUUID().toString();
		
		JSONObject tests = new JSONObject();
		tests.put("Selenium Failure", false);
		
		JSONObject result = new JSONObject();
		result.put("id", id);
		result.put("name", desc.getMethodName());
		result.put("tests", tests);
		
		JSONObject request = new JSONObject();
		request.put("id", id);
		request.put("url", "");
		request.put("method", "");
		request.put("description", e.getMessage());
		
		json.append("results", result);
		json.getJSONObject("collection").append("requests", request);
		
		
		// After each failed test, record the description to be parsed later.
		failingTests.add(desc);
	}
	
	@Override
	protected void finished(Description desc) {
		// Count each test completed.  If all tests have been completed,
		// display the results and reset the static variables.
		currentTest++;
		if (currentTest == totalTests) {
			displayResults();
			failingTests = new ArrayList<Description>();
			currentTest = 0;
			totalTests = 0;
			System.out.println(json);
			
			System.out.println(sendResults(url, json));
		}
	}
	
	private String sendResults(String dashboardUrl, JSONObject json) {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(dashboardUrl);
		try {
			HttpEntity httpentity = new StringEntity(json.toString());
			httppost.setEntity(httpentity);
			return EntityUtils.toString(httpclient.execute(httppost).getEntity());
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	private void displayResults() {
		// Display the failing tests.  This will be replaced by a reporter in the future.
		int i = 0;
		System.out.println(" ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ");
		System.out.println("v v v v v v v v v v v v v v v v v v v v v v v v v v v v v v v v v");
		if (failingTests.isEmpty()) {
			System.out.println("                    !!! ALL TESTS PASSED !!!");
		}
		
		for (Description failure : failingTests) {
			i++;
			System.out.println(String.format("%2d. %6s failure: %s%s",
					i,
					getLevel(failure),
					failure.getMethodName(),
					getBugs(failure)));
		}
		System.out.println("^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^");
		System.out.println(" ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ");
	}
	
	private Importance getLevel(Description desc) {
		// Get the Importance Level of a test, returning NONE if no importance was set.
		if (hasInfo(desc)) {
			return desc.getAnnotation(Info.class).importance();
		} else {
			return Importance.NONE;
		}
	}
	
	private String getBugs(Description desc) {
		// Get a list of bugs in a string, if there are any associated with the test.
		String bugString = "";
		if (hasInfo(desc) && getInfo(desc).bugs().length > 0) {
			bugString += " ~ Bugs: " + StringUtils.join(getInfo(desc).bugs());
		}
		return bugString;
	}
	
	private Info getInfo(Description desc) {
		return desc.getAnnotation(Info.class);
	}
	
	private boolean hasInfo(Description desc) {
		return getInfo(desc) != null;
	}
}