package Unsupervised.IncrementalMitosis;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author Rania Ibrahim
 *
 */
public class Pattern implements Comparable<Pattern> {

	private int id;
	private double averge;
	private int clusterID;
	private double min;
	private int count;
	private int finalClusterID;
	private ArrayList<Double> FrangeDistance;
	private HashMap<String, Double> dim;
	private String checkID;
	private String query;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getCheckID() {
		return checkID;
	}

	public void setCheckID(String checkID) {
		this.checkID = checkID;
	}

	public HashMap<String, Double> getDim() {
		return dim;
	}

	public void setDim(HashMap<String, Double> dim) {
		this.dim = dim;
	};

	public ArrayList<Double> getFrangeDistance() {
		return FrangeDistance;
	}

	public void setFrangeDistance(ArrayList<Double> frangeDistance) {
		FrangeDistance = frangeDistance;
	}

	public Pattern() {
		FrangeDistance = new ArrayList<Double>();
		dim = new HashMap<String, Double>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getAverge() {
		return averge;
	}

	public void setAverge(double averge) {
		this.averge = averge;
	}

	public int getClusterID() {
		return clusterID;

	}

	public void setClusterID(int clusterID) {
		this.clusterID = clusterID;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getFinalClusterID() {
		return finalClusterID;
	}

	public void setFinalClusterID(int finalClusterID) {
		this.finalClusterID = finalClusterID;
	}

	@Override
	public int compareTo(Pattern p) {
		if (p.getId() > this.getId())
			return -1;
		else
			return 1;
	}

	@Override
	public boolean equals(Object o) {
		Pattern p = (Pattern) o;
		if (id == p.id)
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return id;
	}

	public HashMap<String, Double> norm() {
		HashMap<String, Double> norm = new HashMap<String, Double>();
		double mean = 0.0;
		for (String sd : dim.keySet())
			mean += dim.get(sd);
		mean /= dim.size();

		double standardDeviation = 0.0;
		for (String sd : dim.keySet()) {
			double d = dim.get(sd);
			standardDeviation += ((d - mean) * (d - mean));
		}
		standardDeviation /= dim.size();
		standardDeviation = Math.sqrt(standardDeviation);
		for (String sd : dim.keySet()) {
			double d = dim.get(sd);
			norm.put(sd, ((d - mean) / standardDeviation));
		}
		return norm;
	}

}
