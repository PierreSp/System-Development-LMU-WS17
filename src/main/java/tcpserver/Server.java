/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpserver;

import java.io.IOException;
import sysdev.graph.*;
/**
 *
 * @author schubert
 */
public class Server {
    //Some Data replacing the the GeoJson File
    public static final String jsonResult = "{\"data\":{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{\"costs\":{\"Travel_Time\":1000.0,\"Distance\":100.0},\"instructions\":[]},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[13.382720947,52.536064212],[13.403406143,52.53376702]]}}]}}\n";
	private FillGraph fg = new sysdev.graph.FillGraph(); // Load graph only once
	private Graph graph;
    //instance data which might be the road network 
    public int port = 9595;
    
    //Listener Thread for the Server
    Listener listener;    
    
    //constructor
    public Server(){
        //read instance Data
    	graph = fg.getGraph();
    }
    
    /***
     * Starts the listener thread
     */    
    private void startListener() {
       listener = new Listener(port, this.graph);
       listener.start();
    }

    /***
     * Shutdown the listener
     */
    
    private void shutdown() {       
        listener.shutdown();                
    }
    
    /***
     * Start a server and shut it down when enter is pressed.
     * @param args
     * @throws IOException 
     */
     
    public static void main(String[] args) throws IOException{
        Server server = new Server();
        server.startListener();
        System.out.println("SysDev TCP Server started on localhost:" +server.port);
        System.in.read();
        server.shutdown();
        System.exit(0);
    }

    
}
