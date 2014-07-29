package component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

import model.ConnectException;
import connector.DummyInputConnector;
import connector.DummyOutputConnector;
import connector.InputConnector;
import connector.OutputConnector;

@SuppressWarnings("serial")
public class SubSystem extends Component implements Editable {
	private static final Position[] iPoses = { Position.left, Position.top,
			Position.bottom, Position.right };

	private static final Position[] oPoses = { Position.right, Position.bottom,
			Position.top, Position.left };

	private transient int iNum, oNum;

	private ElementPanel subPanel = new ElementPanel();

	public SubSystem() {
		super(100, 50, " mod ");

		makeUStub();
		makeYStub();
		makeXStub();
	}

	private void addInputStub(InputStub is) {
		subPanel.add(is);
		InputConnector ic = is.getInput();
		Position pos = iPoses[iNum];
		DummyInputConnector dic = new DummyInputConnector(ic, pos);
		ic.setDic(dic);
		register(dic);
		iNum++;
	}

	private void addOutputStub(OutputStub os) {
		subPanel.add(os);
		OutputConnector oc = os.getOutput();
		Position pos = oPoses[oNum];
		DummyOutputConnector doc = new DummyOutputConnector(oc, pos);
		oc.setDoc(doc);
		try {
			doc.setDim(oc.getDim());
		} catch (ConnectException e) {
			e.printStackTrace();
		}
		register(doc);
		oNum++;
	}

	@Override
	protected void drawComponentBody(Graphics2D g) {
		int[] xx = { x, x + width / 6, x + width - width / 6, x + width,
				x + width, x + width - width / 6, x + width / 6, x };
		int[] yy = { y + height / 6, y, y, y + height / 6,
				y + height - height / 6, y + height, y + height,
				y + height - height / 6 };
		g.drawPolygon(xx, yy, xx.length);
		Stroke strokeSave = g.getStroke();
		g.setStroke(new BasicStroke(0.5f));
		g.drawPolygon(new int[] { x, x + width, x + width, x }, new int[] { y,
				y, y + height, y + height }, 4);
		g.setStroke(strokeSave);
	}

	@Override
	protected void drawComponentName(Graphics2D g) {
		if (getName() != null) {
			g.setColor(Color.blue);
			int fontSize = (int) ((width) / getName().length());
			if (fontSize > 16)
				fontSize = 16;
			g.setFont(new Font(Font.DIALOG, Font.PLAIN, fontSize));
			float px, py;
			FontMetrics fm = g.getFontMetrics();
			px = (int) (width - fm.stringWidth(getName())) / 2;
			py = (int) (height - fm.getHeight()) / 2;

			int xx = (int) (x);
			int yy = (int) (y);
			g.drawString(getName(), xx + px, yy + py + fm.getAscent());
		}
	}

	@Override
	public void execute(double t) {
		for (Component e : subPanel.getElements()) {
			if (e instanceof InputStub)
				e.execute(t);
		}
		for (Component e : subPanel.getElements()) {
			if (e instanceof DynaSys || e instanceof ApproxDif
					|| e instanceof Integration)
				e.execute(t);
		}
		for (Component e : subPanel.getElements()) {
			if (e instanceof FuncOne || e instanceof FuncTwo)
				e.execute(t);
		}
		for (Component e : subPanel.getElements()) {
			if (e instanceof Sub || e instanceof Add
					|| e instanceof Multiplexer || e instanceof Mult)
				e.execute(t);
		}
		for (Component e : subPanel.getElements()) {
			if (e instanceof Source)
				e.execute(t);
		}
		for (Component e : subPanel.getElements()) {
			if (e instanceof Plot || e instanceof StateSpaceTrajectory)
				e.execute(t);
		}
		for (Component e : subPanel.getElements()) {
			if (e instanceof OutputStub)
				e.execute(t);
		}
	}

	public ElementPanel getSubPanel() {
		return subPanel;
	}

	public void inherite() {
		subPanel.inheriteParams(getPanel().getParams());
	}

	private void makeUStub() {
		InputStub uStub = new InputStub();
		uStub.setName("u");
		uStub.setXY(10, 10);

		addInputStub(uStub);
	}

	private void makeXStub() {
		OutputStub xStub = new OutputStub();
		xStub.setName("x");
		xStub.setXY(300, 300);

		addOutputStub(xStub);
	}

	private void makeYStub() {
		OutputStub yStub = new OutputStub();
		yStub.setName("y");
		yStub.setXY(600, 10);

		addOutputStub(yStub);
	}

	@Override
	public void recreate() {
		for (Component cp : subPanel.getElements()) {
			cp.setSelected(false);
			cp.recreate();
			cp.setPanel(subPanel);
		}

	}

	@Override
	public void reset() {
		for (Component cp : subPanel.getElements())
			cp.reset();
	}

	public void setDim(int dim) throws ConnectException {
	}

	public void setDt(double dt) {
		for (Component e : subPanel.getElements()) {
			e.reset();
			if (e instanceof DynaSys)
				((DynaSys) e).setDt(dt);
			else if (e instanceof ApproxDif)
				((ApproxDif) e).setDt(dt);
			else if (e instanceof Integration)
				((Integration) e).setDt(dt);
		}
	}

	@Override
	public void setPanel(ElementPanel panel) {
		super.setPanel(panel);
		inherite();
	}

	@Override
	public void showDialog(Point point) {
		SubEditor se = new SubEditor(this);
		se.setVisible(true);
		this.setName(se.getModName());
		this.getPanel().repaint();
	}
}
