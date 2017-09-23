package Unsupervised.IncremenetalDBSCAN;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * 
 * @author Sara Shokry
 */
public class Main {
	public void runIncrementalDBSCAN(int minPoints, float epslion,
			String inputFile, String outputFile, boolean isDraw, boolean gui)
			throws NumberFormatException, IOException {
		Point p;
		HashMap<String, Float> s;
		DataInputStream in = new DataInputStream(new FileInputStream(inputFile));
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		HashMap<Integer, String> patternsMap = new HashMap<Integer, String>();
		DBSCAN dbscan = new DBSCAN(minPoints, epslion);
		while ((strLine = br.readLine()) != null) {
			String[] arr = strLine.split("\\s+");
			s = new HashMap<String, Float>();
			p = new Point();
			for (int i = 0; i < arr.length; i++) {
				s.put(i + "", Float.parseFloat(arr[i]));
			}
			p.setTesrms(s);
			dbscan.insertNewPost(p);
			patternsMap.put(p.getID(), strLine);
		}
		br.close();
		PrintWriter out = new PrintWriter(new File(outputFile));
		for (Point point : dbscan.getDataset().points) {
			out.println(point.getID() + " " + patternsMap.get(point.getID())
					+ " " + point.getClusterID());
		}
		out.close();
		if (isDraw) {
			HashMap<Integer, Vector<Point>> clustersMap = dbscan
					.getClustersMap();
			JFreeChart chart1 = Visualizer
					.createChart(clustersMap, patternsMap);
			if (!gui) {
				Display display = new Display();
				Shell shell1 = new Shell(display);
				shell1.setSize(1000, 800);
				shell1.setLayout(new FillLayout());
				shell1.setText("Incremental DBSCAN Clusters");
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
		if (args.length < 5) {
			System.out
					.println("DBSCAN [inputFile] [minPoints] [epslion] [OutputFile] [isDraw(0,1)]");
			return;
		}
		try {
			String inputFile = args[0];
			int minPoints = Integer.parseInt(args[1]);
			float epslion = Float.parseFloat(args[2]);
			String outputFile = args[3];
			boolean isDraw = (Integer.parseInt(args[4]) == 1) ? true : false;
			Main main = new Main();
			main.runIncrementalDBSCAN(minPoints, epslion, inputFile,
					outputFile, isDraw, false);
		} catch (Exception ex) {
			System.out.println("Please check your parameters");
		}
	}
}
