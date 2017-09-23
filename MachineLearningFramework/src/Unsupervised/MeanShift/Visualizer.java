package Unsupervised.MeanShift;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.DefaultXYDataset;

/**
 * 
 * @author Rania Ibrahim
 *
 */
public class Visualizer {

	static JFreeChart createChart(double[][] points, double[][] marker) throws IOException {
		int clusterCount = 0;
		boolean[] taken = new boolean[points.length];
		double eps = 100;
		DefaultXYDataset dataset = new DefaultXYDataset();
		for (int i = 0; i < points.length; i++) {
			if (taken[i])
				continue;
			taken[i] = true;
			ArrayList<double[]> group = new ArrayList<double[]>();
			group.add(points[i]);
			for (int k = i + 1; k < marker.length; k++) {
				if (!taken[k]) {
					if (Math.abs(marker[i][0] - marker[k][0]) < eps
							&& Math.abs(marker[i][1] - marker[k][1]) < eps) {
						group.add(points[k]);
						taken[k] = true;
					}

				}
			}
			if (group.size() > 0.01 * points.length) {
				double[][] value = new double[2][group.size()];
				int count = 0;
				for (double[] point : group) {
					value[0][count] = point[0];
					value[1][count] = point[1];
					count++;
				}
				dataset.addSeries(clusterCount++, value);
				// mode
				double[][] value1 = new double[2][1];
				value1[0][0] = marker[i][0];
				value1[1][0] = marker[i][1];
			}

		}

		JFreeChart chart = ChartFactory.createScatterPlot("Mean Shift",
				"Category", "Value", dataset, PlotOrientation.VERTICAL, true,
				true, false);

		chart.clearSubtitles();
		XYPlot plot = (XYPlot) chart.getPlot();

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setRange(0, 450);
		NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		domainAxis.setRange(0, 800);

		Color brown = new Color(156, 93, 82);
		Color maroon = new Color(139, 28, 98);
		XYItemRenderer renderer = plot.getRenderer();
		Color[] colors = { Color.red, Color.blue, Color.BLACK, Color.green,
				maroon, brown, Color.yellow, Color.MAGENTA, Color.white,
				Color.pink, Color.cyan };
		int i = 0;
		for (i = 0; i < clusterCount; i++) {
			renderer.setSeriesPaint(i, colors[i % colors.length]);
		}

		FileOutputStream fout = new FileOutputStream ("image.jpeg");
		ChartUtilities.writeChartAsJPEG(fout, chart, 500, 500);

		return chart;

	}

}
