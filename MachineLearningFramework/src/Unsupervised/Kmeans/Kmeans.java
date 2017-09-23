package Unsupervised.Kmeans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * 
 * @author Rania Ibrahim
 *
 */
public class Kmeans {

	public double getEculidanDistance(Pattern pattern, Centroid centroid) {
		ArrayList<Double> dim1 = pattern.getFeatures();
		ArrayList<Double> dim2 = centroid.getFeatures();
		double ECDis = 0.0;
		for (int i = 0; i < dim1.size(); i++) {
			double score1 = dim1.get(i);
			double score2 = dim2.get(i);
			ECDis += (score1 - score2) * (score1 - score2);
		}
		ECDis = Math.sqrt(ECDis);
		return ECDis;
	}

	public void run(String inputFile, String centroidFile, String resultFile,
			int isDraw, boolean gui) throws IOException {
		BufferedReader centroidReader = new BufferedReader(new FileReader(
				centroidFile));
		ArrayList<Centroid> centroids = new ArrayList<Centroid>();
		String centroidLine = null;
		int centroidId = 1;
		while ((centroidLine = centroidReader.readLine()) != null) {
			String[] splits = centroidLine.split("\\s+");
			ArrayList<Double> features = new ArrayList<Double>();
			for (int i = 0; i < splits.length; i++) {
				features.add(Double.parseDouble(splits[i]));
			}
			Centroid centroid = new Centroid();
			centroid.setFeatures(features);
			centroid.setId(centroidId++);
			centroids.add(centroid);
		}
		centroidReader.close();

		int id = 1;
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		ArrayList<Pattern> patterns = new ArrayList<Pattern>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] splits = line.split("\\s+");
			ArrayList<Double> features = new ArrayList<Double>();
			for (int i = 0; i < splits.length; i++) {
				features.add(Double.parseDouble(splits[i]));
			}
			Pattern pattern = new Pattern();
			pattern.setId(id++);
			pattern.setFeatures(features);
			patterns.add(pattern);
		}
		reader.close();

		// kmeans
		while (true) {
			boolean membershipChange = false;
			Iterator<Pattern> patternIterator = patterns.iterator();
			while (patternIterator.hasNext()) {
				Pattern pattern = patternIterator.next();
				Centroid previousCentroid = null;
				if (pattern.getCluster().getPatterns().contains(pattern)) {
					previousCentroid = pattern.getCluster();
					pattern.getCluster().getPatterns().remove(pattern);
				}
				// assign to closest centroid
				Iterator<Centroid> centroidIterator = centroids.iterator();
				Centroid bestCentroid = null;
				double leastDistance = 10000000;
				while (centroidIterator.hasNext()) {
					Centroid centroid = centroidIterator.next();
					double distance = getEculidanDistance(pattern, centroid);
					if (distance < leastDistance) {
						leastDistance = distance;
						bestCentroid = centroid;
					}
				}
				bestCentroid.getPatterns().add(pattern);
				pattern.setClusterID(bestCentroid);
				if (!bestCentroid.equals(previousCentroid))
					membershipChange = true;
			}
			boolean centroidChange = false;
			// re-calculate
			Iterator<Centroid> centroidIterator = centroids.iterator();
			while (centroidIterator.hasNext()) {
				Centroid centroid = centroidIterator.next();
				centroidChange = centroidChange
						|| centroid.calculateCentroids();
			}
			// check finished or not
			if (!membershipChange || !centroidChange)
				break;
		}
		PrintWriter out = new PrintWriter(new File(resultFile));
		Iterator<Pattern> patternIterator = patterns.iterator();
		while (patternIterator.hasNext()) {
			Pattern pattern = patternIterator.next();
			out.print(pattern.getId());
			for (Double feature : pattern.getFeatures()) {
				out.print(" " + feature);
			}
			out.println(" " + pattern.getCluster().getId());
		}
		out.close();

		if (isDraw == 1) {
			JFreeChart chart1 = Visualizer.createChart(centroids);
			if (!gui) {
				Display display = new Display();
				Shell shell1 = new Shell(display);
				shell1.setSize(800, 800);
				shell1.setLayout(new FillLayout());
				shell1.setText("Kmeans");
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
		if (args.length < 4) {
			System.out
					.println("Kmeans [InputFile] [CentroidFile] [ResultFile] [isDraw(0,1)]");
			return;
		}
		try {
			String inputFile = args[0];
			String centroidFile = args[1];
			String resultFile = args[2];
			int isDraw = Integer.parseInt(args[3]);
			Kmeans kmeans = new Kmeans();
			kmeans.run(inputFile, centroidFile, resultFile, isDraw, false);
		} catch (Exception ex) {
			System.out.println("Please check your parameters");
		}
	}
}
