package connector;

import java.awt.Point;

public class HVLine {
	public final Point ps;
	public final Point pe;
	private int minX;
	private int minY;
	private int maxX;
	private int maxY;

	public HVLine(Point p1, Point p2) {
		this.ps = p1;
		this.pe = p2;
		minY = Math.min(ps.y, pe.y);
		maxY = Math.max(ps.y, pe.y);
		minX = Math.min(ps.x, pe.x);
		maxX = Math.max(ps.x, pe.x);
	}

	public boolean contains(Point p1, Point p2) {
		if (ps.x == pe.x) {
			if (p1.x == p2.x && p1.x == ps.x) {
				minY = Math.min(p1.y, p2.y);
				maxY = Math.max(p1.y, p2.y);
				if (minY < this.minY && maxY > this.maxY)
					return true;
				if (this.minY < minY && minY < this.maxY)
					return true;
				if (maxY > this.minY && maxY < this.maxY)
					return true;
			}
		} else {
			if (p1.y == p2.y && p1.y == ps.y) {
				minX = Math.min(p1.x, p2.x);
				maxX = Math.max(p1.x, p2.x);
				if (minX < this.minX && maxX > this.maxX)
					return true;
				if (this.minX <= minX && minX < this.maxX)
					return true;
				if (maxX > this.minX && maxX < this.maxX)
					return true;
			}
		}
		return false;
	}
}
