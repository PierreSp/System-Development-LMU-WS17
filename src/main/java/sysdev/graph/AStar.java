package sysdev.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;


public class AStar {
    double path_duration;
    int path_distance;
	final List<Edge> ALLEDGES;
	final Graph GRAPH;

    public AStar(Graph graph) { 	
    	this.GRAPH = graph;
    	ALLEDGES = new ArrayList<Edge>(graph.getEdges());
    }
    
    public List<Node> aStar(Node start, Node goal) {
    	
    	// Do inits
    	
        final int SIZE = GRAPH.getNodes().size(); // reserve space (Does make more sense for large graphs, but init saves so much time :) )
        
        final Set<Node> closedSet = new HashSet<Node>(SIZE); // The set of checked nodes.
 final Map<Node,Node> cameFrom = new HashMap<Node,Node>(); // The map of nodes for the final path
        final Map<Node,Integer> gScore = new HashMap<Node,Integer>(); // Cost(real not heuristic) from start along final path.
        // Estimated total cost from start to goal through y Using heuristic)
        final Map<Node,Integer> fScore = new HashMap<Node,Integer>();
        final Comparator<Node> comparator = new Comparator<Node>() {
            @Override
            public int compare(Node origin, Node destination) {
                if (fScore.get(origin) < fScore.get(destination))
                    return -1;
                if (fScore.get(destination) < fScore.get(origin))
                    return 1;
                return 0;
            }
        };
        PriorityQueue<Node> openSet=
                new PriorityQueue<Node>(SIZE, comparator); // The set of nodes to be evaluated, initially containing the start node

       
        // Start with the work
        openSet.add(start);
        gScore.put(start, 0);
        for (Node v : GRAPH.getNodes().values())
            fScore.put(v, Integer.MAX_VALUE);
        fScore.put(start, heuristicCostEstimate(start,goal));



        while (!openSet.isEmpty()) {
            final Node current = openSet.remove();
            if (current.equals(goal))
                return reconstructPath(cameFrom, goal);

           // openSet.remove(0);
            closedSet.add(current);
            List<Node> neighbors = getNeighbors(current);
            for (Node neighbor : neighbors) {
                if (closedSet.contains(neighbor))
                    continue; // Ignore the neighbor which is already evaluated.

                final int truecost = gScore.get(current) + Edge.calc_distance(current,neighbor); // length of this path.
                if (!openSet.contains(neighbor))
                    openSet.add(neighbor); // Add a new node
                else if (truecost >= gScore.get(neighbor))
                    continue;

                // This path is the best until now. Record it!
                cameFrom.put(neighbor, current);
                gScore.put(neighbor, truecost);
                final int estimatedFScore = gScore.get(neighbor) + heuristicCostEstimate(neighbor, goal);
                fScore.put(neighbor, estimatedFScore);

            }
        }

        return null;
    }

    /**
     * Calculate heuristic cost. Heuristic = "airdistance" to target
     */
    int heuristicCostEstimate(Node start, Node goal) {
    	int h_target = start.getH_target();
    	if(h_target == 10000000) {
    		h_target = Edge.calc_distance(start, goal);
    		start.setH_target(h_target);
    	}
        return h_target;
    }
    
    public List<Node> reconstructPath(Map<Node,Node> cameFrom, Node current) {
        final List<Node> totalPath = new ArrayList<Node>();
        path_duration = 0;
        path_distance = 0;
        while (current != null) {
            final Node previous = current;
            current = cameFrom.get(current);
            if (current != null ) {
                path_duration = path_duration + getDuration(previous, current);
                path_distance = path_distance + getDistance(previous, current);
                totalPath.add(previous);
            }
        }
        Collections.reverse(totalPath);
        return totalPath;
    }
    
    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<Node>();
        for (Edge edge : ALLEDGES) {
            if (edge.getSource().equals(node)) {
                neighbors.add(edge.getDestination());
            }
        }
        return neighbors;
    }
    
    private int getDistance(Node node, Node target) {
        for (Edge edge : ALLEDGES) {
            if (edge.getSource().equals(node)
                    && edge.getDestination().equals(target)) {
                return edge.getWeight();
            }
        }
        return 0;
        //throw new RuntimeException("Try to find distance between one and the same node(dist)" + node.getId() );
    }
    
    private double getDuration(Node node, Node target) {
        for (Edge edge : ALLEDGES) {
            if (edge.getSource().equals(node)
                    && edge.getDestination().equals(target)) {
                return edge.getDuration();
            }
        }
        
        //throw new RuntimeException("Try to find duration between one and the same node(dur)" + node.getId()  + target.getId());
        return 0;
    }
    
    public double getPath_duration() {
		return path_duration;
	}

	public int getPath_distance() {
		return path_distance;
	}
    
}