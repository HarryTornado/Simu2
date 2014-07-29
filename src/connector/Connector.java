package connector;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;

import model.ConnectException;

import component.Component;
import component.Component.Position;

/**
 * @author jyk
 */
@SuppressWarnings("serial")
public abstract class Connector implements Serializable {
	private boolean selected;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean contains(Point p) {
		Point p0 = this.getXY();
		int s2 = this.getParent().getConnectorSize() / 2;
		return (p.x >= p0.x - s2 && p.x <= p0.x + s2 && p.y >= p0.y - s2 && p.y <= p0.y
				+ s2);
	}

	public boolean contact(Connector c2) {
		Component e = c2.getParent();
		int x = e.getX() - e.getHeight() / 4;
		int y = e.getY() - e.getHeight() / 4;
		int width = e.getWidth() + e.getHeight() / 2;
		int height = e.getHeight() + e.getHeight() / 2;
		Point p1 = new Point(x, y);
		Point p2 = new Point(x + width, y);
		Point p3 = new Point(x + width, y + height);
		Point p4 = new Point(x, y + height);
		if (this.passParent(p1, p2))
			return true;
		if (this.passParent(p2, p3))
			return true;
		if (this.passParent(p3, p4))
			return true;
		if (this.passParent(p4, p1))
			return true;
		return false;
	}

	public abstract void disconnect();

	public void drawName(Graphics2D g, int cSize) {
		Font font = g.getFont();
		Font nf = new Font(Font.DIALOG, Font.PLAIN, font.getSize() * 3 / 4);
		g.setFont(nf);
		FontMetrics fm = g.getFontMetrics();
		Color cSave = g.getColor();
		int sWd = fm.stringWidth(this.getName());
		int sHt = fm.getAscent();

		Point p = this.getXY();
		int wd = cSize / 2 + 2;
		if (this.getPosition() == Position.left)
			p = new Point(p.x + wd, p.y + sHt / 2);
		else if (this.getPosition() == Position.right)
			p = new Point(p.x - wd - sWd, p.y + sHt / 2);
		else if (this.getPosition() == Position.top)
			p = new Point(p.x - sWd / 2, p.y + wd + fm.getAscent()
					- fm.getLeading());
		else
			p = new Point(p.x - sWd / 2, p.y - wd);
		g.setColor(Color.gray);
		g.drawString(this.getName(), p.x, p.y);
		g.setFont(font);
		g.setColor(cSave);
	}

	/**
	 * @return
	 * @uml.property name="dim"
	 */
	public abstract int getDim();

	public Point getDirectP() {
		Component e = getParent();
		Component.Position pos = this.getPosition();

		return e.getPosCritical(pos);
	}

	public abstract String getName();

	public abstract Component getParent();

	public abstract Component.Position getPosition();

	public Point getXY() {
		if (getPosition() == Position.left) {
			return new Point(getParent().getX(), getParent().getY()
					+ getParent().getHeight() / 2);
		} else if (getPosition() == Position.right) {
			return new Point(getParent().getX() + getParent().getWidth(),
					getParent().getY() + getParent().getHeight() / 2);
		} else if (getPosition() == Position.top) {
			return new Point(getParent().getX() + getParent().getWidth() / 2,
					getParent().getY());
		}
		return new Point(getParent().getX() + getParent().getWidth() / 2,
				getParent().getY() + getParent().getHeight());
	}

	public boolean isVertical() {
		return getPosition() == Component.Position.top
				|| getPosition() == Component.Position.bottom;
	}

	public boolean passParent(Point p1, Point p2) {
		Component e = getParent();
		int x = e.getX() - e.getHeight() / 4;
		int y = e.getY() - e.getHeight() / 4;
		int width = e.getWidth() + e.getHeight() / 2;
		int height = e.getHeight() + e.getHeight() / 2;
		if (p1.x == p2.x) {
			int miny = Math.min(p1.y, p2.y);
			int maxy = Math.max(p1.y, p2.y);
			return (p1.x > x && p1.x < x + width)
					&& (miny < y && maxy > y + height || miny > y
							&& miny < y + height || maxy >= y
							&& maxy < y + height);
		} else {
			int minx = Math.min(p1.x, p2.x);
			int maxx = Math.max(p1.x, p2.x);
			return (p1.y > y && p1.y < y + height)
					&& (minx < x && maxx > x + width || minx > x
							&& minx < x + width || maxx > x && maxx < x + width);
		}
	}

	/**
	 * @param dim
	 * @throws ConnectException
	 * @uml.property name="dim"
	 */
	public abstract void setDim(int dim) throws ConnectException;

	/**
	 * @param parent
	 * @uml.property name="parent"
	 */
	public abstract void setParent(Component parent);

	/**
	 * @param pos
	 * @uml.property name="position"
	 */
	public abstract void setPosition(Component.Position pos);
}
