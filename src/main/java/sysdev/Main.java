package sysdev;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.glassfish.grizzly.http.server.HttpServer;

/**
 * /**
 * 
 * @author pierre
 *
 */
public class Main {

	private static HttpServer server;
	private static WebTarget target;
	private ObjectMapper mapper = new ObjectMapper();

	public void setUp() throws Exception {

	}

	// Handle shutdown of the server
	public void tearDown() throws Exception {
		server.shutdown(); // No stop, but shutdown
	}

	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {

		// Start my Jersey Server
		server = SysDevJerseyServer.startServer("http://localhost:9090/sysdev/");
		Client c = ClientBuilder.newClient();
		// Define Target
		target = c.target("http://localhost:9090/sysdev/");
		// Test optionality
		/*String output = target.path("services/directions/uri").queryParam("originLat", 48.96670) // Hainsfarth
				.queryParam("originLon", 10.61670).queryParam("destinationLat", 48.24896) // Garching
				.queryParam("destinationLon", 11.65101).request().get(String.class);
		System.out.println(output);
		System.out.println("Home Sweet Home :)"); */
		
		// server.shutdown(); // No stop, but shutdown

	}

}
