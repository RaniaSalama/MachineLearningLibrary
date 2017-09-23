package Unsupervised.SHC;

import java.util.ArrayList;

/**
 * 
 * @author Hams Elashry
 *
 */
public class Cluster {

	private Bin clusterBins[];
	private double histogramOfCluster;
	private ArrayList<Doc> docsOfCluster;
	private double threshold;
	private int noOfBins;
	private int clusterID;

	public Cluster(int noOfBins, double threshold, double histogramOfCluster) {
		this.noOfBins = noOfBins;
		this.threshold = threshold;
		this.histogramOfCluster = histogramOfCluster;
		double intervals = (double) 1 / noOfBins;
		clusterBins = new Bin[noOfBins];
		for (int i = 0; i < noOfBins; i++) {
			clusterBins[i] = new Bin();
			clusterBins[i].setId(i);
			clusterBins[i].setLowerSimilarityOfBin(i * intervals);
			clusterBins[i].setUpperSimilarityOfBin((i + 1) * intervals);
		}
		docsOfCluster = new ArrayList<Doc>();
	}

	public int getClusterID() {
		return clusterID;
	}

	public void setClusterID(int clusterID) {
		this.clusterID = clusterID;
	}

	public ArrayList<Doc> getDocsOfCluster() {
		return docsOfCluster;
	}

	public void setDocsOfCluster(ArrayList<Doc> docsOfCluster) {
		this.docsOfCluster = docsOfCluster;
	}

	public Bin[] getClusterBins() {
		return clusterBins;
	}

	public void setClusterBins(Bin[] clusterBins) {
		this.clusterBins = clusterBins;
	}

	public double getHistogramOfCluster() {
		return histogramOfCluster;
	}

	public void setHistogramOfCluster(double histogramOfCluster) {
		this.histogramOfCluster = histogramOfCluster;
	}

	public void calculateHistogramOfCluster() {
		int t = (int) Math.floor(threshold * noOfBins);
		double num = 0;
		double dem = 0;
		for (int i = 1; i < t; i++) {
			dem += clusterBins[i - 1].getCountOfSimilarityinBin();
		}
		for (int i = t; i <= noOfBins; i++) {
			num += clusterBins[i - 1].getCountOfSimilarityinBin();
			dem += clusterBins[i - 1].getCountOfSimilarityinBin();
		}
		histogramOfCluster = num / dem;
	}

	public double simulationCalculateHistogramOfCluster(Bin[] clusterBins) {
		double histogramOfCluster = 0;
		int t = (int) Math.floor(threshold * noOfBins);
		double num = 0;
		double dem = 0;
		for (int i = 0; i < t; i++) {
			dem += clusterBins[i].getCountOfSimilarityinBin();
		}
		for (int i = t; i < noOfBins; i++) {
			num += clusterBins[i].getCountOfSimilarityinBin();
			dem += clusterBins[i].getCountOfSimilarityinBin();
		}
		histogramOfCluster = num / dem;
		return histogramOfCluster;
	}

	public Bin[] simulationForAddingNewDocToCluster(Doc doc) {
		Bin localHistogramBin[] = new Bin[clusterBins.length];
		for (int i = 0; i < localHistogramBin.length; i++) {
			localHistogramBin[i] = new Bin();
			localHistogramBin[i].setCountOfSimilarityinBin(clusterBins[i]
					.getCountOfSimilarityinBin());
			localHistogramBin[i].setId(clusterBins[i].getId());
			localHistogramBin[i].setLowerSimilarityOfBin(clusterBins[i]
					.getLowerSimilarityOfBin());
			localHistogramBin[i].setUpperSimilarityOfBin(clusterBins[i]
					.getUpperSimilarityOfBin());
		}
		boolean similarityAddedToRange = false;
		double docSimilarity = 0;
		double tempCountOfHistogram = 0;
		for (int i = 0; i < docsOfCluster.size(); i++) {
			similarityAddedToRange = false;
			docSimilarity = docsOfCluster.get(i).distance(doc);
			for (int j = 0; j < localHistogramBin.length
					&& similarityAddedToRange == false; j++) {
				if ((docSimilarity >= (localHistogramBin[j]
						.getLowerSimilarityOfBin()))
						&& (docSimilarity < (localHistogramBin[j]
								.getUpperSimilarityOfBin()))) {
					tempCountOfHistogram = localHistogramBin[j]
							.getCountOfSimilarityinBin();
					localHistogramBin[j]
							.setCountOfSimilarityinBin(tempCountOfHistogram + 1);
					similarityAddedToRange = true;
				}
			}
		}
		return localHistogramBin;
	}

	public void addingNewDocToCluster(Doc doc, Bin[] newClusterBins,
			double newHistogram) {
		docsOfCluster.add(doc);
		if (newClusterBins != null && newHistogram != -1) {
			clusterBins = newClusterBins;
			histogramOfCluster = newHistogram;
		}
	}
}
