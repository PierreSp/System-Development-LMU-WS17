package sysdev.graph;

/**
 * @author pierre
 *
 */
public class Edge {

	// All the data are read in from the json, so no edit is possible
	private final Node source;
	private final Node destination;
	private final double maxspeed;
	private final int weight;
	private final double duration;

	public Edge(Node source, Node destination, double maxspeed) {
		this.source = source;
		this.destination = destination;
		this.weight = calc_distance(source, destination);
		this.maxspeed = maxspeed;
		this.duration = weight / maxspeed * 6;// want duration in minutes given km/h
		
	}

	public static int calc_distance(Node start, Node end) {
		final double EARTHRADIUS = 6371.01;
		double dist_lon = Math.toRadians(start.getLon() - end.getLon());
		double dist_lat = Math.toRadians(start.getLat() - end.getLat());
		double a = Math.sin(dist_lat / 2) * Math.sin(dist_lat / 2) + Math.cos(Math.toRadians(start.getLat()))
				* Math.cos(Math.toRadians(end.getLat())) * (Math.sin(dist_lon / 2) * Math.sin(dist_lon / 2));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = EARTHRADIUS * c * 1000;
		return (int) Math.round(distance);
	}

	// Getter

	public int getWeight() {
		return weight;
	}
	
	public double getDuration() {
		return duration;
	}

	public Node getDestination() {
		return destination;
	}

	public Node getSource() {
		return source;
	}
	
	@Override
	public String toString() {
		return source + " " + destination;
	}

	public double getMaxspeed() {
		return maxspeed;
	}

}
