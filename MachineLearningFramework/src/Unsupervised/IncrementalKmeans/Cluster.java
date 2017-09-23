package Unsupervised.IncrementalKmeans;

import java.util.ArrayList;

/**
 * 
 * @author Rania Ibrahim
 *
 */
public class Cluster {

	private ArrayList<Point> points;
	private Point centroid;
	private int id;
	private int count;

	public Cluster() {
		points = new ArrayList<Point>();
		centroid = new Point();
		count = 0;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Point> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}

	public Point getCentroid() {
		return centroid;
	}

	public void setCentroid(Point centroid) {
		this.centroid = centroid;
	}

}
