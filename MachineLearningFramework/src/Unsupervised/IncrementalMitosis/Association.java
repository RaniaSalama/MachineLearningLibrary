package Unsupervised.IncrementalMitosis;

/**
 * 
 * @author Rania Ibrahim
 *
 */
public class Association implements Comparable<Association> {

	private Pattern p;
	private Pattern q;
	private double dis;
	private boolean isMerge;
	private double upClusterAvg;

	public boolean isMerge() {
		return isMerge;
	}

	public void setMerge(boolean isMerge) {
		this.isMerge = isMerge;
	}

	public double getUpClusterAvg() {
		return upClusterAvg;
	}

	public void setUpClusterAvg(double upClusterAvg) {
		this.upClusterAvg = upClusterAvg;
	}

	public Association(Pattern p1, Pattern q1, double dis1) {
		p = p1;
		q = q1;
		dis = dis1;
		isMerge = false;
		upClusterAvg = 0;
	}

	public Pattern getP() {
		return p;
	}

	public void setP(Pattern p) {
		this.p = p;
	}

	public Pattern getQ() {
		return q;
	}

	public void setQ(Pattern q) {
		this.q = q;
	}

	public double getDis() {
		return dis;
	}

	public void setDis(double dis) {
		this.dis = dis;
	}

	@Override
	public int compareTo(Association oo) {
		if (oo.dis < this.dis)
			return 1;
		else if (oo.dis == this.dis)
			return 0;
		else
			return -1;
	}

	@Override
	public boolean equals(Object o) {
		Association oo = (Association) o;
		if ((oo.getP().getId() == this.getP().getId() && oo.getQ().getId() == this
				.getQ().getId())
				|| (oo.getP().getId() == this.getQ().getId() && oo.getQ()
						.getId() == this.getP().getId())) {
			if (this.dis == oo.dis)
				return true;
			else
				return false;
		} else
			return false;
	}

}
