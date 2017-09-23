package Unsupervised.Mitosis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.io.*;

/**
 * 
 * @author Naglaa Ahmed and Rania Ibrahim
 *
 */
public class Phase1 {

	ArrayList<ArrayList<Double>> distances;

	double k;
	double f;
	ArrayList<Association> l1;
	ArrayList<Double> mins;

	public Phase1(double f, double k, int patternNo, String fileName,
			String simMethod) throws IOException {
		this.k = k;
		this.f = f;
		distances = new ArrayList<ArrayList<Double>>();
		mins = new ArrayList<Double>();
		for (int i = 0; i < patternNo; i++)
			distances.add(new ArrayList<Double>());

		calcDisBetweenPatterns(fileName, patternNo, simMethod);
		l1 = generateSortAssocations();
		l1 = deleteDuplicated(l1);

	};

	private ArrayList<Association> deleteDuplicated(ArrayList<Association> l2) {
		ArrayList<Association> l1 = new ArrayList<Association>();
		int p;
		int q;
		double d;
		for (int i = 0; i < l2.size(); i++) {
			Association association = l2.get(i);
			p = association.getP();
			q = association.getQ();
			d = association.getDis();
			if (p > q && d <= f * mins.get(q))
				continue;
			l1.add(association);
		}
		return l1;
	}

	public ArrayList<Association> getL1() {
		return l1;
	}

	public void setL1(ArrayList<Association> l1) {
		this.l1 = l1;
	}

	public ArrayList<Pattern> readFromfile(String fileName, int patternSize) {
		ArrayList<Pattern> patterns = new ArrayList<Pattern>();
		double dim;
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine = null;
			int count = 0;
			while ((strLine = br.readLine()) != null) {
				if (count > patternSize)
					break;
				count++;
				String[] index = strLine.split("\\s+");
				Pattern pattern = new Pattern();
				for (int i = 0; i < index.length; i++) {
					dim = Double.parseDouble(index[i]);
					pattern.getDim().add(dim);
				}
				patterns.add(pattern);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return patterns;
	}

	public void calcDisBetweenPatterns(String readFileName, int NoOfPatterns, String simMethod)
			throws FileNotFoundException {

		ArrayList<Pattern> patterns = readFromfile(readFileName, NoOfPatterns);
		double distance = 0.0;
		double[] ar = new double[NoOfPatterns];
		File writeToFile = new File("distances.txt");
		PrintWriter out = new PrintWriter(writeToFile);
		for (int i = 0; i < NoOfPatterns; i++) {
			for (int j = 0; j < NoOfPatterns; j++) {
				if (i == j)
					distance = 148800.0;
				else {
					if (simMethod.equalsIgnoreCase("euclidean"))
						distance = getEculidanDistance(patterns.get(i),
								patterns.get(j));
					else if (simMethod.equalsIgnoreCase("pearson"))
						distance = getPearsonDistance(patterns.get(i),
								patterns.get(j));
				}
				ar[j] = distance;
			}
			appendTofile(ar, out);
		}
		out.close();
	}

	public double getEculidanDistance(Pattern pattern1, Pattern pattern2) {
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

	public double getPearsonDistance(Pattern pattern1, Pattern pattern2) {
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

	public void appendTofile(double[] ar, PrintWriter out)
			throws FileNotFoundException {
		for (int i = 0; i < ar.length - 1; i++) {
			out.append(ar[i] + " ");
		}
		out.append(ar[ar.length - 1] + "\n");
	}

	public ArrayList<Integer> Get_Dynamic_Nearest_NeighborsFromFile(String[] p)
			throws IOException {
		double range = 0.0;
		ArrayList<Integer> patterns = new ArrayList<Integer>();
		double min = Double.MAX_VALUE;
		for (int i = 0; i < p.length; i++) {
			double pDistance = Double.parseDouble(p[i]);
			if (pDistance < min)
				min = pDistance;
		}
		range = f * min;
		mins.add(min);
		for (int i = 0; i < p.length; i++) {
			if (Double.parseDouble(p[i]) <= range)
				patterns.add(i);
		}
		return patterns;
	}

	public double Get_Average_Distances(int p) throws IOException {
		double sum = 0;
		double average = 0;
		int size = distances.get(p).size();
		for (int i = 0; i < size; i++)
			sum = sum + distances.get(p).get(i);
		average = sum / size;
		return average;
	}

	public ArrayList<Association> generateSortAssocations() throws IOException {
		ArrayList<Integer> patterns = new ArrayList<Integer>();
		ArrayList<Association> l1 = new ArrayList<Association>();
		BufferedReader reader = new BufferedReader(new FileReader(new File(
				"distances.txt")));
		String line = null;
		int i = 0;
		while ((line = reader.readLine()) != null) {
			String[] distn = line.split("\\s+");
			patterns = Get_Dynamic_Nearest_NeighborsFromFile(distn);
			for (int j = 0; j < patterns.size(); j++) {
				l1.add(new Association(i, patterns.get(j), Double
						.parseDouble(distn[patterns.get(j)])));
				distances.get(i)
						.add(Double.parseDouble(distn[patterns.get(j)]));
			}
			i++;
		}
		reader.close();

		Collections.sort(l1);
		
		return l1;
	}

	public static HashMap<Integer, String> loadDataFromFile(String fileName)
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
}
