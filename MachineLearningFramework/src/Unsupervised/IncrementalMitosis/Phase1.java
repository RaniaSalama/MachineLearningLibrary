package Unsupervised.IncrementalMitosis;

import java.util.ArrayList;
import java.io.*;

/**
 * 
 * @author Rania Ibrahim
 *
 */
public class Phase1 {

	double k;
	double f;
	DistanceManager distanceManager;

	public Phase1(double f, double k, DistanceManager distanceManager)
			throws IOException {
		this.k = k;
		this.f = f;
		this.distanceManager = distanceManager;
	};

	public ArrayList<Association> getIncrementalFRange(Pattern newPoint,
			ArrayList<Pattern> points) {
		ArrayList<Pattern> changedPoints = new ArrayList<Pattern>();
		double min = Double.MAX_VALUE;
		for (Pattern point : points) {
			double pDistance = distanceManager.calculateDistance(newPoint,
					point);
			if (pDistance < min)
				min = pDistance;
			if (pDistance < point.getMin()) {
				changedPoints.add(point);
				point.setMin(pDistance);
			}
			if (point.getMin() != Double.MAX_VALUE
					&& pDistance <= f * point.getMin()) {
				point.setAverge((point.getAverge() * point.getCount() + pDistance)
						/ (point.getCount() + 1));
				point.setCount(point.getCount() + 1);
			}
		}

		double range = Double.MAX_VALUE;
		if (min != Double.MAX_VALUE)
			range = f * min;
		newPoint.setMin(min);
		ArrayList<Association> incrementalFrange = new ArrayList<Association>();
		double sum = 0.0;
		int count = 0;
		for (Pattern point : points) {
			double pDistance = distanceManager.calculateDistance(newPoint,
					point);
			if (pDistance <= range) {
				newPoint.getFrangeDistance().add(pDistance);
				Association association = new Association(newPoint, point,
						pDistance);
				incrementalFrange.add(association);
				sum += pDistance;
				count++;
			} else if (point.getMin() != Double.MAX_VALUE
					&& pDistance <= f * point.getMin()) { // else if for delete
															// duplicated
				Association association = new Association(point, newPoint,
						pDistance);
				incrementalFrange.add(association);
			}
			if (point.getMin() != Double.MAX_VALUE
					&& pDistance <= f * point.getMin()) {
				point.getFrangeDistance().add(pDistance);
			}
		}
		if (count != 0)
			newPoint.setAverge(sum / count);
		else
			newPoint.setAverge(Double.MAX_VALUE);
		newPoint.setCount(count);

		for (Pattern changedPoint : changedPoints) {
			ArrayList<Double> distances = changedPoint.getFrangeDistance();
			ArrayList<Double> remainingDistances = new ArrayList<Double>();
			double csum = 0;
			int ccount = 0;
			for (Double distance : distances) {
				if (distance <= f * changedPoint.getMin()) {
					remainingDistances.add(distance);
					csum += distance;
					ccount++;
				}
			}
			changedPoint.setAverge(csum / ccount);
			changedPoint.setCount(ccount);
			changedPoint.setFrangeDistance(remainingDistances);
		}

		return incrementalFrange;
	}

}
