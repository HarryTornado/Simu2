package poly;

public class Poly {
	static class Divider {
		private Poly quo, rem;

		public Divider(Poly num, Poly den) {
			double tp = den.p[0];
			if (Math.abs(tp) < epsilon)
				throw new RuntimeException("Divide Poly by Zero!");
			if (den.p.length > num.p.length) {
				quo = new Poly();
				rem = den;
			} else {
				double[] quo = new double[num.p.length - den.p.length + 1];
				Poly tmp = num;

				int idx = tmp.p.length - den.p.length;
				do {
					double v = tmp.p[0] / den.p[0];
					quo[quo.length - idx - 1] = v;
					double[] qq = new double[tmp.p.length - 1];
					for (int i = 0; i < qq.length; i++)
						qq[i] = tmp.p[i + 1];
					for (int i = 1; i < den.p.length; i++)
						qq[i - 1] -= v * den.p[i];
					tmp = new Poly(qq);
					idx = tmp.p.length - den.p.length;
				} while (idx >= 0);
				this.quo = new Poly(quo);
				rem = tmp;
			}
		}

		public Poly getQuo() {
			return quo;
		}

		public Poly getRem() {
			return rem;
		}
	}

	private final static double epsilon = 1e-10;

	private double[] p;
	
	public int getOrder(){
		return p.length-1;
	}

	public Poly mult(double v) {
		double[] sp = new double[p.length];
		for (int i = 0; i < sp.length; i++)
			sp[i] = v * p[i];
		return new Poly(sp);
	}

	public Poly mult(Poly q) {
		double[] prod = new double[q.p.length + p.length - 1];

		for (int i = 0; i < q.p.length; i++) {
			double v = q.p[i];
			for (int j = 0; j < p.length; j++) {
				prod[i + j] += p[j] * v;
			}
		}

		return new Poly(prod);
	}

	public Poly() {
		p = new double[1];
	}

	public Poly(double[] p) {
		int off = 0;
		while (off < p.length - 1 && Math.abs(p[off]) <= epsilon)
			off++;
		this.p = new double[p.length - off];
		for (int i = 0; i < this.p.length; i++)
			this.p[i] = p[off + i];
	}

	public Poly add(Poly q) {
		double[] sp = new double[Math.max(p.length, q.p.length)];

		int off = sp.length - p.length;
		for (int i = 0; i < p.length; i++)
			sp[off + i] += p[i];

		off = sp.length - q.p.length;
		for (int i = 0; i < q.p.length; i++)
			sp[off + i] += q.p[i];

		return new Poly(sp);
	}

	public Poly sub(Poly q) {
		double[] sp = new double[Math.max(p.length, q.p.length)];

		int off = sp.length - p.length;
		for (int i = 0; i < p.length; i++)
			sp[off + i] += p[i];

		off = sp.length - q.p.length;
		for (int i = 0; i < q.p.length; i++)
			sp[off + i] -= q.p[i];

		return new Poly(sp);
	}

	public Poly reshape() {
		double[] t;
		int index = p.length - 1;
		for (int i = 0; i < p.length; i++) {
			if (Math.abs(p[i]) > epsilon) {
				index = i;
				break;
			}
		}
		t = new double[p.length - index];
		for (int i = 0; i < t.length; i++)
			t[i] = p[i + index];
		Poly tt = new Poly(t);
		return tt;
	}

	public boolean higherandequal(Poly q) {
		double[] qq = q.p;
		if (p.length >= qq.length)
			return true;
		else
			return false;
	}

	public double[] getArray() {
		return p;
	}

	public boolean equalZero() {
		for (double tt : p) {
			if (Math.abs(tt) > epsilon) {
				return false;
			}
		}
		return true;
	}

	public boolean smaller(double tmp) {
		boolean flag = true;
		for (int i = 0; i < p.length - 1; i++) {
			if (Math.abs(p[i]) > epsilon) {
				flag = false;
			}

		}
		if (flag == true && p[p.length - 1] < tmp)
			return true;
		else
			return false;
	}

	public String toString() {
		StringBuffer ss = new StringBuffer();
		int order = p.length - 1;
		for (double t : p) {
			if (order == p.length - 1) {
				ss.append(t);
				if (order > 0)
					ss.append("s^" + order);
			} else {
				if (Math.abs(t) > epsilon) {
					if (t > 0)
						ss.append("+");
					else
						ss.append("-");
					ss.append(Math.abs(t));
					if (order > 0)
						ss.append("s^" + order);
				}
			}
			order--;
		}
		return ss.toString();
	}

	public Poly quo(Poly den) {
		Divider dv = new Divider(this, den);
		return dv.quo;
	}

	public static void main(String args[]) {
		double[] t = { 0, 1, 1 };
		double[] s = { 3, 1, 1 };
		Poly f = new Poly(t).reshape();
		Poly g = new Poly(s).reshape();
		System.out.println("poly1: " + g);
		System.out.println("poly2: " + f);
		Divider dv = new Divider(g, f);
		System.out.println("quo: " + dv.getQuo());
		System.out.println("rem: " + dv.getRem());
	}
}
