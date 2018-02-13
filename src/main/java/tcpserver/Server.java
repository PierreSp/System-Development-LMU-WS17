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
 * @author schubert, modified by pierre
 */
public class Server {
	// Load graph only once
	private FillGraph fg = new sysdev.graph.FillGraph();
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
        System.out.println("SysDev TCP Server started on localhost:" + server.port);
        System.in.read();
        server.shutdown();
        System.exit(0);
    }

    
}
