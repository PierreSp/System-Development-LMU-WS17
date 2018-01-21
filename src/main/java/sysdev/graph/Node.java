package sysdev.graph;

/**
 * @author pierre
 *
 */	

public class Node {

	private double lon;
	private double lat;

    final private String id;
    final private String bucketid;
    final private long bucketlon;
    final private long bucketlat;
    
    private final int R = 6371000;
    private final double pi = Math.PI;
    private final double angle_lat = (2*pi*R)/(101*360);
    private final double max_lat = 60.908756256113516; // 60.908756256103516
    private final double angle_lon = (2*pi*R*Math.cos(Math.toRadians(max_lat)))/(101*360);

    //As stated in the description, we assume that the earth is a sphere with radius 6371km.
    //In order to find interesting points, we want to classify our points via their coordinates into buckets. 
    //Imagine we put a grid over the earth with quadrants of about 100m length

    public long initBucketLat(double lat) {
    	double product = lat * angle_lat;
        long BucketLat = Math.round(product);
        return BucketLat;
    }

    public long initBucketLon(double lon) {
    	double product = lon * angle_lon;
        long BucketLon = Math.round(product);
        return BucketLon;
    }
    

    public Node(double lat, double lon) {
        this.id = lat + "" + lon;
        this.lon = lon;
        this.lat = lat;
        long blat = initBucketLat(lat);
        long blon = initBucketLon(lon);
        this.bucketlat = blat;
        this.bucketlon = blon;
        this.bucketid = blat + "" + blon;
    }
    
    public long getBucketlon() {
		return bucketlon;
	}

	public long getBucketlat() {
		return bucketlat;
	}

	public String getId() {
        return id;
    }

	public String getBucket_id() {
		return bucketid;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
