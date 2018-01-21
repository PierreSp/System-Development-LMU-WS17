/**
 * 
 */
package sysdev.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author pierre
 *
 */
public class Graph {

    private final HashMap<String, Node> nodes;
    private HashMap<String, ArrayList<Node>> nodebucket;
    private final List<Edge> edges;

    public Graph(HashMap<String, Node> nodeshash, List<Edge> edges) {
        this.nodes = nodeshash;
        this.edges = edges;
        HashMap<String, ArrayList<Node>> nodebucket_tmp = new HashMap<String, ArrayList<Node>>();
        for (Node node : nodeshash.values()) {
        	ArrayList<Node> old = nodebucket_tmp.get(node.getBucket_id());
        	if (old == null) {
        		old = new ArrayList<Node>();
        	}
        	old.add(node);
            nodebucket_tmp.put(node.getBucket_id(), old);
        }
        this.nodebucket = nodebucket_tmp;
        
    }
    
    public Node nearest_node(Node node_in) {
    	List<Node> possible_nodes = new ArrayList<Node>();
    	// Run over all neighborhood buckets to find possible neighbors with small effort
    	for (long i = -1; i < 2; i++) {
    		for (long j = -1; j < 2; j++) {
        		String hashkey = (node_in.getBucketlat() + i) + "" + (node_in.getBucketlon() + j);
        		try {
        			for (Node node : this.nodebucket.get(hashkey)) {
            			if(node != null) {
            				possible_nodes.add(node);
            			}
                    }
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("Empty Bucket");
				}
        		
        	}
    	}
    	int shortest_dist = Integer.MAX_VALUE;
    	Node nearest = null;
    	for (Node node : possible_nodes) {
        	Edge helper_edge = new Edge(node_in, node, 10);
        	if (helper_edge.getWeight() < shortest_dist) {
        		nearest = node;
        		shortest_dist = helper_edge.getWeight();
        	}
        }
        
		return nearest;
    }

    public HashMap<String, Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }
   
    	
    
}


