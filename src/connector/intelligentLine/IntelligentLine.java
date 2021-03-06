package connector.intelligentLine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import component.Component;
import component.ElementPanel;

import connector.ConnectLine;
import connector.Connector;
import connector.HVLine;

/**
 * @author  JiHsianLee
 */
public class IntelligentLine extends ConnectLine {
	private Component cpFrom;
	private Component cpTo;
	private List<HVLine> hvLines = new ArrayList<HVLine>();
	private Node ndFrom;
	private Node ndTo;
	/**
	 * @uml.property  name="nodes"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	private Node[][] nodes;
	private ElementPanel panel;
	private SortedSet<Integer> xs = new TreeSet<Integer>();
	private SortedSet<Integer> ys = new TreeSet<Integer>();

	public IntelligentLine(Connector from, Connector to) {
		super(from, to);
		cpFrom = from.getParent();
		cpTo = to.getParent();
		panel = cpFrom.getPanel();

		for (Component c : cpFrom.getPanel().getElements())
			add(c);
		// add(cpFrom);
		// add(cpTo);

		Point mp = Component.getMidPoint(cpFrom, cpTo);

		add(mp);

		addOthers(50, 50);

		buildMap();

		if (ndFrom == null || ndTo == null)
			return;

		ndFrom.setCost(0);

		expand(ndFrom);
	}

	private void add(Component cp) {
		for (Point p : cp.getCrticialPoints())
			add(p);
	}

	private void add(Point p) {
		xs.add(p.x);
		ys.add(p.y);
	}

	private void addOthers(int xGap, int yGap) {
		if (xGap < 20)
			xGap = 20;
		if (yGap < 20)
			yGap = 20;

		for (int x = 0; x < panel.getWidth(); x += xGap) {
			for (int y = 0; y < panel.getHeight(); y += yGap) {
				add(new Point(x, y));
			}
		}
	}

	private void buildMap() {
		nodes = new Node[xs.size()][ys.size()];

		Cord.maxI = xs.size() - 1;
		Cord.maxJ = ys.size() - 1;

		int i = 0, j = 0;
		for (int x : xs) {
			j = 0;
			for (int y : ys) {
				Point p = new Point(x, y);
				if (!(cpFrom.contains(p) || cpTo.contains(p))) {
					Node nd = new Node(p);
					nd.setIj(new Cord(i, j));
					nodes[i][j] = nd;
					if (nd.getP().equals(from.getDirectP()))
						ndFrom = nd;
					else if (nd.getP().equals(to.getDirectP()))
						ndTo = nd;
				}
				j++;
			}
			i++;
		}
	}

	public void draw(Graphics2D g, Color color) {
		drawHandle(g);
		if (ndFrom == null || ndTo == null || ndTo.getFrom() == null) {
			drawLine(g, from.getXY(), from.getDirectP());
			drawLine(g, to.getDirectP(), to.getXY());
			drawLine(g, from.getDirectP(), to.getDirectP());
			return;
		}
		drawLine(g, from.getXY(), from.getDirectP());
		drawLine(g, to.getXY(), to.getDirectP());
		Node crt = ndTo;
		Node last = null;
		while (crt != null && !crt.equals(ndFrom)) {
			if (last != null) {
				g.drawLine(last.getP().x, last.getP().y, crt.getP().x, crt
						.getP().y);
				if (Node.dist(last.getP(), crt.getP()) > 80)
					hvLines.add(new HVLine(last.getP(), crt.getP()));
			}
			last = crt;
			crt = crt.getFrom();
		}
		if (crt != null)
			g
					.drawLine(last.getP().x, last.getP().y, crt.getP().x, crt
							.getP().y);
	}

	private void expand(Node crt) {
		while (crt != null && !crt.isVisited()) {
			crt.setVisited(true);

			for (Cord c : crt.getNb()) {
				if (c == null)
					continue;
				Node nd = nodes[c.i][c.j];
				if (nd == null)
					continue;
				Integer dist = nd.costFrom(crt, panel, to.getDirectP());
				if (dist == null)
					continue;
				int nc = crt.getCost() + dist;
				if (nd.getCost() == null || nd.getCost() > nc) {
					nd.update(crt, nc);
				}
			}

			crt = getNextToExpand();
		}
	}

	/**
	 * @return
	 * @uml.property  name="hvLines"
	 */
	public List<HVLine> getHvLines() {
		return hvLines;
	}

	private Node getNextToExpand() {
		Integer bestCost = null;

		Node next = null;

		for (int i = 0; i < nodes.length; i++)
			for (int j = 0; j < nodes[i].length; j++) {
				Node nd = nodes[i][j];
				if (nd != null && !nd.isVisited()) {
					if (nd.getCost() == null)
						continue;
					if (bestCost == null || bestCost > nd.getCost()) {
						bestCost = nd.getCost();
						next = nd;
					}
				}
			}
		return next;
	}
}
