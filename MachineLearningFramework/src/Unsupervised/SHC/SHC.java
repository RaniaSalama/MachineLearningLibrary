package Unsupervised.SHC;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * 
 * @author Hams Elashry
 *
 */
public class SHC {

	private int numberOfBins;
	private double threshold;
	private double minHistogram;
	private double differenceThreshold;
	private ArrayList<Cluster> clusters;
	private ArrayList<Doc> documents;
	private int clusterId = 1;

	public SHC() {
		clusters = new ArrayList<Cluster>();
		documents = new ArrayList<Doc>();
	}

	public double getDifferenceThreshold() {
		return differenceThreshold;
	}

	public void setDifferenceThreshold(double differenceThreshold) {
		this.differenceThreshold = differenceThreshold;
	}

	public double getMinHistogram() {
		return minHistogram;
	}

	public void setMinHistogram(double minHistogram) {
		this.minHistogram = minHistogram;
	}

	public int getNumberOfBins() {
		return numberOfBins;
	}

	public void setNumberOfBins(int numberOfBins) {
		this.numberOfBins = numberOfBins;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public ArrayList<Cluster> getClusters() {
		return clusters;
	}

	public void setClusters(ArrayList<Cluster> clusters) {
		this.clusters = clusters;
	}

	public ArrayList<Doc> getDocuments() {
		return documents;
	}

	public void setDocuments(ArrayList<Doc> documents2) {
		this.documents = documents2;
	}

	public void SHCMethod() {
		double oldClusterHistogramBeforeAddingDoc = minHistogram;
		double newClusterHistogramAfterAddingDoc = minHistogram;
		boolean wasAdded = false;
		Bin[] simulationPackage;
		for (int i = 0; i < documents.size(); i++) {
			wasAdded = false;
			for (int j = 0; j < clusters.size(); j++) {
				oldClusterHistogramBeforeAddingDoc = (clusters.get(j))
						.getHistogramOfCluster();
				simulationPackage = clusters.get(j)
						.simulationForAddingNewDocToCluster(documents.get(i));
				newClusterHistogramAfterAddingDoc = (clusters.get(j))
						.simulationCalculateHistogramOfCluster(simulationPackage);
				if ((newClusterHistogramAfterAddingDoc > oldClusterHistogramBeforeAddingDoc)
						|| ((newClusterHistogramAfterAddingDoc > minHistogram) && ((oldClusterHistogramBeforeAddingDoc - newClusterHistogramAfterAddingDoc) < differenceThreshold))) {
					wasAdded = true;
					clusters.get(j).addingNewDocToCluster(documents.get(i),
							simulationPackage,
							newClusterHistogramAfterAddingDoc);
					documents.get(i).setClusterID(
							clusters.get(j).getClusterID());
				}
			}
			if (wasAdded == false) {
				Cluster newCluster = new Cluster(numberOfBins, threshold,
						minHistogram);
				newCluster.setClusterID(clusterId);
				clusterId++;
				newCluster.addingNewDocToCluster(documents.get(i), null, -1);
				clusters.add(newCluster);
				documents.get(i).setClusterID(newCluster.getClusterID());
			}
		}
	}

	public void insertNewPost(Doc doc) {
		documents.add(doc);
		setDocuments(documents);
		SHCMethod();
		documents.clear();
	}

	public void run(String inputFile, int numberOfBin, double threshold,
			int minHistogram, double differenceThreshold, String resultFile,
			int isDraw, boolean gui) throws IOException {
		ArrayList<Doc> documents = new ArrayList<Doc>();
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		String line = null;
		int id = 1;
		while ((line = reader.readLine()) != null) {
			String[] splits = line.split("\\s+");
			Doc document = new Doc();
			HashMap<String, Double> docTermVector = new HashMap<String, Double>();
			for (int i = 0; i < splits.length; i++) {
				docTermVector.put(i + "", Double.parseDouble(splits[i]));
			}
			document.setId(id);
			document.setDocTermVector(docTermVector);
			documents.add(document);
			id++;
		}
		reader.close();
		setNumberOfBins(numberOfBin);
		setThreshold(threshold);
		setMinHistogram(minHistogram);
		setDifferenceThreshold(differenceThreshold);
		setDocuments(documents);
		SHCMethod();
		PrintWriter out = new PrintWriter(new File(resultFile));
		for (Doc document : documents) {
			out.print(document.getId());
			for (int i = 0; i < document.getDocTermVector().size(); i++) {
				out.print(" " + document.getDocTermVector().get(i + ""));
			}
			out.println(" " + document.getClusterID());
		}
		out.close();
		if (isDraw == 1) {
			JFreeChart chart1 = Visualizer.createChart(clusters);
			if (!gui) {
				Display display = new Display();
				Shell shell1 = new Shell(display);
				shell1.setSize(1000, 800);
				shell1.setLayout(new FillLayout());
				shell1.setText("SHC");
				ChartComposite frame1 = new ChartComposite(shell1, SWT.NONE,
						chart1, true);
				frame1.pack();
				shell1.open();
				while (!shell1.isDisposed()) {
					if (!display.readAndDispatch())
						display.sleep();
				}
			}
		}
	}

	public static void main(String[] args) {
		if (args.length < 7) {
			System.out
					.println("SHC [InputFile] [NumberOfbin] [Threshold] [minHistogram] [DifferenceThreshold] [ResultFile] [isDraw(0,1)]");
			return;
		}
		try {
			String inputFile = args[0];
			int numberOfBin = Integer.parseInt(args[1]);
			double threshold = Double.parseDouble(args[2]);
			int minHistogram = Integer.parseInt(args[3]);
			double differenceThreshold = Double.parseDouble(args[4]);
			String resultFile = args[5];
			int isDraw = Integer.parseInt(args[6]);
			SHC shc = new SHC();
			shc.run(inputFile, numberOfBin, threshold, minHistogram,
					differenceThreshold, resultFile, isDraw, false);
		} catch (Exception ex) {
			System.out.println("Please check your parameters");
		}
	}

}
