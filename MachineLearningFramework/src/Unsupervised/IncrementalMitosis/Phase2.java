package Unsupervised.IncrementalMitosis;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * 
 * @author Rania Ibrahim
 *
 */
public class Phase2 {

	double f;
	double k;
	public ArrayList<Cluster> clusters;
	public HashMap<Integer, Cluster> clusterMap;

	public Phase2(double f, double k) throws IOException {
		this.f = f;
		this.k = k;
		clusters = new ArrayList<Cluster>();
		clusterMap = new HashMap<Integer, Cluster>();
	}

	public ArrayList<Association> incrementalMergeCluster(
			ArrayList<Association> associations) throws IOException {
		Collections.sort(associations);
		double newAve;

		ArrayList<Association> temp = new ArrayList<Association>();
		for (int i = 0; i < associations.size(); i++) {
			Association association = associations.get(i);
			Pattern p = association.getP();
			Pattern q = association.getQ();
			double d = association.getDis();
			if ((d <= p.getMin() * f) || (d <= q.getMin() * f)) {
				Cluster clusterOfP = null;
				if (clusterMap.containsKey(p.getClusterID())) {
					clusterOfP = clusterMap.get(p.getClusterID());
					if (clusterOfP.getPatterns().size() == 1) {
						clusterOfP.setAveDis(p.getAverge());
					}
				} else {
					clusterOfP = new Cluster();
					clusterOfP.setId(p.getId());
					clusterOfP.setAveDis(p.getAverge());
					ArrayList<Pattern> patterns = new ArrayList<Pattern>();
					patterns.add(p);
					clusterOfP.setPatterns(patterns);
					clusters.add(clusterOfP);
					clusterMap.put(clusterOfP.getId(), clusterOfP);
					p.setClusterID(p.getId());
				}
				Cluster clusterOfQ = null;
				if (clusterMap.containsKey(q.getClusterID())) {
					clusterOfQ = clusterMap.get(q.getClusterID());
					if (clusterOfQ.getPatterns().size() == 1) {
						clusterOfQ.setAveDis(q.getAverge());
					}
				} else {
					clusterOfQ = new Cluster();
					clusterOfQ.setId(q.getId());
					clusterOfQ.setAveDis(q.getAverge());
					ArrayList<Pattern> patterns = new ArrayList<Pattern>();
					patterns.add(q);
					clusterOfQ.setPatterns(patterns);
					clusters.add(clusterOfQ);
					clusterMap.put(clusterOfQ.getId(), clusterOfQ);
					q.setClusterID(q.getId());
				}
				if ((d <= (k * (Math.min(clusterOfP.getAveDis(),
						clusterOfQ.getAveDis()))))
						&& ((Math.max(clusterOfP.getAveDis(),
								clusterOfQ.getAveDis())) <= (k * (Math.min(
								clusterOfP.getAveDis(), clusterOfQ.getAveDis()))))) {
					clusterOfP.setChanged(true);
					clusterOfQ.setChanged(true);
					if (clusterOfP.getId() != clusterOfQ.getId()) {
						newAve = d
								+ (clusterOfP.aveDis * clusterOfP
										.getClusterList().size())
								+ (clusterOfQ.aveDis * clusterOfQ
										.getClusterList().size());
						if (clusterOfP.getId() > clusterOfQ.getId()) {
							association.setMerge(true);
							association.setUpClusterAvg(clusterOfP.getAveDis());
							clusterOfP.getClusterList().add(association);
							combineTwoArrayLists(clusterOfP.getClusterList(),
									clusterOfQ.getClusterList());
							clusterOfP.setAveDis(newAve
									/ clusterOfP.getClusterList().size());
							ArrayList<Pattern> c2Patterns = clusterOfQ
									.getPatterns();
							for (Pattern c2Pattern : c2Patterns) {
								if (!clusterOfP.getPatterns().contains(
										c2Pattern)) {
									c2Pattern.setClusterID(clusterOfP.getId());
									clusterOfP.getPatterns().add(c2Pattern);
								}
							}
							clusters.remove(searchById(clusterOfQ.getId()));
							clusterMap.remove(clusterOfQ.getId());
							clusterOfQ = null;
						} else {
							association.setMerge(true);
							association.setUpClusterAvg(clusterOfQ.getAveDis());
							clusterOfQ.getClusterList().add(association);
							combineTwoArrayLists(clusterOfQ.getClusterList(),
									clusterOfP.getClusterList());
							clusterOfQ.setAveDis(newAve
									/ clusterOfQ.getClusterList().size());
							ArrayList<Pattern> c1Patterns = clusterOfP
									.getPatterns();
							for (Pattern c1Pattern : c1Patterns) {
								if (!clusterOfQ.getPatterns().contains(
										c1Pattern)) {
									c1Pattern.setClusterID(clusterOfQ.getId());
									clusterOfQ.getPatterns().add(c1Pattern);
								}
							}
							clusters.remove(searchById(clusterOfP.getId()));
							clusterMap.remove(clusterOfP.getId());
							clusterOfP = null;
						}
					} else {
						newAve = d
								+ (clusterOfP.aveDis * clusterOfP
										.getClusterList().size());
						clusterOfP.getClusterList().add(association);
						clusterOfP.setAveDis(newAve
								/ clusterOfP.getClusterList().size());
					}
				} else {
					temp.add(associations.get(i));
				}
			}
		}
		// return left associations.
		return temp;
	}

	public void combineTwoArrayLists(ArrayList<Association> a1,
			ArrayList<Association> a2) {
		a1.addAll(a2);
	}

	public int searchById(int id) {
		for (int i = 0; i < clusters.size(); i++)
			if (clusters.get(i).getId() == id)
				return i;
		return -1;
	}

	public ArrayList<Association> refineStepOne(
			ArrayList<Association> associations) throws IOException {

		ArrayList<Association> temp = new ArrayList<Association>();
		double newAve = 0.0;
		for (int i = 0; i < associations.size(); i++) {
			Association a = associations.get(i);
			Pattern p = a.getP();
			Pattern q = a.getQ();
			double d = a.getDis();
			Cluster clusterOfP = null;
			if (clusterMap.containsKey(p.getClusterID())) {
				clusterOfP = clusterMap.get(p.getClusterID());
				if (clusterOfP.getPatterns().size() == 1) {
					clusterOfP.setAveDis(p.getAverge());
				}
			} else {
				clusterOfP = new Cluster();
				clusterOfP.setId(p.getId());
				clusterOfP.setAveDis(p.getAverge());
				ArrayList<Pattern> patterns = new ArrayList<Pattern>();
				patterns.add(p);
				clusterOfP.setPatterns(patterns);
				clusters.add(clusterOfP);
				clusterMap.put(clusterOfP.getId(), clusterOfP);
				p.setClusterID(p.getId());
			}
			Cluster clusterOfQ = null;
			if (clusterMap.containsKey(q.getClusterID())) {
				clusterOfQ = clusterMap.get(q.getClusterID());
				if (clusterOfQ.getPatterns().size() == 1) {
					clusterOfQ.setAveDis(q.getAverge());
				}
			} else {
				clusterOfQ = new Cluster();
				clusterOfQ.setId(q.getId());
				clusterOfQ.setAveDis(q.getAverge());
				ArrayList<Pattern> patterns = new ArrayList<Pattern>();
				patterns.add(q);
				clusterOfQ.setPatterns(patterns);
				clusters.add(clusterOfQ);
				clusterMap.put(clusterOfQ.getId(), clusterOfQ);
				q.setClusterID(q.getId());
			}

			if (clusterOfP.getId() == clusterOfQ.getId()
					&& d <= k * clusterOfP.getAveDis()) {
				clusterOfP.setChanged(true);
				newAve = d
						+ (clusterOfP.aveDis * clusterOfP.getClusterList()
								.size());
				clusterOfP.getClusterList().add(a);
				clusterOfP.setAveDis(newAve
						/ clusterOfP.getClusterList().size());
			} else {
				temp.add(associations.get(i));
			}

		}

		return temp;

	}

	public ArrayList<Association> getFrangeClusterAssociation(
			ArrayList<Association> associations, Pattern point) {
		ArrayList<Association> result = new ArrayList<Association>();
		for (Association association : associations) {
			Pattern p = association.getP();
			Pattern q = association.getQ();
			if (!p.equals(point)) {
				if (clusterMap.containsKey(p.getClusterID())) {
					Cluster culsterOfP = clusterMap.get(p.getClusterID());
					if (culsterOfP.getClusterList().size() == 0)
						continue;
					culsterOfP.setChanged(true);
					int startIndex = 0;
					for (int i = 0; i < culsterOfP.getClusterList().size(); i++) {
						Association a = culsterOfP.getClusterList().get(i);
						Pattern pp = a.getP();
						Pattern qq = a.getQ();
						Double distance = a.getDis();
						if (distance > pp.getMin() && distance > qq.getMin()) {
							break;
						}
						if (a.isMerge())
							startIndex = i;
						if (distance > point.getMin()) {
							break;
						}
					}
					for (int i = startIndex; i < culsterOfP.getClusterList()
							.size(); i++) {
						result.add(culsterOfP.getClusterList().get(i));
					}
					ArrayList<Pattern> remainingPatterns = new ArrayList<Pattern>();
					ArrayList<Association> remaningAssociations = new ArrayList<Association>();
					for (int i = 0; i < startIndex; i++) {
						Association associationIn = culsterOfP.getClusterList()
								.get(i);
						if (!remainingPatterns.contains(associationIn.getP())) {
							remainingPatterns.add(associationIn.getP());
						}
						if (!remainingPatterns.contains(associationIn.getQ())) {
							remainingPatterns.add(associationIn.getQ());
						}
						remaningAssociations.add(associationIn);
					}
					// Break the cluster
					if (startIndex == 0) {
						int index = searchById(p.getClusterID());
						clusters.remove(index);
						clusterMap.remove(p.getClusterID());
					}
					for (Pattern pattern : culsterOfP.getPatterns()) {
						if (!remainingPatterns.contains(pattern)) {
							pattern.setClusterID(pattern.getId());
						}
					}
					culsterOfP.setPatterns(remainingPatterns);
					culsterOfP.setClusterList(remaningAssociations);
				}
			}
			if (!q.equals(point)) {
				if (clusterMap.containsKey(q.getClusterID())) {
					Cluster culsterOfQ = clusterMap.get(q.getClusterID());
					if (culsterOfQ.getClusterList().size() == 0)
						continue;

					culsterOfQ.setChanged(true);
					int startIndex = 0;
					for (int i = 0; i < culsterOfQ.getClusterList().size(); i++) {
						Association a = culsterOfQ.getClusterList().get(i);
						Pattern pp = a.getP();
						Pattern qq = a.getQ();
						Double distance = a.getDis();
						if (distance > pp.getMin() && distance > qq.getMin()) {
							break;
						}
						if (a.isMerge())
							startIndex = i;
						if (distance > point.getMin()) {
							break;
						}
					}

					for (int i = startIndex; i < culsterOfQ.getClusterList()
							.size(); i++) {
						result.add(culsterOfQ.getClusterList().get(i));
					}
					ArrayList<Pattern> remainingPatterns = new ArrayList<Pattern>();
					ArrayList<Association> remainingAssociations = new ArrayList<Association>();
					for (int i = 0; i < startIndex; i++) {
						Association associationIn = culsterOfQ.getClusterList()
								.get(i);
						if (!remainingPatterns.contains(associationIn.getP())) {
							remainingPatterns.add(associationIn.getP());
						}
						if (!remainingPatterns.contains(associationIn.getQ())) {
							remainingPatterns.add(associationIn.getQ());
						}
						remainingAssociations.add(associationIn);
					}
					// Break the cluster
					if (startIndex == 0) {
						int index = searchById(q.getClusterID());
						clusters.remove(index);
						clusterMap.remove(q.getClusterID());
					}
					for (Pattern pattern : culsterOfQ.getPatterns()) {
						if (!remainingPatterns.contains(pattern)) {
							pattern.setClusterID(pattern.getId());
						}
					}
					culsterOfQ.setPatterns(remainingPatterns);
					culsterOfQ.setClusterList(remainingAssociations);
				}
			}
		}
		return result;
	}
}