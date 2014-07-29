package connector.smartLine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import component.Component;

import connector.ConnectLine;
import connector.Connector;

/**
 * @author jyk
 */
public class SmartLine extends ConnectLine {
	private Point pFrom;
	private Point pTo;
	private Component eFrom;
	private Component eTo;
	private final int gap = 1;
	private Point pMid;

	public SmartLine(Connector from, Connector to) {
		super(from, to);
		pFrom = from.getDirectP();
		pTo = to.getDirectP();
		eFrom = from.getParent();
		eTo = to.getParent();
		pMid = Component.getMidPoint(eFrom, eTo);
	}

	public void draw(Graphics2D g, Color color) {
		g.setColor(color);
		drawHandle(g);

		if (directLineTo(g, from, pTo))
			return;
		if (directLineTo(g, to, pFrom))
			return;
		if (verticalLineTo(g, from, pTo))
			return;
		if (verticalLineTo(g, to, pFrom))
			return;

		if (bothVertical(g))
			return;

		if (bothHorizontal(g))
			return;

		if (from.isVertical()) {
			Point tp = new Point(getMx(), pFrom.y);
			Point tp2 = new Point(tp.x, pTo.y);
			if (!(eFrom.intersects(pFrom, tp) || eTo.intersects(pFrom, tp))
					|| eFrom.intersects(tp, tp2) || eTo.intersects(tp, tp2)
					|| eFrom.intersects(tp2, pTo) || eTo.intersects(tp2, pTo)) {
				drawLine(g, pFrom, tp);
				drawLine(g, tp, tp2);
				drawLine(g, tp2, pTo);
				return;
			} else {
				int mx = 0;
				if (to.getPosition() == Component.Position.left) {
					mx = Math.min(eFrom.getX() - gap, pTo.x);
				} else {
					mx = Math
							.max(eFrom.getX() + eFrom.getOWidth() + gap, pTo.x);
				}
				tp = new Point(mx, pFrom.y);
				tp2 = new Point(mx, pTo.y);
				drawLine(g, tp2, pTo);
				drawLine(g, pFrom, tp);
				drawLine(g, tp, tp2);
				return;
			}
		} else {
			Point tp = new Point(pFrom.x, getMy());
			Point tp2 = new Point(pTo.x, tp.y);
			if (!eTo.intersects(tp2, pTo)) {
				drawLine(g, tp2, pTo);
				drawLine(g, pFrom, tp);
				drawLine(g, tp, tp2);
				return;
			} else {
				int my = 0;
				if (to.getPosition() == Component.Position.top) {
					my = Math.min(pTo.y, eFrom.getY() - gap);
				} else {
					my = Math.max(pTo.y, eFrom.getY() + eFrom.getOHeight()
							+ gap);
				}
				tp = new Point(pFrom.x, my);
				tp2 = new Point(pTo.x, tp.y);
				drawLine(g, tp2, pTo);
				drawLine(g, pFrom, tp);
				drawLine(g, tp, tp2);
				return;
			}
		}

		// drawLine(g, pFrom, pTo);
	}

	private int getMx() {
		int mx = 0;
		if (pFrom.x >= pTo.x) {
			if (pMid.x < eFrom.getX() - gap
					&& pMid.x > eTo.getX() + eTo.getOWidth() + gap)
				mx = pMid.x;
			else
				mx = Math.max(pFrom.x + eFrom.getOWidth() / 2 + gap, pTo.x
						+ eTo.getOWidth() / 2 + gap);
		} else {
			if (pMid.x > eFrom.getX() + eFrom.getOWidth() + gap
					&& pMid.x < eTo.getX() - gap)
				mx = pMid.x;
			else

				mx = Math.min(pFrom.x - (eFrom.getOWidth() / 2 + gap), pTo.x
						- (eTo.getOWidth() / 2 + gap));
		}
		return mx;
	}

	private boolean bothHorizontal(Graphics2D g) {
		if (!from.isVertical() && !to.isVertical()) {
			int my = getMy();
			Point tp = new Point(pFrom.x, my);
			drawLine(g, pFrom, tp);
			Point tp2 = new Point(pTo.x, tp.y);
			drawLine(g, tp, tp2);
			drawLine(g, tp2, pTo);
			return true;
		}
		return false;
	}

	private int getMy() {
		int my = 0;
		if (pFrom.y >= pTo.y) {
			if (pMid.y < eFrom.getY() - gap
					&& pMid.y > eTo.getY() + eTo.getOHeight() + gap)
				my = pMid.y;
			else
				my = Math.max(pFrom.y + eFrom.getOHeight() / 2 + gap, pTo.y
						+ eTo.getOHeight() / 2 + gap);
		} else {
			if (pMid.y > eFrom.getY() + eFrom.getOHeight() + gap
					&& pMid.y < eTo.getY() - gap)
				my = pMid.y;
			else
				my = Math.min(pFrom.y - (eFrom.getOHeight() / 2 + gap), pTo.y
						- (eTo.getOHeight() / 2 + gap));
		}
		return my;
	}

	private boolean bothVertical(Graphics2D g) {
		if (from.isVertical() && to.isVertical()) {
			int mx = getMx();
			Point tp = new Point(mx, pFrom.y);
			drawLine(g, pFrom, tp);
			Point tp2 = new Point(tp.x, pTo.y);
			drawLine(g, tp, tp2);
			drawLine(g, tp2, pTo);
			return true;
		}
		return false;
	}

	private boolean directLineTo(Graphics2D g, Connector c, Point ep) {
		Point p = c.getDirectP();

		Point tp;
		if (c.isVertical())
			tp = new Point(p.x, ep.y);
		else
			tp = new Point(ep.x, p.y);
		if (eFrom.intersects(tp, p) || eTo.intersects(tp, p)
				|| eFrom.intersects(tp, ep) || eTo.intersects(tp, ep))
			return false;
		drawLine(g, p, tp);
		drawLine(g, tp, ep);
		return true;
	}

	private boolean verticalLineTo(Graphics2D g, Connector c, Point ep) {
		Point p = c.getDirectP();

		Point tp;
		if (c.isVertical())
			tp = new Point(ep.x, p.y);
		else
			tp = new Point(p.x, ep.y);
		if (eFrom.intersects(tp, p) || eTo.intersects(tp, p)
				|| eFrom.intersects(tp, ep) || eTo.intersects(tp, ep))
			return false;
		drawLine(g, p, tp);
		drawLine(g, tp, ep);
		return true;
	}
}
