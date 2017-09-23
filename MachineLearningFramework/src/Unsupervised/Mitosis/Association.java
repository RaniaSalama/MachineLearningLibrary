package Unsupervised.Mitosis;

/**
 * 
 * @author Naglaa Ahmed and Rania Ibrahim
 *
 */
public class Association implements Comparable<Association> {

	private int p;
	private int q;
	private double dis;

	public Association(int p, int q, double dis) {
		this.p = p;
		this.q = q;
		this.dis = dis;
	}

	public int getP() {
		return p;
	}

	public void setP(int p) {
		this.p = p;
	}

	public int getQ() {
		return q;
	}

	public void setQ(int q) {
		this.q = q;
	}

	public double getDis() {
		return dis;
	}

	public void setDis(double dis) {
		this.dis = dis;
	}

	@Override
	public int compareTo(Association o) {
		if (o.p == this.p && o.q == this.q && o.dis == this.dis)
			return 0;
		else if (o.dis > this.dis)
			return -1;
		return 1;
	}

	@Override
	public boolean equals(Object o) {
		Association oo = (Association) o;
		if (oo.p == this.p && oo.q == this.q && oo.dis == this.dis)
			return true;
		return false;
	}

}
