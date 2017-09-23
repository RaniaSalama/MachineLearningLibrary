package Unsupervised.IncrementalMitosis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 
 * @author Rania Ibrahim
 *
 */
public class Phase3 {

	public ArrayList<Cluster> clusters;
	public HashMap<Integer, Cluster> clusterMap;
	double f;
	double k;
	DistanceManager distanceManager;

	public Phase3(double f, double k, DistanceManager distanceManager)
			throws IOException {
		this.f = f;
		this.k = k;
		clusters = new ArrayList<Cluster>();
		clusterMap = new HashMap<Integer, Cluster>();
		this.distanceManager = distanceManager;
	}

	public ArrayList<Cluster> getFinalListOfClusters() {
		return clusters;
	}

	public void setFinalListOfClusters(ArrayList<Cluster> finalListOfClusters) {
		this.clusters = finalListOfClusters;
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

	public double Get_Harmonic_Average(Cluster c) {
		double sum = 0.0;
		double harm = 0.0;
		int size = 0;
		for (int i = 0; i < c.getClusterList().size(); i++) {
			Association association = c.getClusterList().get(i);
			Pattern p = association.getP();
			Pattern q = association.getQ();
			double d = association.getDis();
			if (d <= p.getMin() * f || d <= q.getMin() * f) {
				sum = sum + (1 / d);
				size++;
			}
		}
		harm = size / sum;
		return harm;
	}

	public int searchInFinalCluList(int pattern) {
		for (int i = 0; i < clusters.size(); i++)
			if (clusters.get(i).getPatterns().contains(pattern))
				return clusters.get(i).getId();
		return -1;
	}

	public Cluster getFinalClusterFromList(int clusterId) {
		for (int i = 0; i < clusters.size(); i++)
			if (clusters.get(i).getId() == clusterId)
				return clusters.get(i);
		return null;
	}

	public int searchFinalById(int id) {
		for (int i = 0; i < clusters.size(); i++)
			if (clusters.get(i).getId() == id)
				return i;
		return -1;
	}

	public void refineClusters(ArrayList<Cluster> intiClusters,
			ArrayList<Pattern> points) throws IOException {
		// initial
		clusterMap = new HashMap<Integer, Cluster>();
		clusters = new ArrayList<Cluster>();
		for (Cluster currCluster : intiClusters) {
			if (currCluster.isChanged()) {
				currCluster.setChanged(false);
				double harm = Get_Harmonic_Average(currCluster);
				ArrayList<Association> orginAssociations = currCluster
						.getClusterList();
				ArrayList<Association> associations = new ArrayList<Association>();
				HashSet<Integer> pointsHashSet = new HashSet<Integer>();
				for (Association o : orginAssociations) {
					associations.add(new Association(o.getP(), o.getQ(), o
							.getDis()));
				}
				Collections.sort(associations);
				for (Association association : associations) {
					Pattern p = association.getP();
					Pattern q = association.getQ();
					double d = association.getDis();
					if (d <= p.getMin() * f || d <= q.getMin() * f) {
						Cluster clusterOfP = null;
						if (!pointsHashSet.contains(p.getId())) {
							p.setClusterID(p.getId());
							p.setFinalClusterID(p.getId());
							pointsHashSet.add(p.getId());
						}
						if (clusterMap.containsKey(p.getFinalClusterID())) {
							clusterOfP = clusterMap.get(p.getFinalClusterID());
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
							p.setFinalClusterID(p.getId());
							p.setClusterID(p.getId());
						}
						Cluster clusterOfQ = null;
						if (!pointsHashSet.contains(q.getId())) {
							q.setClusterID(q.getId());
							q.setFinalClusterID(q.getId());
							pointsHashSet.add(q.getId());
						}
						if (clusterMap.containsKey(q.getFinalClusterID())) {
							clusterOfQ = clusterMap.get(q.getFinalClusterID());
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
							q.setFinalClusterID(q.getId());
							q.setClusterID(q.getId());
						}

						if (d <= k * harm) {
							if (clusterOfP.getId() != clusterOfQ.getId()) {
								double newAve = d
										+ (clusterOfP.aveDis * clusterOfP
												.getClusterList().size())
										+ (clusterOfQ.aveDis * clusterOfQ
												.getClusterList().size());
								if (clusterOfP.getId() > clusterOfQ.getId()) {
									association.setMerge(true);
									association.setUpClusterAvg(clusterOfP
											.getAveDis());

									clusterOfP.getClusterList()
											.add(association);
									combineTwoArrayLists(
											clusterOfP.getClusterList(),
											clusterOfQ.getClusterList());
									clusterOfP.setAveDis(newAve
											/ clusterOfP.getClusterList()
													.size());
									ArrayList<Pattern> c2Patterns = clusterOfQ
											.getPatterns();
									for (Pattern c2Pattern : c2Patterns) {
										if (!clusterOfP.getPatterns().contains(
												c2Pattern)) {
											c2Pattern
													.setFinalClusterID(clusterOfP
															.getId());
											c2Pattern.setClusterID(clusterOfP
													.getId());
											clusterOfP.getPatterns().add(
													c2Pattern);
										}
									}
									clusters.remove(searchById(clusterOfQ
											.getId()));
									clusterMap.remove(clusterOfQ.getId());
								} else {
									association.setMerge(true);
									association.setUpClusterAvg(clusterOfQ
											.getAveDis());
									clusterOfQ.getClusterList()
											.add(association);
									combineTwoArrayLists(
											clusterOfQ.getClusterList(),
											clusterOfP.getClusterList());
									clusterOfQ.setAveDis(newAve
											/ clusterOfQ.getClusterList()
													.size());
									ArrayList<Pattern> c1Patterns = clusterOfP
											.getPatterns();
									for (Pattern c1Pattern : c1Patterns) {
										if (!clusterOfQ.getPatterns().contains(
												c1Pattern)) {
											c1Pattern
													.setFinalClusterID(clusterOfQ
															.getId());
											c1Pattern.setClusterID(clusterOfQ
													.getId());
											clusterOfQ.getPatterns().add(
													c1Pattern);
										}
									}
									clusters.remove(searchById(clusterOfP
											.getId()));
									clusterMap.remove(clusterOfP.getId());
								}
							} else {
								double newAve = d
										+ (clusterOfP.aveDis * clusterOfP
												.getClusterList().size());
								clusterOfP.getClusterList().add(association);
								clusterOfP.setAveDis(newAve
										/ clusterOfP.getClusterList().size());
							}
						}
					}

				}
			} else {
				clusters.add(currCluster);
				clusterMap.put(currCluster.getId(), currCluster);
			}
		}
	}

	public ArrayList<Pattern> getNegibours(Pattern pattern, double f,
			ArrayList<Pattern> points) throws IOException {
		ArrayList<Pattern> neigbours = new ArrayList<Pattern>();
		double distance = 0;
		for (Pattern mypattern : points) {
			if (mypattern == pattern)
				continue;
			else {
				distance = distanceManager
						.calculateDistance(pattern, mypattern);
			}
			if (distance <= f * pattern.getMin()) {
				neigbours.add(mypattern);
			}
		}
		return neigbours;
	}

	public void outlierHandling(ArrayList<Cluster> myClusters, int size,
			ArrayList<Pattern> points) throws IOException {
		ArrayList<Pattern> o = new ArrayList<Pattern>();
		ArrayList<Cluster> remian = new ArrayList<Cluster>();
		ArrayList<Cluster> reset = new ArrayList<Cluster>();
		// get small clusters
		for (Cluster cluster : myClusters) {
			if (cluster.clusterList.size() <= 0.01 * size) {
				Cluster c = clusterMap.get(cluster.getId());
				clusterMap.remove(c.getId());
				ArrayList<Pattern> patterns = cluster.patterns;
				for (Pattern pattern : patterns) {
					if (!o.contains(pattern))
						o.add(pattern);
				}
			} else {
				remian.add(cluster);
				reset.add(cluster);
			}
		}

		myClusters = remian;
		clusters = remian;

		for (Pattern point : points) {
			if (!clusterMap.containsKey(point.getClusterID())) {
				if (!o.contains(point))
					o.add(point);
			} else if (clusterMap.get(point.getClusterID()).getPatterns()
					.size() == 1) {
				Cluster c = clusterMap.get(point.getClusterID());
				clusterMap.remove(c.getId());
				clusters.remove(c);
				if (!o.contains(point))
					o.add(point);
			}
		}

		for (Pattern pattern : o) {
			double ff = f;
			HashMap<Cluster, Integer> cluster = new HashMap<Cluster, Integer>();
			int max = -1;
			Cluster major = null;
			while (true) {
				if (ff >= 100000)
					break;
				ArrayList<Pattern> neighbours = getNegibours(pattern, ff,
						points);
				for (Pattern neighbourPattern : neighbours) {
					if (!clusterMap
							.containsKey(neighbourPattern.getClusterID()))
						continue;
					Cluster c = clusterMap.get(neighbourPattern.getClusterID());
					if (c.getClusterList().size() == 0)
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
			}
			if (major != null) {
				clusters.remove(major);
				major.patterns.add(pattern);
				pattern.setClusterID(major.getId());
				pattern.setFinalClusterID(major.getId());
				clusterMap.put(major.getId(), major);
				clusters.add(major);
			}
		}

	}

}
