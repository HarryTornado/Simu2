package poly;

/**
 * Given A, B, and Y, find the solution for 
 * 		AD + BC = Y
 **/
public class PoleAssignment {
	private Poly a, b, y;
	private EuclidDivision ed;
	private int flag = 0;

	public PoleAssignment(Poly a, Poly b, Poly y) {
		this.a = a.reshape();
		this.b = b.reshape();
		this.y = y.reshape();

		eculid();
	}

	private void eculid() {
		if (a.higherandequal(b)) {
			ed = new EuclidDivision(a, b);
			flag = 1;
		} else
			ed = new EuclidDivision(b, a);
	}

	public Poly getD() {
		if (flag == 1)
			return ed.getVF().mult(y);
		else
			return ed.getUG().mult(y);
	}

	public Poly getC() {
		if (flag == 1)
			return ed.getUG().mult(y);
		else
			return ed.getVF().mult(y);

	}

	public static void main(String args[]) {
		double[] t1 = { 1, 0, 0, 0 };
		double[] t2 = { 1 };
		PoleAssignment cd = new PoleAssignment(new Poly(t2), new Poly(t1), new Poly(
				new double[] { 1, 3, 3, 1 }));
		System.out.println("c= " + cd.getD());
		System.out.println("d= " + cd.getC());
	}

}
