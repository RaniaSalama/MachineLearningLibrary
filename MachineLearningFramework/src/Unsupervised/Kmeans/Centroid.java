package Unsupervised.Kmeans;

import java.util.ArrayList;

/**
 * 
 * @author Rania Ibrahim
 *
 */
public class Centroid {

	private ArrayList<Pattern> patterns;
	private ArrayList<Double> features;
	private int id;

	public Centroid() {
		patterns = new ArrayList<Pattern>();
		features = new ArrayList<Double>();
	}

	public ArrayList<Pattern> getPatterns() {
		return patterns;
	}

	public void setPatterns(ArrayList<Pattern> patterns) {
		this.patterns = patterns;
	}

	public ArrayList<Double> getFeatures() {
		return features;
	}

	public void setFeatures(ArrayList<Double> features) {
		this.features = features;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean calculateCentroids() {
		ArrayList<Double> newfeatures = new ArrayList<Double>();
		for (int j = 0; j < patterns.get(0).getFeatures().size(); j++) {
			double feature = 0;
			for (int i = 0; i < patterns.size(); i++) {
				feature += patterns.get(i).getFeatures().get(j);
			}
			feature /= patterns.size();
			newfeatures.add(feature);
		}
		if (getEculidanDistance(newfeatures, features) <= 0.00000001)
			return false;
		return true;
	}

	public double getEculidanDistance(ArrayList<Double> dim1,
			ArrayList<Double> dim2) {
		double ECDis = 0.0;
		for (int i = 0; i < dim1.size(); i++) {
			double score1 = dim1.get(i);
			double score2 = dim2.get(i);
			ECDis += (score1 - score2) * (score1 - score2);
		}
		ECDis = Math.sqrt(ECDis);
		return ECDis;
	}

}
