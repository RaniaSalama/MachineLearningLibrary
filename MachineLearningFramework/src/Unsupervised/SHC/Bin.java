package Unsupervised.SHC;

/**
 * 
 * @author Hams Elashry
 *
 */
public class Bin {

	private int id;
	private double countOfSimilarityInBin;
	private double lowerSimilarityOfBin;
	private double upperSimilarityOfBin;

	public Bin() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getCountOfSimilarityinBin() {
		return countOfSimilarityInBin;
	}

	public void setCountOfSimilarityinBin(double countOfSimilarityinBin) {
		this.countOfSimilarityInBin = countOfSimilarityinBin;
	}

	public double getLowerSimilarityOfBin() {
		return lowerSimilarityOfBin;
	}

	public void setLowerSimilarityOfBin(double lowerSimilarityOfBin) {
		this.lowerSimilarityOfBin = lowerSimilarityOfBin;
	}

	public double getUpperSimilarityOfBin() {
		return upperSimilarityOfBin;
	}

	public void setUpperSimilarityOfBin(double upperSimilarityOfBin) {
		this.upperSimilarityOfBin = upperSimilarityOfBin;
	}
}
