package Unsupervised.MeanShift;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

// not our code and can't find the reference
public class MeanShift {

	public double[][] segmentRGBImage(double[][] points) {

		int rad = 100;
		double[][] marker = new double[points.length][2];

		double shift = 0;
		int iters = 0;
		for (int i = 0; i < points.length; i++) {
			double xc = points[i][0];
			double yc = points[i][1];
			double xcOld, ycOld;
			int pos = i;

			iters = 0;
			do {
				xcOld = xc;
				ycOld = yc;

				float mx = 0;
				float my = 0;
				int num = 0;

				for (int j = 0; j < points.length; j++) {
					double dist = Math
							.sqrt(((points[i][0] - points[j][0]) * (points[i][0] - points[j][0]))
									+ ((points[i][1] - points[j][1]) * (points[i][1] - points[j][1])));
					if (dist < rad) {
						mx += points[j][0];
						my += points[j][1];
						num++;
					}
				}

				float num_ = 1f / num;
				xc = mx * num_;
				yc = my * num_;

				double dx = xc - xcOld;
				double dy = yc - ycOld;

				shift = dx * dx + dy * dy;
				iters++;

			} while (shift > 1E-11 && iters < 3000);

			marker[pos][0] = (int) xc;
			marker[pos][1] = (int) yc;
		}

		return marker;
	}

	public void run(String fileName, int patternNo, String resultFile,
			int isDraw, boolean gui) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		double[][] points = null;
		for (int i = 0; i < patternNo; i++) {
			String[] line = reader.readLine().split("\\s+");
			if (i == 0) {
				points = new double[patternNo][line.length];
			}
			for (int j = 0; j < line.length; j++) {
				points[i][j] = Double.parseDouble(line[j]);
			}
		}
		reader.close();
		MeanShift m = new MeanShift();
		double[][] marker = m.segmentRGBImage(points);
		PrintWriter out = new PrintWriter(new File(resultFile));
		int clusterCount = 0;
		boolean[] taken = new boolean[points.length];
		double eps = 100;
		HashMap<Integer, double[]> pointsLabel = new HashMap<Integer, double[]>();
		for (int i = 0; i < points.length; i++) {
			if (taken[i])
				continue;
			taken[i] = true;
			ArrayList<double[]> group = new ArrayList<double[]>();
			ArrayList<Integer> ids = new ArrayList<Integer>();

			group.add(points[i]);
			ids.add(i);
			for (int k = i + 1; k < marker.length; k++) {
				if (!taken[k]) {
					if (Math.abs(marker[i][0] - marker[k][0]) < eps
							&& Math.abs(marker[i][1] - marker[k][1]) < eps) {
						group.add(points[k]);
						ids.add(k);
						taken[k] = true;
					}

				}
			}
			if (group.size() > 0.01 * points.length) {
				clusterCount++;
				Iterator<Integer> idsIterator = ids.iterator();
				for (double[] point : group) {
					double[] newPoint = new double[point.length + 1];
					for (int j = 0; j < point.length; j++) {
						newPoint[j] = point[j];
					}
					newPoint[newPoint.length - 1] = clusterCount;
					pointsLabel.put(idsIterator.next(), newPoint);
				}
			}
		}
		out.close();

		for (int i = 0; i < points.length; i++) {
			out.print((i + 1) + "");
			double[] point = new double[points[i].length];
			if (pointsLabel.containsKey(i)) {
				point = pointsLabel.get(i);
				for (int j = 0; j < point.length; j++) {
					out.print(" " + point[j]);
				}
				out.println();
			} else {
				for (int j = 0; j < point.length; j++) {
					out.print(" " + point[j]);
				}
				out.println(" " + clusterCount);
				clusterCount++;
			}
		}

		if (isDraw == 1) {
			JFreeChart chart = Visualizer.createChart(points, marker);
			if (!gui) {
				Display display = new Display();
				Shell shell = new Shell(display);
				shell.setSize(800, 800);
				shell.setLayout(new FillLayout());
				shell.setText("Mean Shift");
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
					.println("MeanShift [InputFile] [PatternsNo] [ResultFile] [isDraw(0,1)]");
			return;
		}
		try {
			String inputFile = args[0];
			int patternsNo = Integer.parseInt(args[1]);
			String resultFile = args[2];
			int isDraw = Integer.parseInt(args[3]);
			MeanShift meanShift = new MeanShift();
			meanShift.run(inputFile, patternsNo, resultFile, isDraw, false);
		} catch (Exception ex) {
			System.out.println("Please check your parameters");
		}
	}
}
