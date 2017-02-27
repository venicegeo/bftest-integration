package bfui.test.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jboss.netty.handler.codec.base64.Base64Encoder;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.w3c.dom.Entity;

public class SauceResultReporter extends TestWatcher {
	
	private static String sessionId;
	
	@Override
	protected void failed(Throwable e, Description desc) {
		try {
			sendResult(false);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	@Override
	protected void succeeded(Description desc) {
		try {
			sendResult(true);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendResult(Boolean result) throws URISyntaxException, ClientProtocolException, IOException {
		HttpClient client = HttpClients.createDefault();
		String user = System.getenv("sauce_user");
		String key = System.getenv("sauce_key");
		String auth = Base64.getEncoder().encodeToString((user + ":" + key).getBytes());
		URL url = new URL("https://saucelabs.com/rest/v1/" + user + "/jobs/" + sessionId);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "Basic " + auth);
		OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
        osw.write("{\"passed\": " + result + "}");
        osw.close();
        System.out.println(connection.getInputStream());
	}
	
	public static void setSession(SessionId sessionId) {
		SauceResultReporter.sessionId = sessionId.toString();
		System.out.println(sessionId);
	}
}
