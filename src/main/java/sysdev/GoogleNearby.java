package sysdev;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.ws.rs.core.MediaType;

public class GoogleNearby {
	// Just an example of how to use google api

	private static final String GOOGLE_NEARBY = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
	private static final String GOOGLE_KEY = "key=AIzaSyDAu_Eic5g_0MbPVAXAKvjF5Uq39iDjR8Q";

	public static void nearbySearchInternal(double lat, double lon, int rad, String t) {
		String response = "";
		try {
			URL url = new URL(buildNearbyQueryString(lat, lon, rad, t));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String nextLine;
			while ((nextLine = br.readLine()) != null) {
				response += nextLine;
			}
			System.out.println(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void nearbySearchJersey(double lat, double lon, int rad, String t) {
		Client client = Client.create();
		WebResource webResource = client.resource(buildNearbyQueryString(lat, lon, rad, t));
		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed: HTTP error code: " + response.getStatus());
		}
		String responseString = response.getEntity(String.class);
		System.out.println(responseString);
	}

	public static String buildNearbyQueryString(double lat, double lon, int rad, String t) {
		String location = "location=" + lat + "," + lon;
		String radius = "radius=" + rad;
		String type = "type=" + t;
		return GOOGLE_NEARBY + location + "&" + radius + "&" + type + "&" + GOOGLE_KEY;
	}
}
