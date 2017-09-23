package Unsupervised.IncrementalMitosis;

import java.util.ArrayList;

/**
 * 
 * @author Rania Ibrahim
 *
 */
public class Cluster {
	ArrayList<Association> clusterList;
	int id;
	ArrayList<Pattern> patterns;
	double aveDis;
	boolean changed;

	public double getAveDis() {
		return aveDis;
	}

	public void setAveDis(double aveDis) {
		this.aveDis = aveDis;
	}

	public ArrayList<Pattern> getPatterns() {
		return patterns;
	}

	public void setPatterns(ArrayList<Pattern> patterns) {
		this.patterns = patterns;
	}

	public Cluster() {
		id = 0;
		aveDis = 0.0;
		clusterList = new ArrayList<Association>();
		patterns = new ArrayList<Pattern>();
		changed = false;
	};

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

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
	public boolean equals(Object o) {
		Cluster oo = (Cluster) o;
		if (oo.id == id)
			return true;
		return false;
	}

}
