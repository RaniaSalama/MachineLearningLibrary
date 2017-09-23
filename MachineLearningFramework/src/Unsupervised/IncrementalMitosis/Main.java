package Unsupervised.IncrementalMitosis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.SortedSet;
import java.util.TreeSet;

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
public class Main {

	public void cluster(String fileName, double f, double k, int patternNo,
			String distanceMethod, int doOutlierHandling, String outputFile,
			int isDraw, boolean gui) throws IOException {
		DistanceManager distanceManager = new DistanceManager(distanceMethod);
		Phase1 phase1 = new Phase1(f, k, distanceManager);
		Phase2 phase2 = new Phase2(f, k);
		Phase3 phase3 = new Phase3(f, k, distanceManager);
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		ArrayList<Pattern> points = new ArrayList<Pattern>();
		ArrayList<Association> left = new ArrayList<Association>();
		ArrayList<Association> associations = new ArrayList<Association>();

		for (int i = 1; i <= patternNo; i++) {
			String pattern = reader.readLine();
			String[] line = pattern.split("\\s+");
			Pattern point = new Pattern();
			HashMap<String, Double> map = new HashMap<String, Double>();
			int id = 0;
			for (String token : line) {
				map.put(id + "", Double.parseDouble(token));
				id++;
			}
			point.setQuery(pattern);
			point.setDim(map);
			point.setId(i);
			point.setClusterID(i);
			point.setFinalClusterID(i);
			associations = phase1.getIncrementalFRange(point, points);
			ArrayList<Association> fClusterAssociations = phase2
					.getFrangeClusterAssociation(associations, point);
			points.add(point);
			phase2.combineTwoArrayLists(associations, left);
			phase2.combineTwoArrayLists(associations, fClusterAssociations);
			left = phase2.incrementalMergeCluster(associations);
			left = phase2.refineStepOne(left);
			phase3.refineClusters(phase2.clusters, points);
			phase2.clusters = phase3.clusters;
			phase2.clusterMap = phase3.clusterMap;
		}
		if (doOutlierHandling == 1)
			phase3.outlierHandling(phase3.clusters, patternNo, points);
		reader.close();

		HashMap<Integer, String> patternsMap = loadDataFromFile(fileName);
		LinkedHashMap<Integer, Integer> patterns = new LinkedHashMap<Integer, Integer>();
		for (Cluster cluster : phase3.clusters) {
			ArrayList<Pattern> patternsInCluster = cluster.getPatterns();
			for (Pattern pattern : patternsInCluster) {
				patterns.put(pattern.getId(), cluster.getId());
			}
		}
		for (int i = 1; i <= patternNo; i++) {
			if (!patterns.containsKey(i))
				patterns.put(i, -1);
		}
		// sort
		SortedSet<Integer> patternsIds = new TreeSet<Integer>(patterns.keySet());
		PrintWriter out = new PrintWriter(new File(outputFile));
		for (Integer patternId : patternsIds) {
			out.println(patternId + " " + patternsMap.get(patternId) + " "
					+ patterns.get(patternId));
		}
		out.close();
		if (isDraw == 1) {
			JFreeChart chart1 = Visualizer.createChart(phase3.clusters,
					patternsMap, patternNo);
			if (!gui) {
				Display display = new Display();
				Shell shell1 = new Shell(display);
				shell1.setSize(1000, 800);
				shell1.setLayout(new FillLayout());
				shell1.setText("Incremental Mitosis Clusters");
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

	public HashMap<Integer, String> loadDataFromFile(String fileName)
			throws IOException {
		HashMap<Integer, String> patternMap = new HashMap<Integer, String>();

		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;
		int i = 1;
		while ((line = reader.readLine()) != null) {
			patternMap.put(i++, line);
		}
		reader.close();
		return patternMap;
	}

	public static void main(String[] args) throws IOException {
		if (args.length < 8) {
			System.out
					.println("IncrementalMitosis [f] [k] [inputFilename] [patternNumber] [distanceMethod(euclidean,cosine,pearson,KLD,jaccard)] [outlierHandlingUsage(0,1)] [outputFile] [draw(0,1)]");
			return;
		}
		try {
			double f = Double.parseDouble(args[0]);
			double k = Double.parseDouble(args[1]);
			String inputFileName = args[2];
			int patternNo = Integer.parseInt(args[3]);
			String distanceMethod = args[4];
			int isUseOutlierHandler = Integer.parseInt(args[5]);
			String outputFile = args[6];
			int isDraw = Integer.parseInt(args[7]);
			Main main = new Main();
			main.cluster(inputFileName, f, k, patternNo, distanceMethod,
					isUseOutlierHandler, outputFile, isDraw, false);
		} catch (Exception ex) {
			System.out.println("Error please check your parameters");
		}
	}

}
