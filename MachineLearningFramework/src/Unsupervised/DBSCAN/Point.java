package Unsupervised.DBSCAN;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 
 * @author Sara Shokry
 */
public class Point {

	public static final int UNCLASSIFIED = -2;
	public static final int NOISE = -1;
	public static final int CLASSIFIED = 0;
	private HashMap<String, Float> terms;
	private int label; // State
	private int clusterID;
	private int numOfNeighboors = 0;
	private int pointID = NOISE; // The point id in the data set
	
	public Point() {
		terms = new HashMap<String, Float>();
		label = UNCLASSIFIED;
	}

	public Point(HashMap<String, Float> termsList) {
		terms = new HashMap<String, Float>(termsList);
		label = UNCLASSIFIED;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		if (label == UNCLASSIFIED || label == NOISE) {
			clusterID = UNCLASSIFIED;
		}

		this.label = label;
	}

	public int getClusterID() {
		return clusterID;
	}

	public void setClusterID(int clusterId) {
		clusterID = clusterId;
		this.label = CLASSIFIED;
	}

	public int getID() {
		return pointID;
	}

	public void setID(int ID) {
		this.pointID = ID;
	}

	public HashMap<String, Float> getTerms() {
		return terms;
	}

	public void setTerms(HashMap<String, Float> terms) {
		this.terms = terms;
	}

	public HashMap<String, Float> getTesrms() {
		return terms;
	}

	public void setTesrms(HashMap<String, Float> tesrms) {
		this.terms = new HashMap<String, Float>(tesrms);
	}
	
	public boolean isClassified() {
		if (label != UNCLASSIFIED) {
			return true;
		} else {
			return false;
		}
	}

	public void addTerm(String Name, Float frequency) {
		terms.put(Name, frequency);
	}

	public int getNumOfNeighboors() {
		return numOfNeighboors;
	}

	public void setNumOfNeighboors(int numOfNeighboors) {
		this.numOfNeighboors = numOfNeighboors;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Point other = (Point) obj;
		if (!Objects.equals(this.terms, other.terms)) {
			return false;
		}
		return true;
	}

	public void reset() {
		this.label = UNCLASSIFIED;
	}

	public double distance(Point point) {
		Point q = (Point) point;
		Double Totaldistance = 0.0;
		Float distance = 0f;
		Set<String> mergedTerms = new HashSet<String>(terms.keySet());
		mergedTerms.addAll(q.getTesrms().keySet());

		// Calculate distance
		for (String term : mergedTerms) {
			distance = 0f;
			if (terms.containsKey(term)) {
				distance = terms.get(term);
			}
			if (q.getTesrms().containsKey(term)) {
				distance -= q.getTesrms().get(term);
			}
			Totaldistance += distance * distance;

		}
		return Math.sqrt(Totaldistance);

	}

	public String toString() {
		return terms.toString();
	}
}
