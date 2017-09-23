package Unsupervised.DBSCAN;

import java.util.HashMap;
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

	/*********** Normal part *******************/
	public void cluster() {
		clustersMap = new HashMap<Integer, Vector<Point>>();
		dataset.reset();
		for (Point p : dataset.points) {
			if (!p.isClassified()) {
				Vector<Point> neighbors = getNeighbors(p);
				if (neighbors.size() < this.minPoints - 1) {
					p.setLabel(Point.NOISE);
				} else {
					clustersNumbers++;
					p.setClusterID(clustersNumbers);
					p.setNumOfNeighboors(neighbors.size());
					Vector<Point> clusterPoints = new Vector<Point>();
					clusterPoints.add(p);
					for (Point q : neighbors) {
						q.setClusterID(clustersNumbers);
						clusterPoints.add(q);
					}
					clustersMap.put(clustersNumbers, clusterPoints);
					expandCluster(neighbors, clustersNumbers);
				}
			}

		}

	}

	private void expandCluster(Vector<Point> pNeighbors, int clusternumber) {

		while (!pNeighbors.isEmpty()) {
			Point q = pNeighbors.get(0);
			Vector<Point> qNeighbors = getNeighbors(q);
			q.setNumOfNeighboors(qNeighbors.size());
			if (qNeighbors.size() >= this.minPoints - 1) {
				for (Point p : qNeighbors) {

					if (p.getLabel() == Point.UNCLASSIFIED) {
						pNeighbors.add(p);
						p.setClusterID(clusternumber);
						(clustersMap.get(clusternumber)).add(p);

					} else if (p.getLabel() == Point.NOISE) {
						p.setClusterID(clusternumber);
						clustersMap.get(clusternumber).add(p);

					}

				}
			}
			pNeighbors.remove(0);

		}

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

}
