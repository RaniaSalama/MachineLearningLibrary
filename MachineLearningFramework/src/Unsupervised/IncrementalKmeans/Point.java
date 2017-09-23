package Unsupervised.IncrementalKmeans;

import java.util.ArrayList;

/**
 * 
 * @author Rania Ibrahim
 *
 */
public class Point {

	private ArrayList<Double> dim;
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Double> getDim() {
		return dim;
	}

	public void setDim(ArrayList<Double> dim) {
		this.dim = dim;
	}

	public ArrayList<Double> norm() {
		ArrayList<Double> norm = new ArrayList<Double>();
		double mean = 0.0;
		for (Double d : dim)
			mean += d;
		mean /= dim.size();

		double standardDeviation = 0.0;
		for (Double d : dim) {
			standardDeviation += ((d - mean) * (d - mean));
		}
		standardDeviation /= dim.size();
		standardDeviation = Math.sqrt(standardDeviation);
		for (Double d : dim) {
			norm.add((d - mean) / standardDeviation);
		}
		return norm;
	}

}
