/**
 * 
 */
package sysdev;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import sysdev.graph.FillGraph;
import sysdev.graph.Graph;

/**
 * Root resource (exposed at "myresource" path)
 */

@Path("services/directions")
public class my_google_jersey_directions {

	private static final String GOOGLE_DIRECTIONS = "https://maps.googleapis.com/maps/api/directions/json?";
	private static final String GOOGLE_KEY = "key=AIzaSyDD3mIAFIetKZUVJ8EGXxBU4iBDu4w93IU";
	private FillGraph fg = new sysdev.graph.FillGraph(); // Load graph only once
	private Graph graph = fg.getGraph();

	public static String jersey_path(double lat_o, double lon_o, double lat_d, double lon_d) {
		try {
			Client jersey_client = Client.create();
			WebResource webresource = jersey_client.resource(buildDirectionQueryString(lat_o, lon_o, lat_d, lon_d));
			ClientResponse web_response = webresource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
			if (web_response.getStatus() != 200) {
				throw new RuntimeException("Failed: HTTP error code: " + web_response.getStatus());
			}
			String responseString = web_response.getEntity(String.class);
			return responseString;
		} catch (Exception e) {

			e.printStackTrace();
			return "";

		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/uri")
	public String directionURI(@QueryParam("originLat") double originLat, @QueryParam("originLon") double originLon,
			@QueryParam("destinationLat") double destinationLat, @QueryParam("destinationLon") double destinationLon) {
		String output = jersey_path(originLat, originLon, destinationLat, destinationLon);
		// System.out.println(output);
		return output;
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/dijkstra")
	public String dijkstraShortestPath(@QueryParam("originLat") double originLat,
			@QueryParam("originLon") double originLon, @QueryParam("destinationLat") double destinationLat,
			@QueryParam("destinationLon") double destinationLon) {

        Socket socket =null; 
        try {
            socket = new Socket("localhost", 9595);
           
        DataOutputStream  oos = new DataOutputStream(socket.getOutputStream());
        
        oos.writeDouble(originLat);
        oos.writeDouble(originLon);
        oos.writeDouble(destinationLat);
        oos.writeDouble(destinationLon);
        oos.flush();
        
        DataInputStream ois = new DataInputStream(socket.getInputStream());
        String response = (String) ois.readUTF();
        System.out.println(response);
        return response;

        } catch (UnknownHostException e) {
            System.out.println("Unknown Host...");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOProbleme...");
            e.printStackTrace();
        } finally {
            if (socket != null)
                try {
                    socket.close();
                    System.out.println("Socket geschlossen...");
                    
                    
                } catch (IOException e) {
                    System.out.println("Socket nicht zu schliessen...");
                    e.printStackTrace();
                }
        }
		return null; 
    }

	public static String buildDirectionQueryString(double lat_o, double lon_o, double lat_d, double lon_d) {
		String origin = "origin=" + lat_o + "," + lon_o;
		String destination = "destination=" + lat_d + "," + lon_d;
		return GOOGLE_DIRECTIONS + origin + "&" + destination + "&" + GOOGLE_KEY;
	}

}
