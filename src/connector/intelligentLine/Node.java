package connector.intelligentLine;

import java.awt.Point;

import component.Component;
import component.ElementPanel;

import connector.HVLine;

/**
 * @author  JiHsianLee
 */
public class Node {
	private Point p;
	private Integer cost;
	private Node from;
	private boolean visited;
	private Cord ij;
	private int turn;

	/**
	 * @return
	 * @uml.property  name="turn"
	 */
	public int getTurn() {
		return turn;
	}

	/**
	 * @param turn
	 * @uml.property  name="turn"
	 */
	public void setTurn(int turn) {
		this.turn = turn;
	}

	public Node(Point p) {
		this.p = p;
	}

	/**
	 * @return
	 * @uml.property  name="cost"
	 */
	public Integer getCost() {
		return cost;
	}

	/**
	 * @param cost
	 * @uml.property  name="cost"
	 */
	public void setCost(Integer cost) {
		this.cost = cost;
	}

	/**
	 * @return
	 * @uml.property  name="from"
	 */
	public Node getFrom() {
		return from;
	}

	/**
	 * @param from
	 * @uml.property  name="from"
	 */
	public void setFrom(Node from) {
		this.from = from;
	}

	/**
	 * @return
	 * @uml.property  name="visited"
	 */
	public boolean isVisited() {
		return visited;
	}

	/**
	 * @param visited
	 * @uml.property  name="visited"
	 */
	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	/**
	 * @return
	 * @uml.property  name="p"
	 */
	public Point getP() {
		return p;
	}

	/**
	 * @return
	 * @uml.property  name="ij"
	 */
	public Cord getIj() {
		return ij;
	}

	/**
	 * @param ij
	 * @uml.property  name="ij"
	 */
	public void setIj(Cord ij) {
		if (this.ij == null)
			this.ij = ij;
		else
			throw new RuntimeException("ij cannot be changed!");
	}

	@Override
	public boolean equals(Object o) {
		Node nd = (Node) o;
		return p.equals(nd.getP());
	}

	public Cord[] getNb() {
		return new Cord[] { ij.left(), ij.right(), ij.top(), ij.bottom() };
	}

	public boolean isTurn(Node nd) {
		Node from = nd.from;
		if (from == null)
			return false;
		return !(p.x == from.p.x || p.y == from.p.y);
	}

	public void update(Node crt, int nc) {
		this.setCost(nc);
		this.setFrom(crt);
		if (this.isTurn(crt))
			this.setTurn(crt.getTurn() + 1);
		else
			this.setTurn(crt.getTurn());
		this.setVisited(false);
	}

	public Integer costFrom(Node from, ElementPanel panel, Point target) {
		for (Component c : panel.getElements())
			if (c.intersects(p, from.p))
				return null;
		if (panel.getHvLines() != null && Node.dist(p, from.p) > 20)
			for (HVLine hvl : panel.getHvLines())
				if (hvl.contains(p, from.p))
					return null;

		int vDist = Math.abs(p.y - from.p.y);
		int hDist = Math.abs(p.x - from.p.x);

		int turn = from.getTurn();

		if (isTurn(from))
			turn++;

		int tDist = turn * dist(p, target);

		return vDist + hDist + tDist;
	}

	public static int dist(Point p1, Point p2) {
		int vDist = Math.abs(p1.y - p2.y);
		int hDist = Math.abs(p1.x - p2.x);
		return vDist + hDist;
	}

	public String toString() {
		return ij.toString();
	}
}
