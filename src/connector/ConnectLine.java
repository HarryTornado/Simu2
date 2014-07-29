package connector;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * @author  JiHsianLee
 */
public abstract class ConnectLine {
	protected Connector from;
	protected Connector to;

	public ConnectLine(Connector from, Connector to) {
		this.from = from;
		this.to = to;
	}

	public abstract void draw(Graphics2D g, Color color);

	protected void drawHandle(Graphics2D g) {
		drawLine(g, from.getXY(), from.getDirectP());
		drawLine(g, to.getXY(), to.getDirectP());
	}

	public static void drawLine(Graphics2D g, Point p1, Point p2) {
		g.drawLine(p1.x, p1.y, p2.x, p2.y);
	}
}
