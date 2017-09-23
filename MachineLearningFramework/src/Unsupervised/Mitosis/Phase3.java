package Unsupervised.Mitosis;

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
 * @author Naglaa Ahmed and Rania Ibrahim
 *
 */
public class Phase3 {

	private ArrayList<Association> l1;
	private ArrayList<Association> l2;
	private ArrayList<Cluster> listOfClusters;
	private ArrayList<Cluster> finalListOfClusters;
	double f;
	double k;
	Phase1 phase1;
	Phase2 phase2;
	ArrayList<Integer> deleteditems;

	public Phase3() {

	}

	public ArrayList<Cluster> getFinalListOfClusters() {
		return finalListOfClusters;
	}

	public void setFinalListOfClusters(ArrayList<Cluster> finalListOfClusters) {
		this.finalListOfClusters = finalListOfClusters;
	}

	public ArrayList<Association> getL1() {
		return l1;
	}

	public void setL1(ArrayList<Association> l1) {
		this.l1 = l1;
	}

	public ArrayList<Association> getL2() {
		return l2;
	}

	public void setL2(ArrayList<Association> l2) {
		this.l2 = l2;
	}

	public ArrayList<Cluster> getListOfClusters() {
		return listOfClusters;
	}

	public void setListOfClusters(ArrayList<Cluster> listOfClusters) {
		this.listOfClusters = listOfClusters;
	}

	public double getF() {
		return f;
	}

	public void setF(double f) {
		this.f = f;
	}

	public double getK() {
		return k;
	}

	public void setK(double k) {
		this.k = k;
	}

	public Phase1 getPhase1() {
		return phase1;
	}

	public void setPhase1(Phase1 phase1) {
		this.phase1 = phase1;
	}

	public Phase2 getPhase2() {
		return phase2;
	}

	public void setPhase2(Phase2 phase2) {
		this.phase2 = phase2;
	}

	public double Get_Harmonic_Average(Cluster c) {
		double sum = 0.0;
		double harm = 0.0;
		for (int i = 0; i < c.getClusterList().size(); i++)
			sum = sum + (1 / (c.getClusterList().get(i).getDis()));
		harm = c.getClusterList().size() / sum;
		return harm;
	}

	public int searchInFinalCluList(int pattern) {
		for (int i = 0; i < finalListOfClusters.size(); i++)
			if (finalListOfClusters.get(i).getPatterns().contains(pattern))
				return finalListOfClusters.get(i).getId();
		return -1;
	}

	public int getFinalClusterId(int pattern) throws IOException {
		int res = searchInFinalCluList(pattern);
		if (res == -1) {
			Cluster c = new Cluster();
			c.setId(pattern);
			ArrayList<Integer> a = new ArrayList<Integer>();
			a.add(pattern);
			c.setPatterns(a);
			c.setAveDis(phase1.Get_Average_Distances(pattern));
			finalListOfClusters.add(c);
			return pattern;
		}
		return res;

	}

	public Cluster getFinalClusterFromList(int clusterId) {
		for (int i = 0; i < finalListOfClusters.size(); i++)
			if (finalListOfClusters.get(i).getId() == clusterId)
				return finalListOfClusters.get(i);
		return null;
	}

	public int searchFinalById(int id) {
		for (int i = 0; i < finalListOfClusters.size(); i++)
			if (finalListOfClusters.get(i).getId() == id)
				return i;
		return -1;
	}

	public void refineClusters() throws IOException {
		finalListOfClusters = new ArrayList<Cluster>();
		Association a;
		Cluster currCluster;
		ArrayList<Association> currListOfAss;
		int p;
		int q;
		double d;
		double harm;
		l2.clear();

		for (int i = 0; i < listOfClusters.size(); i++) {
			currCluster = listOfClusters.get(i);
			harm = Get_Harmonic_Average(currCluster);
			currListOfAss = currCluster.getClusterList();
			for (int j = 0; j < currListOfAss.size(); j++) {
				a = currListOfAss.get(j);
				p = a.getP();
				q = a.getQ();
				d = a.getDis();
				Cluster c1 = getFinalClusterFromList(getFinalClusterId(p));
				Cluster c2 = getFinalClusterFromList(getFinalClusterId(q));

				if (d < k * harm) {
					if (c1.getId() != c2.getId()) {
						if (c1.getId() > c2.getId()) {
							ArrayList<Integer> c2Patterns = c2.getPatterns();
							for (Integer c2Pattern : c2Patterns)
								if (!c1.getPatterns().contains(c2Pattern))
									c1.getPatterns().add(c2Pattern);
							finalListOfClusters.remove(searchFinalById(c2
									.getId()));
						} else {
							ArrayList<Integer> c1Patterns = c1.getPatterns();
							for (Integer c1Pattern : c1Patterns)
								if (!c2.getPatterns().contains(c1Pattern))
									c2.getPatterns().add(c1Pattern);
							finalListOfClusters.remove(searchFinalById(c1
									.getId()));
						}
					}
					l2.add(a);
				}
			}
		}

		// Initialize
		for (int i = 0; i < finalListOfClusters.size(); i++)
			finalListOfClusters.get(i).setClusterList(
					new ArrayList<Association>());

		// set associations
		for (int i = 0; i < l2.size(); i++) {
			a = l2.get(i);
			p = a.getP();
			q = a.getQ();
			d = a.getDis();
			Cluster c1 = getFinalClusterFromList(getFinalClusterId(p));
			Cluster c2 = getFinalClusterFromList(getFinalClusterId(q));
			if (c1.getId() == c2.getId() && !c1.getClusterList().contains(a))
				c1.getClusterList().add(a);
		}

	}

	public HashMap<Integer, String> loadDataFromFile(String fileName)
			throws IOException {
		HashMap<Integer, String> patternMap = new HashMap<Integer, String>();

		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;
		int i = 0;
		while ((line = reader.readLine()) != null) {
			patternMap.put(i++, line);
		}
		reader.close();
		return patternMap;
	}

	public ArrayList<Integer> getNegibours(Integer pattern, double f,
			String fileName, String simMethod) throws IOException {
		ArrayList<Integer> neigbours = new ArrayList<Integer>();
		HashMap<Integer, String> patternsMap = loadDataFromFile(fileName);
		double ECDis = 0;
		for (Integer mypattern : patternsMap.keySet()) {
			String[] xy1 = patternsMap.get(mypattern).split("\\s+");
			String[] xy2 = patternsMap.get(pattern).split("\\s+");
			if (mypattern == pattern)
				continue;
			else {
				if (simMethod.equalsIgnoreCase("euclidean"))
					ECDis = getEculidanDistance(xy1, xy2);
				else if (simMethod.equalsIgnoreCase("pearson"))
					ECDis = getpeasonDistance(xy1, xy2);
			}
			if (ECDis <= f * phase1.mins.get(pattern)) {
				neigbours.add(mypattern);
			}
		}
		return neigbours;
	}

	public double getEculidanDistance(String[] pattern1, String[] pattern2) {
		double ECDis = 0.0;
		int dimSize = pattern1.length;
		for (int k = 0; k < dimSize; k++) {
			ECDis += (Double.parseDouble(pattern1[k]) - Double
					.parseDouble(pattern2[k]))
					* (Double.parseDouble(pattern1[k]) - Double
							.parseDouble(pattern2[k]));
		}
		ECDis = Math.sqrt(ECDis);
		return ECDis;
	}

	public ArrayList<Double> norm(String[] pattern) {
		ArrayList<Double> norm = new ArrayList<Double>();
		double mean = 0.0;
		for (String d : pattern)
			mean += Double.parseDouble(d);
		mean /= pattern.length;

		double standardDeviation = 0.0;
		for (String d : pattern) {
			standardDeviation += ((Double.parseDouble(d) - mean) * (Double
					.parseDouble(d) - mean));
		}
		standardDeviation /= pattern.length;
		standardDeviation = Math.sqrt(standardDeviation);
		for (String d : pattern) {
			norm.add((Double.parseDouble(d) - mean) / standardDeviation);
		}
		return norm;
	}

	public double getpeasonDistance(String[] pattern1, String[] pattern2) {
		double pearsonDis = 0.0;
		double r = 0.0;
		int size = pattern1.length;
		ArrayList<Double> norm1 = norm(pattern1);
		ArrayList<Double> norm2 = norm(pattern2);
		for (int i = 0; i < size; i++) {
			r += (norm1.get(i) * norm2.get(i));
		}
		r /= size;
		pearsonDis = 1 - r;
		return pearsonDis;
	}

	public void outlierHandling(ArrayList<Cluster> clusters, int size,
			String fileName, String simMethod) throws IOException {
		ArrayList<Integer> o = new ArrayList<Integer>();
		ArrayList<Cluster> remain = new ArrayList<Cluster>();
		ArrayList<Cluster> reset = new ArrayList<Cluster>();
		// get small clusters
		for (Cluster cluster : clusters) {
			if (cluster.clusterList.size() < 0.01 * size) {
				ArrayList<Integer> patterns = cluster.patterns;
				for (Integer pattern : patterns) {
					if (!o.contains(pattern))
						o.add(pattern);
				}
			} else {
				remain.add(cluster);
				reset.add(cluster);
			}
		}

		// get points that its own cluster
		HashMap<Integer, String> patternsMap = loadDataFromFile(fileName);
		for (Integer pattern : patternsMap.keySet()) {
			Cluster c = getFinalClusterFromList(getFinalClusterId(pattern));
			if (c.clusterList.size() == 0) {
				if (!o.contains(pattern))
					o.add(pattern);
			}
		}
		clusters = remain;
		finalListOfClusters = remain;

		p: for (Integer pattern : o) {
			double ff = f;
			HashMap<Cluster, Integer> cluster = new HashMap<Cluster, Integer>();
			int max = -1;
			Cluster major = null;
			while (true) {
				ArrayList<Integer> neighbours = getNegibours(pattern, ff,
						fileName, simMethod);
				if (neighbours.size() == 0)
					continue p;
				for (Integer neighbourPattern : neighbours) {
					Cluster c = getFinalClusterFromList(getFinalClusterId(neighbourPattern));
					if (c.clusterList.size() == 0)
						continue;
					int count = 0;
					if (cluster.containsKey(c))
						count = cluster.get(c);
					cluster.put(c, count + 1);
					if (count + 1 > max) {
						max = count + 1;
						major = c;
					}
				}
				if (max == -1) {
					ff = 2 * ff;
				} else {
					break;
				}
				if (ff > 10000)
					continue p;
			}
			getFinalClusterFromList(getFinalClusterId(pattern));
			major.patterns.add(pattern);
		}
		finalListOfClusters = reset;
	}

	public void run(double f, double k, String inputFileName, int patternNo,
			String simMethod, int isUseOutlierHandler, String outputFile,
			int isDraw, boolean gui) throws IOException {
		this.f = f;
		this.k = k;
		listOfClusters = new ArrayList<Cluster>();
		phase2 = new Phase2(f, k, patternNo, inputFileName, simMethod);
		phase2.MergePatternsIntoClusters();
		phase2.refineStepOne();
		phase1 = phase2.phase1;
		l1 = phase2.getL1();
		l2 = phase2.getL2();
		listOfClusters = phase2.getListOfClusters();
		deleteditems = new ArrayList<Integer>();
		this.refineClusters();
		if (isUseOutlierHandler == 1) {
			this.outlierHandling(this.finalListOfClusters, patternNo,
					inputFileName, simMethod);
		}
		HashMap<Integer, String> patternsMap = this
				.loadDataFromFile(inputFileName);
		ArrayList<Cluster> clusters = this.finalListOfClusters;
		LinkedHashMap<Integer, Integer> patterns = new LinkedHashMap<Integer, Integer>();
		for (Cluster cluster : clusters) {
			ArrayList<Integer> patternsInCluster = cluster.getPatterns();
			for (Integer pattern : patternsInCluster) {
				patterns.put(pattern, cluster.getId());
			}
		}
		for (int i = 0; i < patternNo; i++) {
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
			JFreeChart chart1 = Visualizer.createChart(
					this.finalListOfClusters, patternsMap, patternNo);
			if (!gui) {
				Display display = new Display();
				Shell shell1 = new Shell(display);
				shell1.setSize(1000, 800);
				shell1.setLayout(new FillLayout());
				shell1.setText("Mitosis Clusters");
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

		if (args.length < 8) {
			System.out
					.println("Mitosis [f] [k] [inputFilename] [patternNumber] [distanceMethod(euclidean,pearson)] [outlierHandlingUsage(0,1)] [outputFile] [draw(0,1)]");
			return;
		}
		try {
			double f = Double.parseDouble(args[0]);
			double k = Double.parseDouble(args[1]);
			String inputFileName = args[2];
			int patternNo = Integer.parseInt(args[3]);
			String simMethod = args[4];
			int isUseOutlierHandler = Integer.parseInt(args[5]);
			String outputFile = args[6];
			int isDraw = Integer.parseInt(args[7]);
			Phase3 phase3 = new Phase3();
			phase3.run(f, k, inputFileName, patternNo, simMethod,
					isUseOutlierHandler, outputFile, isDraw, false);
		} catch (Exception ex) {
			System.out.println("Error please check your parameters");
		}
	}
}
