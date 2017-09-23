package Unsupervised.DBSCAN;

import java.util.Vector;

/**
 * 
 * @author Sara Shokry
 */
public class DataSet {

	Vector<Point> points;
	int currentID = 1;

	public DataSet() {
		points = new Vector<Point>();
	}

	public DataSet(Vector<Point> pointsList) {
		points = new Vector<Point>(pointsList);
	}

	public void reset() {
		for (Point p : points) {
			p.setLabel(Point.UNCLASSIFIED);
		}
	}

	public int size() {
		if (points != null) {
			return points.size();
		} else {
			return 0;
		}
	}

	public Point getPoint(int i) {
		return points.get(i);
	}

	public void addPointAutoID(Point p) {
		p.setID(currentID);
		currentID++;
		points.add(p);
	}

	public void addPoint(Point p) {
		points.add(p);
	}
}
