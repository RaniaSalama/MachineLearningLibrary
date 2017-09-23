package Unsupervised.Mitosis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * 
 * @author Naglaa Ahmed and Rania Ibrahim
 *
 */
public class Phase2 {
	private ArrayList<Association> l1;
	private ArrayList<Association> l2;
	private ArrayList<Cluster> listOfClusters;
	double f;
	double k;
	Phase1 phase1;
	ArrayList<Integer> deleteditems;

	public Phase2(double f, double k, int patternNo, String fileName,
			String simMethod) throws IOException {
		this.f = f;
		this.k = k;
		listOfClusters = new ArrayList<Cluster>();
		phase1 = new Phase1(f, k, patternNo, fileName, simMethod);
		l1 = phase1.getL1();
		deleteditems = new ArrayList<Integer>();
	};

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

	public ArrayList<Association> getL2() {
		return l2;
	}

	public void setL2(ArrayList<Association> l2) {
		this.l2 = l2;
	}

	public ArrayList<Association> getL1() {
		return l1;
	}

	public void setL1(ArrayList<Association> l1) {
		this.l1 = l1;
	}

	public int searchInCluList(int pattern) {
		for (int i = 0; i < listOfClusters.size(); i++)
			if (listOfClusters.get(i).getPatterns().contains(pattern))
				return listOfClusters.get(i).getId();
		return -1;
	}

	public int getClusterId(int pattern) throws IOException {
		int res = searchInCluList(pattern);
		if (res == -1) {
			Cluster c = new Cluster();
			c.setId(pattern);
			ArrayList<Integer> a = new ArrayList<Integer>();
			a.add(pattern);
			c.setPatterns(a);
			c.setAveDis(phase1.Get_Average_Distances(pattern));
			listOfClusters.add(c);
			return pattern;
		}

		return res;
	}

	public Cluster getClusterFromList(int clusterId) {
		for (int i = 0; i < listOfClusters.size(); i++)
			if (listOfClusters.get(i).getId() == clusterId)
				return listOfClusters.get(i);
		return null;
	}

	public int searchById(int id) {
		for (int i = 0; i < listOfClusters.size(); i++)
			if (listOfClusters.get(i).getId() == id)
				return i;
		return -1;
	}

	public void combineTwoArrayLists(ArrayList<Association> a1,
			ArrayList<Association> a2) {
		for (int i = 0; i < a2.size(); i++) {
			if (!a1.contains(a2.get(i)))
				a1.add(a2.get(i));
		}

	}

	public void MergePatternsIntoClusters() throws IOException {
		l2 = new ArrayList<Association>();
		double newAve;
		for (int i = 0; i < l1.size(); i++) {
			Association a = l1.get(i);
			int p = a.getP();
			int q = a.getQ();
			double d = a.getDis();
			Cluster c1 = getClusterFromList(getClusterId(p));
			Cluster c2 = getClusterFromList(getClusterId(q));
			if ((d < (k * (Math.min(c1.getAveDis(), c2.getAveDis()))))
					&& ((Math.max(c1.getAveDis(), c2.getAveDis())) < (k * (Math
							.min(c1.getAveDis(), c2.getAveDis()))))) {

				if (c1.getId() != c2.getId()) {
					newAve = d
							+ (c1.averageDistance * c1.getClusterList().size())
							+ (c2.averageDistance * c2.getClusterList().size());
					if (c1.getId() > c2.getId()) {
						combineTwoArrayLists(c1.getClusterList(),
								c2.getClusterList());
						if (!c1.getClusterList().contains(a))
							c1.getClusterList().add(a);
						Collections.sort(c1.getClusterList());
						c1.setAveDis(newAve / c1.getClusterList().size());
						ArrayList<Integer> c2Patterns = c2.getPatterns();
						for (Integer c2Pattern : c2Patterns)
							if (!c1.getPatterns().contains(c2Pattern))
								c1.getPatterns().add(c2Pattern);
						listOfClusters.remove(searchById(c2.getId()));
					} else {
						combineTwoArrayLists(c2.getClusterList(),
								c1.getClusterList());
						if (!c2.getClusterList().contains(a))
							c2.getClusterList().add(a);
						Collections.sort(c2.getClusterList());
						c2.setAveDis(newAve / c2.getClusterList().size());
						ArrayList<Integer> c1Patterns = c1.getPatterns();
						for (Integer c1Pattern : c1Patterns)
							if (!c2.getPatterns().contains(c1Pattern))
								c2.getPatterns().add(c1Pattern);
						listOfClusters.remove(searchById(c1.getId()));
					}
				} else {
					if (!c1.getClusterList().contains(a)) {
						newAve = d
								+ (c1.averageDistance * c1.getClusterList()
										.size());
						c1.getClusterList().add(a);
						Collections.sort(c1.getClusterList());
						c1.setAveDis(newAve / c1.getClusterList().size());
					}
				}
				deleteditems.add(i);
				l2.add(l1.get(i));
			}
		}
		ArrayList<Association> temp = new ArrayList<Association>();
		for (int i = 0; i < l1.size(); i++) {
			if (!deleteditems.contains(i)) {
				temp.add(l1.get(i));
			}
		}
		l1 = temp;
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

	public void refineStepOne() throws IOException {
		deleteditems.clear();
		double newAve = 0.0;
		for (int i = 0; i < l1.size(); i++) {
			Association a = l1.get(i);
			int p = a.getP();
			int q = a.getQ();
			double d = a.getDis();
			Cluster c1 = getClusterFromList(getClusterId(p));
			Cluster c2 = getClusterFromList(getClusterId(q));
			if (c1.getId() == c2.getId()) {
				if (d < k * c1.getAveDis()) {
					if (!c1.getClusterList().contains(a)) {
						newAve = d
								+ (c1.averageDistance * c1.getClusterList()
										.size());
						c1.getClusterList().add(a);
						c1.setAveDis(newAve / c1.getClusterList().size());
						Collections.sort(c1.getClusterList());
					}
					l2.add(l1.get(i));
					deleteditems.add(i);
				}
			}

		}
		ArrayList<Association> temp = new ArrayList<Association>();
		for (int i = 0; i < l1.size(); i++) {
			if (!deleteditems.contains(i)) {
				temp.add(l1.get(i));
			}
		}
		l1 = temp;
	}
}
