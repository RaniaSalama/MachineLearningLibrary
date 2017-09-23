package Unsupervised.Kmeans;

import java.util.ArrayList;

/**
 * 
 * @author Rania Ibrahim
 *
 */
public class Pattern {

	private int id;
	private Centroid cluster;
	private ArrayList<Double> features;

	public Pattern() {
		cluster = new Centroid();
		features = new ArrayList<Double>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Centroid getCluster() {
		return cluster;

	}

	public void setClusterID(Centroid cluster) {
		this.cluster = cluster;
	}

	public ArrayList<Double> getFeatures() {
		return features;
	}

	public void setFeatures(ArrayList<Double> features) {
		this.features = features;
	}

}
