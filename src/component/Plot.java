package component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

import javax.swing.JDialog;

import model.Hist;
import model.NameEditor;
import net.effortech.harry.graph.FigurePanel;
import Jama.Matrix;
import connector.InputConnector;

@SuppressWarnings("serial")
public class Plot extends Component implements Editable {
	transient private FigurePanel fp;
	transient private Hist hist;
	private InputConnector input;
	private double maxv;

	transient private JDialog result;

	public Plot() {
		super(70, 50, "plot");

		input = new InputConnector("u", Component.Position.bottom);

		register(input);
		reset();
	}

	@Override
	protected void drawComponentBody(Graphics2D g) {
		int w = 0, h = 0;
		g.drawRect(x, y, width, height);
		w = width / 10;
		h = height / 10;
		Stroke save = g.getStroke();
		g.setStroke(new BasicStroke(.3f));
		g.drawRect(x + 2, y + 2, width - 4, height - 4);

		g.drawRect(x - 1 + (int) w, y - 1 + (int) h, (int) (width - 2 * w),
				(int) (height - 3.6 * h));
		g.drawRect(x + 1 + (int) w, y + 1 + (int) h, (int) (width - 2 * w),
				(int) (height - 3.6 * h));
		g.setStroke(save);
	}

	@Override
	protected void drawComponentName(Graphics2D g) {
		String name = getName();
		if (name != null) {
			g.setColor(Color.blue);
			int fontSize = (int) ((width) / name.length());
			if (fontSize > 16)
				fontSize = 16;
			g.setFont(new Font(Font.DIALOG, Font.PLAIN, fontSize));
			float px, py;
			FontMetrics fm = g.getFontMetrics();
			int h = height / 10;
			px = (int) (width - fm.stringWidth(name)) / 2;
			py = h + (int) (height - 3.6 * h - fm.getHeight()) / 2;

			int xx = (int) (x);
			int yy = (int) (y);
			g.drawString(name, xx + px, yy + py + fm.getAscent());
		}
	}

	@Override
	public void execute(double t) {
		if (hist != null && input.isConnected()) {
			Matrix u = input.getInput();
			hist.store(u, t);
			double v = u.norm1();
			if (v > maxv)
				maxv = v;
		}
	}

	private FigurePanel getFP() {
		if (fp == null) {
			if (hist != null) {
				hist.makeFp();
				fp = hist.getFigurePanel();
				fp.setMark(true);
				fp.setTitle(getName());
			}
		}
		return fp;
	}

	@Override
	public void reset() {
		fp = null;
		if (input.isConnected()) {
			if (input.getSource() != null
					&& input.getSource().getParent() != null) {
				this.setName("plot of "
						+ input.getSource().getParent().getName() + "."
						+ input.getSource().getName());
				hist = new Hist(input.getSource().getParent().getName(), input
						.getSource().getName()
						+ " trajectory", input.getSource().getName(), 1f);
				hist.setOrder(input.getDim());
			} else {
				this.setName("plot");
				hist = null;
			}
		}
		result = null;
		maxv = 0;
	}

	@Override
	public void setDim(int dim) {
		reset();
	}

	@Override
	public void showDialog(Point point) {
		NameEditor editor = new NameEditor("Name of the plotter", "Name", this
				.getName());
		editor.showDialog("input dim: " + input.getDim() + ", input max norm: "
				+ maxv);
		if (editor.isOk()) {
			setName(editor.getName());
		}
	}

	public void showResult() {
		if (result == null) {
			result = new JDialog();
			result.setTitle(this.getName());
			result.setModal(false);
			FigurePanel fp = getFP();
			if (fp != null)
				result.add(fp);
			result.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			result.setSize(800, 500);
			result.setLocationRelativeTo(null);
		}
		result.setVisible(true);
	}

	@Override
	public void recreate() {
		// TODO Auto-generated method stub

	}
}
