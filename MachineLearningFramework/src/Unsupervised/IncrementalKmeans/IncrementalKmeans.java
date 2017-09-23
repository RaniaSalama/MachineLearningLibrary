package Unsupervised.IncrementalKmeans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

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
public class IncrementalKmeans {
	ArrayList<Cluster> clusters;

	public IncrementalKmeans() {

	}

	public Cluster getNearestCluster(Point x) {
		double min = Double.MAX_VALUE;
		Cluster minCluster = null;
		for (Cluster c : clusters) {
			double distance = getEculidanDistance(x, c.getCentroid());
			if (distance < min) {
				min = distance;
				minCluster = c;
			}
		}
		return minCluster;
	}

	public void getKmeans(Point x) {
		Cluster c = getNearestCluster(x);
		c.setCount(c.getCount() + 1);
		Point centroid = c.getCentroid();
		ArrayList<Double> dim = new ArrayList<Double>();
		for (int i = 0; i < x.getDim().size(); i++) {
			double dd = centroid.getDim().get(i)
					+ ((1.0 / c.getCount()) * (x.getDim().get(i) - centroid
							.getDim().get(i)));
			dim.add(dd);
		}
		centroid.setDim(dim);
		c.setCentroid(centroid);
		c.getPoints().add(x);
	}

	public double getEculidanDistance(Point pattern1, Point pattern2) {
		ArrayList<Double> dim1 = pattern1.getDim();
		ArrayList<Double> dim2 = pattern2.getDim();
		double ECDis = 0.0;
		int dimSize = dim1.size();
		for (int k = 0; k < dimSize; k++) {
			ECDis += (dim1.get(k) - dim2.get(k))
					* ((dim1.get(k) - dim2.get(k)));
		}
		ECDis = Math.sqrt(ECDis);
		return ECDis;

	}

	public double getPearsonDistance(Point pattern1, Point pattern2) {
		ArrayList<Double> norm1 = pattern1.norm();
		ArrayList<Double> norm2 = pattern2.norm();
		double pearsonDis = 0.0;
		double r = 0.0;
		int size = norm1.size();
		for (int i = 0; i < size; i++) {
			r += (norm1.get(i) * norm2.get(i));
		}
		r /= size;
		pearsonDis = 1 - r;
		return pearsonDis;
	}

	public void run(String initialCentroidFile, String inputFile,
			String resultFile, boolean isDraw, boolean gui) throws IOException {
		BufferedReader centroidsReader = new BufferedReader(new FileReader(
				initialCentroidFile));
		ArrayList<Point> centroids = new ArrayList<Point>();
		String centroidsLine = null;
		int id = 1;
		while ((centroidsLine = centroidsReader.readLine()) != null) {
			String[] point = centroidsLine.split("\\s+");
			Point c1 = new Point();
			ArrayList<Double> dim = new ArrayList<Double>();
			for (String s : point) {
				dim.add(Double.parseDouble(s));
			}
			c1.setDim(dim);
			c1.setId(id++);
			centroids.add(c1);
		}
		centroidsReader.close();

		clusters = new ArrayList<Cluster>();
		for (Point centroid : centroids) {
			Cluster cluster = new Cluster();
			cluster.setId(centroid.getId());
			cluster.setCentroid(centroid);
			clusters.add(cluster);
		}

		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		ArrayList<Point> points = new ArrayList<Point>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] point = line.split("\\s+");
			Point c1 = new Point();
			ArrayList<Double> dim = new ArrayList<Double>();
			for (String s : point) {
				dim.add(Double.parseDouble(s));
			}
			c1.setDim(dim);
			c1.setId(id++);
			points.add(c1);
		}
		reader.close();
		for (Point p : points) {
			getKmeans(p);
		}

		PrintWriter out = new PrintWriter(new File(resultFile));
		for (Point p : points) {
			int iid = p.getId();
			for (Cluster c : this.clusters) {
				ArrayList<Point> pointss = c.getPoints();
				for (Point pp : pointss) {
					if (p.getId() == pp.getId()) {
						iid = c.getId();
					}
				}
			}
			out.println(iid);
		}
		out.close();

		if (isDraw) {
			JFreeChart chart = Visualizer.createChart(this.clusters);
			if (!gui) {
				Display display = new Display();
				Shell shell = new Shell(display);
				shell.setSize(800, 800);
				shell.setLayout(new FillLayout());
				shell.setText("Incremental kmeans");
				ChartComposite frame = new ChartComposite(shell, SWT.NONE,
						chart, true);

				frame.pack();
				shell.open();
				while (!shell.isDisposed()) {
					if (!display.readAndDispatch())
						display.sleep();
				}
			}
		}
	}

	public static void main(String[] args) {
		if (args.length < 4) {
			System.out
					.println("IncrementalKmeans [CentroidFile] [InputFile] [ResultFile] [isDraw(0,1)]");
			return;
		}
		try {
			String centroidsFile = args[0];
			String inputFile = args[1];
			String resultFile = args[2];
			boolean isDraw = (Integer.parseInt(args[3]) == 1) ? true : false;
			IncrementalKmeans incrementalKmeans = new IncrementalKmeans();
			incrementalKmeans.run(centroidsFile, inputFile, resultFile, isDraw, false);
		} catch (Exception ex) {
			System.out.println("Please check your parameters");
		}
	}

}
