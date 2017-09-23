package Unsupervised.Mitosis;

import java.util.ArrayList;

/**
 * 
 * @author Naglaa Ahmed and Rania Ibrahim
 *
 */
public class Pattern {
	private ArrayList<Double> features;

	public Pattern() {
		features = new ArrayList<Double>();
	}

	public ArrayList<Double> getDim() {
		return features;
	}

	public void setDim(ArrayList<Double> dim) {
		this.features = dim;
	};

	public ArrayList<Double> norm() {
		ArrayList<Double> norm = new ArrayList<Double>();
		double mean = 0.0;
		for (Double d : features)
			mean += d;
		mean /= features.size();

		double standardDeviation = 0.0;
		for (Double d : features) {
			standardDeviation += ((d - mean) * (d - mean));
		}
		standardDeviation /= features.size();
		standardDeviation = Math.sqrt(standardDeviation);
		for (Double d : features) {
			norm.add((d - mean) / standardDeviation);
		}
		return norm;
	}
}
