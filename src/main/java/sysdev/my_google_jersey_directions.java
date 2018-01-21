/**
 * 
 */
package sysdev;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.Geometry;
import org.geojson.LineString;
import org.geojson.LngLatAlt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * @author pierre
 *
 */
import sysdev.graph.DijkstraAlgorithm;
import sysdev.graph.FillGraph;
import sysdev.graph.Graph;
import sysdev.graph.Node;

/**
 * Root resource (exposed at "myresource" path)
 */

@Path("services/directions")
public class my_google_jersey_directions {

	private static final String GOOGLE_DIRECTIONS = "https://maps.googleapis.com/maps/api/directions/json?";
	private static final String GOOGLE_KEY = "key=AIzaSyDD3mIAFIetKZUVJ8EGXxBU4iBDu4w93IU";
	private FillGraph fg = new sysdev.graph.FillGraph();
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
		System.out.println(output);
		return output;
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/dijkstra")
	public String dijkstraShortestPath(@QueryParam("originLat") double originLat,
			@QueryParam("originLon") double originLon, @QueryParam("destinationLat") double destinationLat,
			@QueryParam("destinationLon") double destinationLon) {

		String output = "";
		try {

			DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
			Node nearest_origin = graph.nearest_node(new Node(originLat, originLon));
			Node nearest_dest = graph.nearest_node(new Node(destinationLat, destinationLon));
			dijkstra.execute(nearest_origin, nearest_dest);
			LinkedList<Node> path = dijkstra.getPath(nearest_dest);
			FeatureCollection featureCollection = new FeatureCollection();
			Feature feature = new Feature();
			List<LngLatAlt> LngLatList_path = new ArrayList<LngLatAlt>();
			for (Node node : path) {
				LngLatAlt lngLatAlt = new LngLatAlt(node.getLon(), node.getLat());
				LngLatList_path.add(lngLatAlt);
			}
			Geometry<LngLatAlt> opt_linestring = new LineString();
			opt_linestring.setCoordinates(LngLatList_path);
			feature.setGeometry(opt_linestring);
			Map<String, Object> costs = new HashMap<String, Object>();
			// DecimalFormat df = new DecimalFormat("#.###");
			System.out.println("Mystat");
			System.out.println(dijkstra.getPath_duration());
			costs.put("Distance", (int) dijkstra.getPath_distance());
			costs.put("Travel_Time", (int) (dijkstra.getPath_duration()));
			feature.setProperty("costs", costs);
			featureCollection.add(feature);

			output = new ObjectMapper().writeValueAsString(featureCollection);
			output = "{\"data\": " + output + "}";
			System.out.println(output);

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return output;
	}

	public static String buildDirectionQueryString(double lat_o, double lon_o, double lat_d, double lon_d) {
		String origin = "origin=" + lat_o + "," + lon_o;
		String destination = "destination=" + lat_d + "," + lon_d;
		return GOOGLE_DIRECTIONS + origin + "&" + destination + "&" + GOOGLE_KEY;
	}

}
