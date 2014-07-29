package component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import model.ConnectException;
import connector.Connector;
import connector.InputConnector;
import connector.OutputConnector;

/**
 * @author jyk
 */
@SuppressWarnings("serial")
public abstract class Component implements Serializable {
	/**
	 * @author jyk
	 */
	public enum Position {
		bottom, left, right, top
	}

	public static Point getMidPoint(Component e1, Component e2) {
		Point mp1 = new Point(e1.x + e1.width / 2, e1.y + e1.height / 2);
		Point mp2 = new Point(e2.x + e2.width / 2, e2.y + e2.height / 2);

		int mx = (mp1.x + mp2.x) / 2;
		int my = (mp1.y + mp2.y) / 2;

		return new Point(mx, my);
	}

	/**
	 * @uml.property name="connectors"
	 */
	private List<Connector> connectors = new ArrayList<Connector>();

	private int connectorSize = 10;

	/**
	 * @uml.property name="height"
	 */
	protected int height;

	transient private ElementMenu menu;

	/**
	 * @uml.property name="name"
	 */
	private String name;

	private Rectangle outer;

	transient private ElementPanel panel;

	public abstract void recreate();

	/**
	 * @uml.property name="selected"
	 */
	private boolean selected;

	// private Connector selectedConnector;

	private Rectangle self;
	/**
	 * @uml.property name="width"
	 */
	protected int width;

	/**
	 * @uml.property name="x"
	 */
	protected int x = 50;

	/**
	 * @uml.property name="y"
	 */
	protected int y = 50;

	public Component(int width, int height, String name) {
		this.width = width;
		this.height = height;
		this.name = name;
		setXY(50, 50);
		connectorSize = Math.min(width, height) / 5;
		if (connectorSize > 10)
			connectorSize = 10;
	}

	public boolean contains(Point p) {
		return self.contains(p);
	}

	public void disconnect() {
		menu.setVisible(false);

		for (Connector c : connectors) {
			c.disconnect();
		}
	}

	public void draw(Graphics2D g) {
		if (selected)
			g.setStroke(new BasicStroke(2f));
		else
			g.setStroke(new BasicStroke(1f));

		drawComponentBody(g);

		drawComponentName(g);

		if (selected)
			g.setStroke(new BasicStroke(1f));

		drawConnectors(g);
		g.setColor(Color.black);
	}

	protected abstract void drawComponentBody(Graphics2D g);

	protected void drawComponentName(Graphics2D g) {
		if (name != null) {
			g.setColor(Color.blue);
			int fontSize = (int) ((width * 1) / name.length());
			if (fontSize > 12)
				fontSize = 12;
			g.setFont(new Font(Font.DIALOG, Font.PLAIN, fontSize));
			float px, py;
			FontMetrics fm = g.getFontMetrics();
			px = (int) (width - fm.stringWidth(name)) / 2;
			py = (int) (height - fm.getHeight()) / 2;

			int xx = (int) (x);
			int yy = (int) (y);
			g.drawString(name, xx + px, yy + py + fm.getAscent());
		}
	}

	private void drawConnectors(Graphics2D g) {
		for (Connector c : connectors) {
			Color cSave = g.getColor();
			Stroke sSave = g.getStroke();
			if (c.isSelected()) {
				g.setColor(Color.red);
				g.setStroke(new BasicStroke(2f));
			}
			if (c instanceof InputConnector) {
				drawInput(g, (InputConnector) c);
			} else {
				drawOutput(g, (OutputConnector) c);
			}
			g.setColor(cSave);
			g.setStroke(sSave);
			c.drawName(g, connectorSize);
		}
	}

	public void drawInput(Graphics2D g, InputConnector ip) {
		int[] xs = null, ys = null;
		if (ip.pos == Position.left) {
			xs = new int[] { x - connectorSize / 2, x + connectorSize / 2,
					x - connectorSize / 2 };
			ys = new int[] { y + height / 2 - connectorSize / 2,
					y + height / 2, y + height / 2 + connectorSize / 2 };
		} else if (ip.pos == Position.top) {
			xs = new int[] { x + width / 2 - connectorSize / 2, x + width / 2,
					x + width / 2 + connectorSize / 2 };
			ys = new int[] { y - connectorSize / 2, y + connectorSize / 2,
					y - connectorSize / 2 };
		} else if (ip.pos == Position.bottom) {
			xs = new int[] { x + width / 2 - connectorSize / 2, x + width / 2,
					x + width / 2 + connectorSize / 2 };
			ys = new int[] { y + height + connectorSize / 2,
					y + height - connectorSize / 2,
					y + height + connectorSize / 2 };
		} else {
			xs = new int[] { x + width + connectorSize / 2,
					x + width - connectorSize / 2,
					x + width + connectorSize / 2 };
			ys = new int[] { y + height / 2 - connectorSize / 2,
					y + height / 2, y + height / 2 + connectorSize / 2 };
		}
		if (xs != null && ys != null)
			g.drawPolygon(xs, ys, 3);
	}

	public void drawOutput(Graphics2D g, OutputConnector op) {
		int[] xs = null, ys = null;
		if (op.pos == Position.right) {
			xs = new int[] { x + width - connectorSize / 2,
					x + width + connectorSize / 2,
					x + width - connectorSize / 2 };
			ys = new int[] { y + height / 2 - connectorSize / 2,
					y + height / 2, y + height / 2 + connectorSize / 2 };
		} else if (op.pos == Position.top) {
			xs = new int[] { x + width / 2 - connectorSize / 2, x + width / 2,
					x + width / 2 + connectorSize / 2 };
			ys = new int[] { y + connectorSize / 2, y - connectorSize / 2,
					y + connectorSize / 2 };
		} else if (op.pos == Position.bottom) {
			xs = new int[] { x + width / 2 - connectorSize / 2, x + width / 2,
					x + width / 2 + connectorSize / 2 };
			ys = new int[] { y + height - connectorSize / 2,
					y + height + connectorSize / 2,
					y + height - connectorSize / 2 };
		} else {
			xs = new int[] { x + connectorSize / 2, x - connectorSize / 2,
					x + connectorSize / 2 };
			ys = new int[] { y + height / 2 - connectorSize / 2,
					y + height / 2, y + height / 2 + connectorSize / 2 };
		}
		if (xs != null && ys != null)
			g.drawPolygon(xs, ys, 3);
	}

	public abstract void execute(double t);

	public boolean farAway(Point p) {
		return !outer.contains(p);
	}

	public void fireDefaultAction(Point mousePoint) {
		if (menu == null) {
			menu = new ElementMenu(this);
			menu.setPanel(panel);
		}

		menu.fireDflt(mousePoint);
	}

	public void flipH() {
		for (Connector c : connectors) {
			if (c.getPosition() == Position.left)
				c.setPosition(Component.Position.right);
			else if (c.getPosition() == Position.right)
				c.setPosition(Position.left);
		}
	}

	public void flipV() {
		for (Connector c : connectors) {
			if (c.getPosition() == Position.top)
				c.setPosition(Component.Position.bottom);
			else if (c.getPosition() == Position.bottom)
				c.setPosition(Position.top);
		}
	}

	/**
	 * @return
	 * @uml.property name="connectors"
	 */
	public List<Connector> getConnectors() {
		return connectors;
	}

	public Point[] getCrticialPoints() {
		Point left = getPosCritical(Position.left);
		Point right = getPosCritical(Position.right);
		Point top = getPosCritical(Position.top);
		Point bottom = getPosCritical(Position.bottom);
		return new Point[] { left, new Point(left.x - getHeight() / 2, left.y),
				right, new Point(right.x + getHeight() / 2, right.y), top,
				new Point(top.x, top.y - getHeight() / 2), bottom,
				new Point(bottom.x, bottom.y + getHeight() / 2) };
	}

	/**
	 * @return
	 * @uml.property name="height"
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	public int getOHeight() {
		return outer.height;
	}

	public int getOWidth() {
		return outer.width;
	}

	/**
	 * @return
	 * @uml.property name="panel"
	 */
	public ElementPanel getPanel() {
		return panel;
	}

	public Point getPosCritical(Component.Position pos) {
		Point p = new Point(getX(), getY());

		Point p2;
		if (pos == Component.Position.right)
			p2 = new Point(p.x + getWidth() + getHeight() / 4 + 1, p.y
					+ getHeight() / 2);
		else if (pos == Component.Position.left)
			p2 = new Point(p.x - getHeight() / 4 - 1, p.y + getHeight() / 2);
		else if (pos == Component.Position.top)
			p2 = new Point(p.x + getWidth() / 2, p.y - getHeight() / 4 - 1);
		else
			p2 = new Point(p.x + getWidth() / 2, p.y + getHeight()
					+ getHeight() / 4 + 1);
		return p2;
	}

	/**
	 * @return
	 * @uml.property name="width"
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return
	 * @uml.property name="x"
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return
	 * @uml.property name="y"
	 */
	public int getY() {
		return y;
	}

	public boolean intersects(Point p1, Point p2) {
		if (outer.contains(p1) || outer.contains(p2))
			return true;

		int xMin = Math.min(p1.x, p2.x);
		int xMax = Math.max(p1.x, p2.x);
		int yMin = Math.min(p1.y, p2.y);
		int yMax = Math.max(p1.y, p2.y);

		if (xMin == xMax) {
			if (outer.x < xMin && xMin < outer.x + outer.width)
				if (yMin < outer.y && yMax >= outer.y + outer.height)
					return true;
		} else {
			if (outer.y < yMin && yMin < outer.y + outer.height)
				if (xMin < outer.x && xMax >= outer.x + outer.width)
					return true;
		}
		return false;
	}

	public boolean intersects2(Point p1, Point p2) {
		if (self.contains(p1) || self.contains(p2))
			return true;

		int xMin = Math.min(p1.x, p2.x);
		int xMax = Math.max(p1.x, p2.x);
		int yMin = Math.min(p1.y, p2.y);
		int yMax = Math.max(p1.y, p2.y);

		if (xMin == xMax) {
			if (self.x < xMin && xMin < self.x + self.width)
				if (yMin < self.y && yMax > self.y + self.height)
					return true;
		} else {
			if (self.y < yMin && yMin < self.y + self.height)
				if (xMin < self.x && xMax > self.x + outer.width)
					return true;
		}
		return false;
	}

	public boolean isInside(int mx, int my) {
		return (mx > x && mx < x + width) && (my > y && my < y + height);
	}

	/**
	 * @return
	 * @uml.property name="selected"
	 */
	public boolean isSelected() {
		return selected;
	}

	public void move(int dx, int dy) {
		setXY(x + dx, y + dy);
	}

	public void unRegisterAll() {
		connectors = new ArrayList<Connector>();
	}

	public void register(Connector c) {
		connectors.add(c);

		c.setParent(this);
	}

	public abstract void reset();

	public void scale(double s) {
		width = (int) (width * s);
		height = (int) (height * s);
		connectorSize = (int) (connectorSize * s);
	}

	public abstract void setDim(int dim) throws ConnectException;

	/**
	 * @param height
	 * @uml.property name="height"
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @param name
	 * @uml.property name="name"
	 */
	public void setName(String name) {
		this.name = name;
		if (panel != null)
			panel.repaint();
	}

	/**
	 * @param panel
	 * @uml.property name="panel"
	 */
	public void setPanel(ElementPanel panel) {
		this.panel = panel;
	}

	/**
	 * @param selected
	 * @uml.property name="selected"
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		if (menu != null)
			menu.setVisible(false);
	}

	/**
	 * @param width
	 * @uml.property name="width"
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;

		self = new Rectangle(x, y, width, height);
		outer = new Rectangle(x - height / 4, y - height / 4, width + height
				/ 2, height + height / 2);
	}

	public void showMenu(int x, int y) {
		menu = new ElementMenu(this);
		menu.setPanel(panel);

		menu.show(panel, x, y);
	}

	public int getConnectorSize() {
		return connectorSize;
	}
}
