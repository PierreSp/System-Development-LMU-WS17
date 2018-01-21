package sysdev.graph;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DijkstraAlgorithm {

    private final List<Node> nodes;
    private final List<Edge> edges;
    private Set<Node> settledNodes; // nodes visited
    private Set<Node> unSettledNodes; // nodes not visited
    private Map<Node, Node> predecessors; // node to node connection
    private Map<Node, Integer> distance; // shortest distance of Node and next to target
    private double path_duration;
    private int path_distance;

    public DijkstraAlgorithm(Graph graph) {
        // create a copy of the array so that we can operate on this array
    	List<Node> nlist = new ArrayList<Node>(graph.getNodes().values());
    	this.path_distance = 0; // remember path distance for info panel
    	this.path_duration = 0; // remember path duration for info panel
        this.nodes = nlist;
        this.edges = new ArrayList<Edge>(graph.getEdges());
    }

    public void execute(Node source, Node destination) {
        settledNodes = new HashSet<Node>(); // Nodes to visit
        unSettledNodes = new HashSet<Node>(); // Nodes visited
        distance = new HashMap<Node, Integer>();
        predecessors = new HashMap<Node, Node>();
        distance.put(source, 0);
        unSettledNodes.add(source);
        // (unSettledNodes.size() > 0)  if we want to find connection to all nodes, makes only sense with fixed source
        while (!isSettled(destination)){
        	Node node = getMinimum(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
        }
        System.out.println("Found Target Node");
    }

    private void findMinimalDistances(Node node) {
        List<Node> adjacentNodes = getNeighbors(node);
        for (Node target : adjacentNodes) {
            if (getShortestDistance(target) > getShortestDistance(node)
                    + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node)
                        + getDistance(node, target));
                predecessors.put(target, node);
                unSettledNodes.add(target);
            }
        }

    }

    private int getDistance(Node node, Node target) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(node)
                    && edge.getDestination().equals(target)) {
                return edge.getWeight();
            }
        }
        throw new RuntimeException("Try to find distance between one and the same node");
    }
    
    private double getDuration(Node node, Node target) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(node)
                    && edge.getDestination().equals(target)) {
                return edge.getDuration();
            }
        }
        throw new RuntimeException("Try to find duration between one and the same node");
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<Node>();
        for (Edge edge : edges) {
            if (edge.getSource().equals(node)
                    && !isSettled(edge.getDestination())) {
                neighbors.add(edge.getDestination());
            }
        }
        return neighbors;
    }

    private Node getMinimum(Set<Node> nodes) {
    	Node minimum = null;
        for (Node node_m : nodes) {
            if (minimum == null) {
                minimum = node_m;
            } else {
                if (getShortestDistance(node_m) < getShortestDistance(minimum)) {
                    minimum = node_m;
                }
            }
        }
        return minimum;
    }

    private boolean isSettled(Node node) {
        return settledNodes.contains(node);
    }

    private int getShortestDistance(Node destination) {
        Integer d = distance.get(destination);
        if (d == null) {
            return Integer.MAX_VALUE;
        } else {
            return d;
        }
    }

    /*
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     */
    public LinkedList<Node> getPath(Node target) {
        LinkedList<Node> path = new LinkedList<Node>();
        Node step = target;
        // check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step);
        while (predecessors.get(step) != null) {
        	path_duration = path_duration + getDuration(predecessors.get(step), step);
        	path_distance = path_distance + getDistance(predecessors.get(step), step);
        	System.out.println(path_duration);
            step = predecessors.get(step);
            path.add(step);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }

	public int getPath_distance() {
		return path_distance;
	}

	public double getPath_duration() {
		return path_duration;
	}

	public List<Node> getNodes() {
		return nodes;
	}

}