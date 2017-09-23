package Unsupervised.Mitosis;

import java.util.ArrayList;

/**
 * 
 * @author Naglaa Ahmed and Rania Ibrahim
 *
 */
public class Cluster implements Comparable<Cluster> {
	ArrayList<Association> clusterList;
	int id;
	ArrayList<Integer> patterns;
	double averageDistance;

	public double getAveDis() {
		return averageDistance;
	}

	public void setAveDis(double aveDis) {
		this.averageDistance = aveDis;
	}

	public ArrayList<Integer> getPatterns() {
		return patterns;
	}

	public void setPatterns(ArrayList<Integer> patterns) {
		this.patterns = patterns;
	}

	public Cluster() {
		id = 0;
		averageDistance = 0.0;
		clusterList = new ArrayList<Association>();
		patterns = new ArrayList<Integer>();
	};

	public Cluster(int id1, ArrayList<Association> clusterList1) {
		id = id1;
		clusterList = clusterList1;

	};

	public ArrayList<Association> getClusterList() {
		return clusterList;
	}

	public void setClusterList(ArrayList<Association> clusterList) {
		this.clusterList = clusterList;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int compareTo(Cluster c) {
		return (c.id == this.id) ? 0 : 1;
	}

	public boolean equals(Object o) {
		Cluster c = (Cluster) o;
		return c.id == this.id;
	}

}
