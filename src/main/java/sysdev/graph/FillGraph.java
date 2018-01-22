package sysdev.graph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.geojson.LineString;
import org.geojson.LngLatAlt;
import org.geojson.MultiPoint;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FillGraph {

	private List<Edge> edges;
	private HashMap<String, Node> nodes;
	private Graph graph;

	public FillGraph() {
		// Fill up graph at initialization, so no call is needed later
		this.setGraph(this.read_in_json());
	}

	public Graph read_in_json() {
		nodes = new HashMap<String, Node>();
		edges = new ArrayList<Edge>();
		try {
			// Get json data
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("geojson/schleswig-holstein.json").getFile());
			FeatureCollection featureCollection = new ObjectMapper().readValue(file, FeatureCollection.class);
			List<Feature> features = featureCollection.getFeatures();
			// Loop through each feature from json and add it to the graph
			for (Feature feature : features) {
				GeoJsonObject obj = feature.getGeometry();
				if (obj instanceof LineString) {
					List<LngLatAlt> coordinateList = ((LineString) obj).getCoordinates();
					int max_speed = feature.getProperty("maxspeed");
					LngLatAlt old_lnglat = null;
					Node old_node = null;
					Node new_node = null;
					for (LngLatAlt next_lnglat : coordinateList) {
						if (old_lnglat != null) {
							new_node = new Node(next_lnglat.getLatitude(), next_lnglat.getLongitude());
							String key = new_node.getId();
							if (!nodes.containsKey(key)) {
								nodes.put(key, new_node);
							} else {
								new_node = nodes.get(key);
							}
							edges.add(new Edge(old_node, new_node, max_speed));
						} else {
							old_node = new Node(next_lnglat.getLatitude(), next_lnglat.getLongitude());
							String key = old_node.getId();
							if (!nodes.containsKey(key)) {
								nodes.put(key, old_node);
							} else {
								old_node = nodes.get(key);
							}
							new_node = old_node;
						}
						old_node = new_node;
						old_lnglat = next_lnglat;
					}
				} else if (obj instanceof MultiPoint) {
					List<LngLatAlt> lngLatList = ((MultiPoint) obj).getCoordinates();
					for (LngLatAlt lngLat_coord : lngLatList) {
						Node new_node = new Node(lngLat_coord.getLatitude(), lngLat_coord.getLongitude());
						String key = new_node.getId();
						if (!nodes.containsKey(key)) {
							nodes.put(key, new_node);
						}
					}
				}
			}
		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Graph graphc = new Graph(nodes, edges);
		return graphc;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		// Have an option to edit the graph later
		this.graph = graph;
	}

}
