/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpserver;

import com.fasterxml.jackson.databind.ObjectMapper;

import sysdev.graph.AStar;
import sysdev.graph.Graph;
import sysdev.graph.Node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.Geometry;
import org.geojson.LineString;
import org.geojson.LngLatAlt;

/**
 *
 * @author schubert
 */
public class RequestHandler implements Runnable{
    //client socket to read the request 
    private Socket socket;
    //Server data being handed down from the listener
    private Graph graph;
    //read for running the RequestHandler
    private Thread thread;
    //Name of the thread
    private String threadName;
    //number for generating new ThreadIDs
    private static long threadID = 0;
    
    
    
    public RequestHandler(Socket socket,Graph serverData){
       this.socket = socket;
       this.graph = serverData;
    }
    
    /***
     * Read the request from the socket and output the result as a GeoJson
     */
    
    
    public void run(){
        InputStream in = null;
        System.out.println("ReqHandler is working");
        DataOutputStream out = null;
        try {
            in = socket.getInputStream();
            //ObjectMapper mapper = new ObjectMapper();
            DataInputStream ois = new DataInputStream(in);
            double originLat = ois.readDouble();
            double originLon = ois.readDouble();
            double destinationLat = ois.readDouble();
            double destinationLon = ois.readDouble();
            String output = "";
			Node nearest_origin = graph.nearest_node(new Node(originLat, originLon));
			Node nearest_dest = graph.nearest_node(new Node(destinationLat, destinationLon));
			AStar shortest_path_algo = new AStar();
			List<Node> path = shortest_path_algo.aStar(graph, nearest_origin, nearest_dest);
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
			System.out.println(shortest_path_algo.getPath_duration());
			costs.put("Distance", (int) shortest_path_algo.getPath_distance());
			costs.put("Travel_Time", (int) (shortest_path_algo.getPath_duration()));
			feature.setProperty("costs", costs);
			featureCollection.add(feature);

			output = new ObjectMapper().writeValueAsString(featureCollection);
			output = "{\"data\": " + output + "}";
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(output);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("IOException occurred while processing request.");
            e.printStackTrace();
        }
    }
    
    /***
     * Start a new runnable
     */
       
    public void start(){
        threadID++;
        String threadName = "RequestHandler"+(threadID-1);
        thread = new Thread(this,threadName);
        thread.start();
    }

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
}
