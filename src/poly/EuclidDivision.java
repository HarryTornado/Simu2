package poly;

import java.util.ArrayList;
import java.util.List;

public class EuclidDivision {
	private Poly f, g;
	private List<Poly> q, r;
	private Poly u, v;

	public EuclidDivision(Poly f, Poly g) {
		this.f = f;
		this.g = g;

		algorithm();
	}

	private void algorithm() {
		q = new ArrayList<Poly>();
		r = new ArrayList<Poly>();
		// exchange(f, g);
		r.add(f);
		r.add(g);
		while ((!r.get(r.size() - 1).equalZero())) {
			q.add(r.get(r.size() - 2).quo(r.get(r.size() - 1)));
			r.add(r.get(r.size() - 2).sub(
					r.get(r.size() - 1).mult(q.get(q.size() - 1))));
			if (r.get(r.size() - 1).smaller(1e-14))
				break;
		}
		r.remove(r.size() - 1);
		q.remove(q.size() - 1);
		Poly e = new Poly(new double[] { 1 });
		u = e.add(q.get(q.size() - 1).mult(q.get(q.size() - 2)));
		v = r.get(r.size() - 1).sub(g.mult(u)).quo(f);

		double[] rp = r.get(r.size() - 1).getArray();
		Poly tmp = new Poly(new double[] { rp[0] });
		u = u.quo(tmp);
		v = v.quo(tmp);
	}

	public Poly getVF() {
		return v;
	}

	public Poly getUG() {
		return u;
	}

	public static void main(String args[]) {
		double[] t1 = { 1, -4, 1, 2, 6, -9 };
		double[] t2 = { 1, -3, 1 };
		new EuclidDivision(new Poly(t1), new Poly(t2));
	}
}
