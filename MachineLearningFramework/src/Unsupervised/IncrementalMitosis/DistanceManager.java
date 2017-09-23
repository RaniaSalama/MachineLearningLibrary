package Unsupervised.IncrementalMitosis;

import java.util.HashMap;

/**
 * 
 * @author Rania Ibrahim
 *
 */
public class DistanceManager {

	String distanceMethod;

	public DistanceManager(String distanceMethod) {
		this.distanceMethod = distanceMethod;
	}

	public double calculateDistance(Pattern pattern1, Pattern pattern2) {
		double pDistance = 0;
		if (distanceMethod.equalsIgnoreCase("euclidean"))
			pDistance = getEculidanDistance(pattern1, pattern2);
		else if (distanceMethod.equalsIgnoreCase("cosine"))
			pDistance = cosine(pattern1, pattern2);
		else if (distanceMethod.equalsIgnoreCase("pearson"))
			pDistance = getPearsonDistance(pattern1, pattern2);
		else if (distanceMethod.equalsIgnoreCase("kld"))
			pDistance = KLDistance(pattern1, pattern2);
		else if (distanceMethod.equalsIgnoreCase("jaccard"))
			pDistance = Jaccard(pattern1, pattern2);
		return pDistance;
	}

	public double getEculidanDistance(Pattern pattern1, Pattern pattern2) {
		HashMap<String, Double> dim1 = pattern1.getDim();
		HashMap<String, Double> dim2 = pattern2.getDim();
		double ECDis = 0.0;
		for (String feature1 : dim1.keySet()) {
			double score1 = dim1.get(feature1);
			double score2 = 0;
			if (dim2.containsKey(feature1))
				score2 = dim2.get(feature1);
			ECDis += (score1 - score2) * (score1 - score2);
		}
		for (String feature2 : dim2.keySet()) {
			if (!dim1.containsKey(feature2)) {
				ECDis += dim2.get(feature2) * dim2.get(feature2);
			}
		}
		ECDis = Math.sqrt(ECDis);
		return ECDis;
	}

	public double cosine(Pattern pattern1, Pattern pattern2) {
		double similarity = 0;
		double num = 0;
		double dem1 = 0;
		double dem2 = 0;
		double termValue1 = 0;
		double termValue2 = 0;
		HashMap<String, Double> dim1 = pattern1.getDim();
		HashMap<String, Double> dim2 = pattern2.getDim();

		for (String termName : dim1.keySet()) {
			termValue2 = 0;
			termValue1 = dim1.get(termName);
			if (dim2.containsKey(termName)) {
				termValue2 = dim2.get(termName);
			}
			num += (termValue1 * termValue2);
			dem1 += (termValue1 * termValue1);
		}

		for (String termName : dim2.keySet()) {
			termValue2 = dim2.get(termName);
			dem2 += (termValue2 * termValue2);
		}

		similarity = (num) / Math.sqrt(dem1 * dem2);
		return Math.max(1 - similarity, 0); // because round off error

	}

	public double getPearsonDistance(Pattern pattern1, Pattern pattern2) {
		HashMap<String, Double> norm1 = pattern1.norm();
		HashMap<String, Double> norm2 = pattern2.norm();
		double pearsonDis = 0.0;
		double r = 0.0;
		int size = 0;
		for (String n : norm1.keySet()) {
			double norm2Freq = 0;
			if (norm2.containsKey(n))
				norm2Freq = norm2.get(n);
			r += (norm1.get(n) * norm2Freq);
			size++;
		}
		r /= size;
		pearsonDis = 1 - r;
		return pearsonDis;
	}

	public double KLDistance(Pattern p1, Pattern p2) {
		double result = 0.0;
		double frequency2 = 0.0;
		HashMap<String, Double> map = p1.getDim();
		HashMap<String, Double> map2 = p2.getDim();
		for (String sequence : map.keySet()) {
			double frequency1 = map.get(sequence);
			if (map2.containsKey(sequence))
				frequency2 = (double) map2.get(sequence);
			else
				frequency2 = 0;
			double log1 = 0;
			if (frequency1 != 0)
				log1 = Math.log(frequency1);
			double log2 = 0;
			if (frequency2 != 0)
				log2 = Math.log(frequency2);
			result += frequency1 * (log1 - log2);

		}
		return Math.abs(result);
	}

	public double Jaccard(Pattern p1, Pattern p2) {
		double AB = 0;
		double A2 = 0;
		double B2 = 0;
		HashMap<String, Double> map1 = p1.getDim();
		HashMap<String, Double> map2 = p2.getDim();
		for (String s : map1.keySet()) {
			double frequency1 = map1.get(s);
			if (map2.containsKey(s))
				AB += frequency1 * map2.get(s);
			A2 += frequency1 * frequency1;
		}
		for (String s : map2.keySet()) {
			double frequency2 = map2.get(s);
			B2 += frequency2 * frequency2;
		}
		double f = AB / (A2 + B2 - AB);
		if (A2 + B2 - AB == 0)
			return 10000000;
		return (1 - f);
	}

}
