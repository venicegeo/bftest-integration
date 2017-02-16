package bfui.test;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import bfui.test.Importance.Level;

public class ImportanceReporter extends TestWatcher {
	private static ArrayList<Description> failingTests = new ArrayList<Description>();
	private static int currentTest = 0;
	private static int totalTests = 0;
	
	@Override
	protected void failed(Throwable e, Description desc) {
		failingTests.add(desc);
	}
	
	@Override
	protected void finished(Description desc) {
		currentTest++;
		if (currentTest == totalTests) {
			displayResults();
			// Clear out before next suite.
			failingTests = new ArrayList<Description>();
			currentTest = 0;
			totalTests = 0;
		}
	}
	
	@Override
	protected void starting(Description desc) {
		// Count @Test annotations:
		if (currentTest == 0) {
			for (Method method : desc.getTestClass().getDeclaredMethods()) {
				if (method.isAnnotationPresent(Test.class)) {
					totalTests++;
				}
			}
			System.out.println("Total tests: " + totalTests);
		}
	}
	
	public static ArrayList<Description> getFailingTests() {
		System.out.println("I'm getting the failing tests!");
		return failingTests;
	}
	
	private void displayResults() {
		int i = 0;
		for (Description failure : failingTests) {
			i++;
			System.out.println(String.format("%d. %6s failure: %s",
					i,
					getLevel(failure),
					failure.getMethodName()));
		}
	}
	
	private Level getLevel(Description desc) {
		if (desc.getAnnotation(Importance.class) != null) {
			return desc.getAnnotation(Importance.class).level();
		} else {
			return Level.NONE;
		}
	}
}
