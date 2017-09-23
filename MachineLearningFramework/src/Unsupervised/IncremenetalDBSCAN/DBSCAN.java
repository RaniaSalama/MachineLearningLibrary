package Unsupervised.IncremenetalDBSCAN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * 
 * @author Sara Shokry
 */
public class DBSCAN {

	private DataSet dataset;
	private int clustersNumbers;
	private float epslion;
	private int minPoints;
	private HashMap<Integer, Vector<Point>> clustersMap;

	/*********** Constructors *******************/
	public DBSCAN(int minPoints, float epslion) {
		this.minPoints = minPoints;
		this.epslion = epslion;
		dataset = new DataSet();
		clustersNumbers = 0;
		clustersMap = new HashMap<Integer, Vector<Point>>();

	}

	public HashMap<Integer, Vector<Point>> getClustersMap() {
		return clustersMap;
	}

	public void setClustersMap(HashMap<Integer, Vector<Point>> clustersMap) {
		this.clustersMap = clustersMap;
	}

	public DBSCAN(DataSet d, float epslion, int minP) {
		dataset = new DataSet(d.points);
		this.epslion = epslion;
		this.minPoints = minP;
		clustersNumbers = 0;
		clustersMap = new HashMap<Integer, Vector<Point>>();
	}

	public int getClustersNumbers() {
		return clustersNumbers;
	}

	public void setClustersNumbers(int clustersNumbers) {
		this.clustersNumbers = clustersNumbers;
	}

	public DataSet getDataset() {
		return dataset;
	}

	public void setDataset(DataSet dataSet) {
		dataset = new DataSet(dataSet.points);
		;
	}

	public float getEpslion() {
		return epslion;
	}

	public void setEpslion(float epslion) {
		this.epslion = epslion;
	}

	public int getMinPoints() {
		return minPoints;
	}

	public void setMinPoints(int minPoints) {
		this.minPoints = minPoints;
	}

	private Vector<Point> getNeighbors(Point p) {

		Vector<Point> neighbors = new Vector<Point>();
		for (Point q : dataset.points) {
			if ((p.getID() != q.getID()) && p.distance(q) <= this.epslion) {
				neighbors.add(q);
			}
		}
		return neighbors;
	}

	/*********** Incremental part *******************/
	public void insertNewPost(Point point) {
		Point p = (Point) point;
		this.dataset.addPointAutoID(p);
		Vector<Point> pNeighboors = getNeighbors(p);
		Vector<Point> updSeeds = updSet(p, pNeighboors);
		if (updSeeds.isEmpty()) {
			p.setLabel(Point.NOISE);
		} else {
			Set<Integer> changedCluster = new HashSet<Integer>();
			ArrayList<Point> NoisePoints = new ArrayList<Point>();
			for (Point q : updSeeds) {
				if (getNeighbors(q).size() >= minPoints) {
					if (q.getClusterID() > 0) {
						changedCluster.add(q.getClusterID());
					} else if (q.getLabel() == Point.NOISE) {
						NoisePoints.add(q);
					}
				} else if (getNeighbors(q).size() < minPoints) {
					if (q.getClusterID() > 0) {
						changedCluster.add(q.getClusterID());
					} else if (q.getLabel() == Point.NOISE) {
						NoisePoints.add(q);
					}
				}
			}
			if (changedCluster.isEmpty()) {
				if (NoisePoints.size() != 0) {
					this.clustersNumbers++;
					p.setClusterID(clustersNumbers);
					p.setNumOfNeighboors(pNeighboors.size());
					Vector<Point> clusterPoints = new Vector<Point>();
					clusterPoints.add(p);
					for (Point q : updSeeds) {
						q.setClusterID(clustersNumbers);
						if (q.getID() != p.getID()) {
							clusterPoints.add(q);
						}
					}
					if (clusterPoints.size() > 0) {
						clustersMap.put(clustersNumbers, clusterPoints);
					}
				} else {
					p.setLabel(Point.NOISE);
				}

			} else if (changedCluster.size() == 1) {
				int absorbationClusterID = (Integer) changedCluster.toArray()[0];
				p.setClusterID(absorbationClusterID);
				clustersMap.get(absorbationClusterID).add(p);
				for (Point q : updSeeds) {
					if (q.getID() != p.getID()
							&& q.getClusterID() != absorbationClusterID) {
						clustersMap.get(absorbationClusterID).add(q);
						q.setClusterID(absorbationClusterID);
					}
				}
			} else if (changedCluster.size() > 1) {
				Object changedClusterIDs[] = changedCluster.toArray();
				int mergerClusterID = (Integer) changedClusterIDs[0];
				p.setClusterID(mergerClusterID);
				clustersMap.get(mergerClusterID).add(p);
				for (Point q : updSeeds) {
					if (q.getID() != p.getID()
							&& q.getClusterID() != mergerClusterID) {
						clustersMap.get(mergerClusterID).add(q);
						q.setClusterID(mergerClusterID);
					}
				}
				for (int i = 1; i < changedClusterIDs.length; i++) {
					Vector<Point> changeList = clustersMap
							.get(changedClusterIDs[i]);
					for (Point q : changeList) {
						q.setClusterID(mergerClusterID);
						clustersMap.get(mergerClusterID).add(q);
					}
					clustersMap.remove(changedClusterIDs[i]);
				}
				Vector<Point> x = clustersMap.get(mergerClusterID);
				joinLists(x, updSeeds);
			}
		}

	}

	private Vector<Point> updSet(Point p, Vector<Point> pNeighboors) {
		Vector<Point> updateSets = new Vector<Point>();
		Vector<Point> neighboors = pNeighboors;
		joinLists(updateSets, pNeighboors);
		for (Point q : neighboors) {
			if (q.getNumOfNeighboors() == (this.minPoints - 1)) {
				joinLists(updateSets, getNeighbors(q));
			}
		}
		return updateSets;
	}

	private void joinLists(Vector<Point> L1, Vector<Point> L2) {
		boolean found = false;
		for (Point p : L2) {
			found = false;
			for (int i = 0; (i < L1.size() && !found); i++) {
				if (p.getID() == L1.get(i).getID()) {
					found = true;
				}
			}
			if (!found) {
				L1.add(p);
			}
		}
	}

}
