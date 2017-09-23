package Unsupervised.MeasurementPerformer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 
 * @author Rania Ibrahim
 * 
 */
public class MeasurementPerformer {

	public void copyFile(String inputFile, String outputFile)
			throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		PrintWriter out = new PrintWriter(new File(outputFile));
		String line = null;
		while ((line = reader.readLine()) != null) {
			out.println(line);
		}
		reader.close();
		out.close();
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	// randindex.exe is Dr. Noha Yousri code
	public String[][] compare(String solution1, String testSolution,
			String resultsFile) throws IOException, InterruptedException {
		// get samples_number
		int samples = 0;
		PrintWriter solution1Writer = new PrintWriter(new File("solution1.txt"));
		BufferedReader solution1Reader = new BufferedReader(new FileReader(
				solution1));
		String solution1Line = null;
		while ((solution1Line = solution1Reader.readLine()) != null) {
			samples++;
			String[] splits = solution1Line.split("\\s+");
			solution1Writer.println(splits[splits.length - 1]);
		}
		solution1Reader.close();
		solution1Writer.close();

		PrintWriter solution2Writer = new PrintWriter(new File("solution2.txt"));
		solution2Writer.println(samples + "");
		BufferedReader solution2Reader = new BufferedReader(new FileReader(
				testSolution));
		String testLine = null;
		while ((testLine = solution2Reader.readLine()) != null) {
			String[] splits = testLine.split("\\s+");
			solution2Writer.println(splits[splits.length - 1]);
		}
		solution2Reader.close();
		solution2Writer.close();

		// copyFile(solution1, "solution1.txt");

		// run randindex.exe
		Process process = Runtime.getRuntime().exec("randindex.exe");
		process.waitFor();
		BufferedReader processReader = new BufferedReader(new FileReader(
				"rand.txt"));
		String[] processSplits = processReader.readLine().split("\\s+");
		processReader.close();
		double fmeasure = Double.parseDouble(processSplits[0]);
		double jaccard = Double.parseDouble(processSplits[2]);
		double rand = Double.parseDouble(processSplits[1]);
		double randIndex = Double.parseDouble(processSplits[3]);
		PrintWriter out = new PrintWriter(new File(resultsFile));
		out.println("Fmeasure = " + (fmeasure));
		out.println("Jaccard = " + (jaccard));
		out.println("Rand = " + (rand));
		out.println("Rand Index = " + (randIndex));
		out.close();

		String[][] data = new String[1][4];
		data[0][0] = round(fmeasure, 2) + "";
		data[0][1] = round(jaccard, 2) + "";
		data[0][2] = round(rand, 2) + "";
		data[0][3] = round(randIndex, 2) + "";
		return data;

	}

	public void convertClusterSolutionToGroundTruth(String inputFile,
			String outputFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		PrintWriter out = new PrintWriter(new File(outputFile));
		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] splits = line.split("\\s+");
			out.println(splits[splits.length - 1]);
		}
		reader.close();
		out.close();
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out
					.println("MeasurementPerformer 1 [GroundTruth] [Solution] [ResultFile]");
			System.out
					.println("MeasurementPerformer 2 [ClusterSolution] [OutputGroundTruthFormat]");
			return;
		}
		try {
			int option = Integer.parseInt(args[0]);
			if (option == 1) {
				String solution1 = args[1];
				String testSolution = args[2];
				String resultsFile = args[3];
				MeasurementPerformer measurmentPerformer = new MeasurementPerformer();
				measurmentPerformer.compare(solution1, testSolution,
						resultsFile);
			} else if (option == 2) {
				String clusterSolution = args[1];
				String OutputGroundTruthFormat = args[2];
				MeasurementPerformer measurmentPerformer = new MeasurementPerformer();
				measurmentPerformer.convertClusterSolutionToGroundTruth(
						clusterSolution, OutputGroundTruthFormat);
			} else {
				System.out.println("Invalid Option");
			}
		} catch (Exception ex) {
			System.out.println("Please check your parameters");
		}
	}

}
