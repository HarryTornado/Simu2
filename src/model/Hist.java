package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.effortech.harry.graph.Figure;
import net.effortech.harry.graph.FigurePanel;
import net.effortech.harry.graph.LineType;
import Jama.Matrix;

/**
 * @author jyk
 */
public class Hist {
	private Color[] cls = { Color.blue, Color.red, Color.black, Color.cyan,
			Color.magenta, Color.gray };

	private FigurePanel fp;
	private float linewidth;
	/**
	 * @uml.property name="lts"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	private LineType[] lts = { LineType.solid, LineType.dashdot,
			LineType.dotted, LineType.dashed, };

	private int order;

	private String prefix;
	private List<Double> time = new ArrayList<Double>();
	private List<Matrix> trajectory = new ArrayList<Matrix>();

	public Hist(String title, String name, String prefix, float linewidth) {
		fp = new FigurePanel(title, name, "time");
		fp.setLegendVisible(true);
		this.linewidth = linewidth;
		this.prefix = prefix;
	}

	public FigurePanel getFigurePanel() {
		return fp;
	}

	public void makeFp() {
		fp.clear();

		if (time.size() == 0)
			return;

		double[] t = new double[time.size()];
		int i = 0;
		for (double ti : time)
			t[i++] = ti;

		int clsIndex = 0;
		int ltsIndex = 0;

		for (int n = 0; n < order; n++) {
			double[] v = new double[time.size()];
			i = 0;
			for (Matrix x : trajectory) {
				v[i++] = x.get(n, 0);
			}
			Figure f = new Figure(v, t);
			f.setLineType(lts[ltsIndex]);
			f.setColor(cls[clsIndex]);
			f.setTitle(prefix + (n + 1));
			f.setLineWidth(linewidth);

			fp.add(f);

			ltsIndex = (ltsIndex + 1) % lts.length;
			clsIndex = (clsIndex + 1) % cls.length;
		}
	}

	public void reset() {
		time = new ArrayList<Double>();
		trajectory = new ArrayList<Matrix>();
	}

	/**
	 * @param order
	 * @uml.property name="order"
	 */
	public void setOrder(int order) {
		this.order = order;
		reset();
	}

	public void store(Matrix x, double t) {
		trajectory.add(x);
		time.add(t);
	}
}
