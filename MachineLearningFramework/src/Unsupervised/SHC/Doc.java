package Unsupervised.SHC;

import java.util.HashMap;

/**
 * 
 * @author Hams Elashry
 *
 */
public class Doc {
	private HashMap<String, Double> docTermVector;
	private int clusterID;
	private int id;

	public int getClusterID() {
		return clusterID;
	}

	public void setClusterID(int clusterID) {
		this.clusterID = clusterID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public HashMap<String, Double> getDocTermVector() {
		return docTermVector;
	}

	public void setDocTermVector(HashMap<String, Double> docTermVector) {
		this.docTermVector = docTermVector;
	}

	public double distance(Doc doc) {
		double similarity = 0;
		double num = 0;
		double dem1 = 0;
		double dem2 = 0;
		double termValue1 = 0;
		double termValue2 = 0;
		for (String termName : docTermVector.keySet()) {
			termValue2 = 0;
			termValue1 = docTermVector.get(termName);
			if (doc.getDocTermVector().containsKey(termName))
				termValue2 = doc.getDocTermVector().get(termName);
			num += (termValue1 * termValue2);
			dem1 += (termValue1 * termValue1);
		}

		for (String termName : doc.getDocTermVector().keySet()) {
			termValue2 = doc.getDocTermVector().get(termName);
			dem2 += (termValue2 * termValue2);
		}

		termValue1 = Math.sqrt(dem1);
		termValue2 = Math.sqrt(dem2);
		similarity = (num) / (termValue1 * termValue2);
		return similarity;
	}
}
